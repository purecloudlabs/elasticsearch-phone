package tests;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.plugins.PluginsService;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Before;
import org.junit.Test;

@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE)
public class PhoneSearchIntegrationTest extends ElasticsearchIntegrationTest {
    
    static {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }
    
    private List<String> analyzers;
    
    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        analyzers = Arrays.asList("phone", "phone-email");
        createIndex("test");
        ensureGreen("test");
        final XContentBuilder mapping = jsonBuilder().startObject().startObject("type").startObject("properties");
        for (String analyzer : analyzers) {
            mapping.startObject(analyzer).field("type", "string").field("analyzer", analyzer).field("search_analyzer", "phone-search").endObject();
        }
        mapping.endObject().endObject().endObject();
        
        client().admin().indices().preparePutMapping("test").setType("type").setSource(mapping).get();
        ensureGreen("test");
        Locale.setDefault(new Locale("en_US"));
    }
    
    @Override
    protected Settings nodeSettings(int nodeOrdinal) {
        org.elasticsearch.common.settings.ImmutableSettings.Builder builder =
                ImmutableSettings.builder().put(super.nodeSettings(nodeOrdinal)).put("plugins." + PluginsService.LOAD_PLUGIN_FROM_CLASSPATH, true);
        return builder.build();
    }
    
    @Test
    public void testSipUri_ExactMatch() throws ExecutionException, InterruptedException, IOException {
        assertTrue(verifyMatch("sip:+441344840400@domain.com", "sip:+441344840400@domain.com", "phone"));
        assertTrue(verifyMatch("sip:+441344840400@domain.com", "sip:+441344840400@domain.com", "phone-email"));
    }
    
    @Test
    public void testSipUri_WithoutSipPrefix() throws ExecutionException, InterruptedException, IOException {
        assertTrue(verifyMatch("sip:+441344840400@domain.com", "441344840400@domain.com", "phone"));
        assertTrue(verifyMatch("sip:+441344840400@domain.com", "441344840400@domain.com", "phone-email"));
    }
    
    @Test
    public void testSipUri_PrefixMatch() throws ExecutionException, InterruptedException, IOException {
        assertTrue(verifyMatch("sip:+441344840400@domain.com", "+441344", "phone"));
        assertTrue(verifyMatch("sip:+441344840400@domain.com", "+441344", "phone-email"));
    }
    
    @Test
    public void testSipUri_SipPrefixMatch() throws ExecutionException, InterruptedException, IOException {
        assertTrue(verifyMatch("sip:+441344840400@domain.com", "sip:+441344", "phone"));
        assertTrue(verifyMatch("sip:+441344840400@domain.com", "sip:+441344", "phone-email"));
    }
    
    @Test
    public void testTel_TelPrefixMatch() throws ExecutionException, InterruptedException, IOException {
        assertTrue(verifyMatch("tel:+441344840400@domain.com", "tel:+441344", "phone"));
        assertTrue(verifyMatch("tel:+441344840400@domain.com", "tel:+441344", "phone-email"));
    }
    
    @Test
    public void testSipUri_PartialDomainNoMatch() throws ExecutionException, InterruptedException, IOException {
        assertFalse(verifyMatch("sip:+441344840400@domain.com", "441344840400@dom", "phone"));
        assertFalse(verifyMatch("sip:+441344840400@domain.com", "441344840400@dom", "phone-email"));
    }
    
    @Test
    public void testSipUri_NoMatchForTel() throws ExecutionException, InterruptedException, IOException {
        assertFalse(verifyMatch("sip:+441344840400@domain.com", "tel:+4413448", "phone"));
        assertFalse(verifyMatch("sip:+441344840400@domain.com", "tel:+4413448", "phone-email"));
    }
    
    @Test
    public void testEmail_FullMatch() throws ExecutionException, InterruptedException, IOException {
        assertTrue(verifyMatch("user.name@domain.com", "user.name@domain.com", "phone"));
        assertTrue(verifyMatch("user.name@domain.com", "user.name@domain.com", "phone-email"));
    }
    
    @Test
    public void testEmail_UserMatch1() throws ExecutionException, InterruptedException, IOException {
        assertTrue(verifyMatch("user.name@domain.com", "user.name", "phone"));
        assertTrue(verifyMatch("user.name@domain.com", "user.name", "phone-email"));
    }
    
    @Test
    public void testEmail_UserMatch2() throws ExecutionException, InterruptedException, IOException {
        assertFalse(verifyMatch("user.name@domain.com", "user", "phone"));
        assertTrue(verifyMatch("user.name@domain.com", "user", "phone-email"));
    }
    
    @Test
    public void testEmail_UserMatch3() throws ExecutionException, InterruptedException, IOException {
        assertFalse(verifyMatch("user.name@domain.com", "name", "phone"));
        assertTrue(verifyMatch("user.name@domain.com", "name", "phone-email"));
    }
    
    @Test
    public void testEmail_DomainMatch1() throws ExecutionException, InterruptedException, IOException {
        assertFalse(verifyMatch("user.name@domain.com", "domain.com", "phone"));
        assertTrue(verifyMatch("user.name@domain.com", "domain.com", "phone-email"));
    }
    
    @Test
    public void testEmail_DomainMatch2() throws ExecutionException, InterruptedException, IOException {
        assertFalse(verifyMatch("user.name@domain.com", "domain", "phone"));
        assertTrue(verifyMatch("user.name@domain.com", "domain", "phone-email"));
    }
    
    private boolean verifyMatch(String indexTerm, String searchTerm, String field) throws InterruptedException, ExecutionException {
        index("test", "type", "1", field, indexTerm);
        
        flush();
        refresh();
        SearchResponse sr = client().prepareSearch("test").setQuery(QueryBuilders.matchQuery(field, searchTerm).operator(Operator.AND)).execute().actionGet();
        return sr.getHits().getTotalHits() == 1;
    }
}

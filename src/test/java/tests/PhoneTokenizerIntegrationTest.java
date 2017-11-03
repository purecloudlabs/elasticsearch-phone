package tests;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.hamcrest.CoreMatchers.is;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.admin.cluster.node.info.NodesInfoResponse;
import org.elasticsearch.action.admin.cluster.node.info.PluginInfo;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse.AnalyzeToken;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.lang3.StringUtils;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.plugins.PluginsService;
import org.elasticsearch.test.ElasticsearchIntegrationTest;
import org.junit.Before;
import org.junit.Test;

@ElasticsearchIntegrationTest.ClusterScope(scope = ElasticsearchIntegrationTest.Scope.SUITE)
public class PhoneTokenizerIntegrationTest extends ElasticsearchIntegrationTest {
    
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
            mapping.startObject(analyzer).field("type", "string").field("analyzer", analyzer).endObject();
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
    public void testPluginIsLoaded() {
        NodesInfoResponse infos = client().admin().cluster().prepareNodesInfo().setPlugins(true).execute().actionGet();
        boolean found = false;
        for (PluginInfo pluginInfo : infos.getNodes()[0].getPlugins().getInfos()) {
            if ("phone-plugin".equals(pluginInfo.getName())) {
                found = true;
            }
        }
        assertTrue(found);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Phone numbers/SIP URIs, both the phone and phone-email analyzers should tokenize the phone number.
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    
    @Test
    public void testEurope() throws ExecutionException, InterruptedException, IOException {
        assertIncludes("tel:+441344840400", Arrays.asList("44", "1344", "1344840400", "441344840400"));
    }
    
    @Test
    public void testGermanCastle() throws ExecutionException, InterruptedException, IOException {
        assertIncludes("tel:+498362930830", Arrays.asList("49", "498362930830", "8362930830"));
    }
    
    @Test
    public void testBMWofSydney() throws ExecutionException, InterruptedException, IOException {
        assertIncludes("tel:+61293344555", Arrays.asList("61", "293344555", "61293344555"));
    }
    
    @Test
    public void testCoffeeShopInIreland() throws ExecutionException, InterruptedException, IOException {
        assertIncludes("tel:+442890319416", Arrays.asList("44", "289", "2890319416", "442890319416"));
    }
    
    @Test
    public void testTelWithCountryCode() throws ExecutionException, InterruptedException, IOException {
        assertIncludes("tel:+17177158163", Arrays.asList("1", "717", "7177", "17177158163"));
    }
    
    @Test
    public void testTelWithCountryCode2() throws ExecutionException, InterruptedException, IOException {
        assertIncludes("tel:+12177148350", Arrays.asList("1", "217", "2177", "2177148350", "12177148350"));
    }
    
    @Test
    public void testNewTollFreeNumber() throws ExecutionException, InterruptedException, IOException {
        assertIncludes("tel:+18337148350", Arrays.asList("1", "833", "8337", "8337148350", "18337148350"));
    }
    
    @Test
    public void testMissingCountryCode() throws ExecutionException, InterruptedException, IOException {
        assertIncludes("tel:8177148350", Arrays.asList("817", "8177", "81771", "817714", "8177148350"));
    }
    
    @Test
    public void testSipWithNumericUsername() throws ExecutionException, InterruptedException, IOException {
        assertIncludes("sip:222@autosbcpc", Arrays.asList("222"));
    }
    
    @Test
    public void testTruncatedNumber() throws ExecutionException, InterruptedException, IOException {
        assertIncludes("tel:5551234", Arrays.asList("5551234"));
    }
    
    @Test
    public void testSipWithAlphabeticUsername() throws ExecutionException, InterruptedException, IOException {
        assertIncludes("sip:abc@autosbcpc", Arrays.asList("abc"));
    }
    
    @Test
    public void testGarbageInGarbageOut() throws ExecutionException, InterruptedException, IOException {
        assertIncludes("test", Arrays.asList("test"));
    }
    
    @Test
    public void testSipWithCountryCode() throws ExecutionException, InterruptedException, IOException {
        assertIncludes("sip:+14177141363@178.97.105.13;isup-oli=0;pstn-params=808481808882", Arrays.asList("417", "4177", "14177"));
    }
    
    @Test
    public void testSipWithTelephoneExtension() throws ExecutionException, InterruptedException, IOException {
        assertIncludes("sip:+13169410766;ext=2233@178.17.10.117:8060", Arrays.asList("316", "2233", "1316"));
    }
    
    @Test
    public void testSipWithUsername() throws ExecutionException, InterruptedException, IOException {
        assertIncludes("sip:JeffSIP@178.12.220.18", Arrays.asList("JeffSIP"));
    }
    
    @Test
    public void testPhoneNumberWithoutPrefix() throws ExecutionException, InterruptedException, IOException {
        assertIncludes("+14177141363", Arrays.asList("14177141363", "417", "4177", "14177"));
    }
    
    @Test
    public void testSipWithoutDomainPart() throws ExecutionException, InterruptedException, IOException {
        assertIncludes("sip:+122882", Arrays.asList("122882", "122", "228", "1228", "2288", "12288"));
    }
    
    @Test
    public void testTelPrefix() throws ExecutionException, InterruptedException, IOException {
        assertIncludes("tel:+1228", Arrays.asList("1228", "122", "228"));
    }
    
    @Test
    public void testNumberPrefix() throws ExecutionException, InterruptedException, IOException {
        assertIncludes("+1228", Arrays.asList("1228", "122", "228"));
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    // Email addresses (tokenized as email by the phone-email analyzer)
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    
    @Test
    public void testEmailSimple() throws ExecutionException, InterruptedException, IOException {
        assertIncludes("user@domain.com", Arrays.asList("user@domain.com", "user"), "phone");
        assertIncludes("user@domain.com", Arrays.asList("user@domain.com", "user", "domain.com", "domain"), "phone-email");
    }
    
    @Test
    public void testEmailWithNumber() throws ExecutionException, InterruptedException, IOException {
        assertIncludes("user123@domain.com", Arrays.asList("user123@domain.com", "user123"), "phone");
        assertIncludes("user123@domain.com", Arrays.asList("user123@domain.com", "user123", "user", "123", "domain.com", "domain"), "phone-email");
    }
    
    @Test
    public void testEmailUserNameWithMultipleParts() throws ExecutionException, InterruptedException, IOException {
        assertIncludes("user.name.with.parts@domain.com", Arrays.asList("user.name.with.parts@domain.com", "user.name.with.parts"), "phone");
        assertIncludes("user.name.with.parts@domain.com",
                Arrays.asList("user.name.with.parts@domain.com", "user.name.with.parts", "user", "name", "with", "parts", "domain.com", "domain"),
                "phone-email");
    }
    
    private void assertIncludes(String input, List<String> expectedTokens, String field) throws ExecutionException, InterruptedException, IOException {
        assertIncludes(input, expectedTokens, Arrays.asList(field));
    }
    
    private void assertIncludes(String input, List<String> expectedTokens) throws ExecutionException, InterruptedException, IOException {
        assertIncludes(input, expectedTokens, analyzers);
    }
    
    private void assertIncludes(String input, List<String> expectedTokens, List<String> fields) throws ExecutionException, InterruptedException, IOException {
        for (String field : fields) {
            AnalyzeResponse response = client().admin().indices().prepareAnalyze(input).setField(field).setIndex("test").execute().get();
            index("test", "type", "1", field, input);
            
            // Verify all the expected tokens are in there
            List<String> tokens = new ArrayList<String>();
            for (AnalyzeToken token : response.getTokens()) {
                assertFalse(StringUtils.isEmpty(token.getTerm()));
                tokens.add(token.getTerm());
            }
            
            flush();
            refresh();
            
            for (String expectedToken : expectedTokens) {
                assertTrue(tokens.contains(expectedToken));
                SearchResponse sr = client().prepareSearch("test").setQuery(QueryBuilders.termQuery(field, expectedToken)).execute().actionGet();
                assertThat(sr.getHits().getTotalHits(), is(1L));
                sr = client().prepareSearch("test").setQuery(QueryBuilders.termQuery(field, "bogussearchterm")).execute().actionGet();
                assertThat(sr.getHits().getTotalHits(), is(0l));
            }
        }
    }
}

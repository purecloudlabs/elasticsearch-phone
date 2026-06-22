package org.elasticsearch.index.analysis;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.junit.Test;

/**
 * Verifies search behavior: a value indexed with the {@code phone}/{@code phone_email} analyzer is
 * matched (or not) by a query analyzed with the {@code phone_search} analyzer. Ported from the
 * original PhoneSearchIntegrationTest; {@link #matches} mirrors a {@code match} query with the AND
 * operator (every search token must be present among the indexed tokens).
 */
public class PhoneSearchAnalyzerTest extends PhoneAnalysisTestCase {

    private void assertMatch(Analyzer index, String indexValue, String query) throws IOException {
        assertTrue("expected query '" + query + "' to match indexed value '" + indexValue + "'",
                matches(index, indexValue, query));
    }

    private void assertNoMatch(Analyzer index, String indexValue, String query) throws IOException {
        assertFalse("expected query '" + query + "' NOT to match indexed value '" + indexValue + "'",
                matches(index, indexValue, query));
    }

    @Test
    public void testSipUri_ExactMatch() throws IOException {
        assertMatch(phone, "sip:+441344840400@domain.com", "sip:+441344840400@domain.com");
        assertMatch(phoneEmail, "sip:+441344840400@domain.com", "sip:+441344840400@domain.com");
    }

    @Test
    public void testSipUri_WithoutSipPrefix() throws IOException {
        assertMatch(phone, "sip:+441344840400@domain.com", "441344840400@domain.com");
        assertMatch(phoneEmail, "sip:+441344840400@domain.com", "441344840400@domain.com");
    }

    @Test
    public void testSipUri_PrefixMatch() throws IOException {
        assertMatch(phone, "sip:+441344840400@domain.com", "+441344");
        assertMatch(phoneEmail, "sip:+441344840400@domain.com", "+441344");
    }

    @Test
    public void testSipUri_SipPrefixMatch() throws IOException {
        assertMatch(phone, "sip:+441344840400@domain.com", "sip:+441344");
        assertMatch(phoneEmail, "sip:+441344840400@domain.com", "sip:+441344");
    }

    @Test
    public void testTel_TelPrefixMatch() throws IOException {
        assertMatch(phone, "tel:+441344840400@domain.com", "tel:+441344");
        assertMatch(phoneEmail, "tel:+441344840400@domain.com", "tel:+441344");
    }

    @Test
    public void testSipUri_PartialDomainNoMatch() throws IOException {
        assertNoMatch(phone, "sip:+441344840400@domain.com", "441344840400@dom");
        assertNoMatch(phoneEmail, "sip:+441344840400@domain.com", "441344840400@dom");
    }

    @Test
    public void testSipUri_NoMatchForTel() throws IOException {
        assertNoMatch(phone, "sip:+441344840400@domain.com", "tel:+4413448");
        assertNoMatch(phoneEmail, "sip:+441344840400@domain.com", "tel:+4413448");
    }

    @Test
    public void testEmail_FullMatch() throws IOException {
        assertMatch(phone, "user.name@domain.com", "user.name@domain.com");
        assertMatch(phoneEmail, "user.name@domain.com", "user.name@domain.com");
    }

    @Test
    public void testEmail_UserMatch1() throws IOException {
        assertMatch(phone, "user.name@domain.com", "user.name");
        assertMatch(phoneEmail, "user.name@domain.com", "user.name");
    }

    @Test
    public void testEmail_UserMatch2() throws IOException {
        assertNoMatch(phone, "user.name@domain.com", "user");
        assertMatch(phoneEmail, "user.name@domain.com", "user");
    }

    @Test
    public void testEmail_UserMatch3() throws IOException {
        assertNoMatch(phone, "user.name@domain.com", "name");
        assertMatch(phoneEmail, "user.name@domain.com", "name");
    }

    @Test
    public void testEmail_DomainMatch1() throws IOException {
        assertNoMatch(phone, "user.name@domain.com", "domain.com");
        assertMatch(phoneEmail, "user.name@domain.com", "domain.com");
    }

    @Test
    public void testEmail_DomainMatch2() throws IOException {
        assertNoMatch(phone, "user.name@domain.com", "domain");
        assertMatch(phoneEmail, "user.name@domain.com", "domain");
    }
}

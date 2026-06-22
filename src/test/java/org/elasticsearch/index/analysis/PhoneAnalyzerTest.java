package org.elasticsearch.index.analysis;

import java.io.IOException;

import org.junit.Test;

/**
 * Verifies the index-time {@code phone} and {@code phone_email} analyzers tokenize phone numbers
 * and SIP URIs as expected. Ported from the original PhoneTokenizerIntegrationTest; both analyzers
 * are expected to produce these tokens for raw phone/SIP input.
 */
public class PhoneAnalyzerTest extends PhoneAnalysisTestCase {

    private void assertBoth(String input, String... expected) throws IOException {
        assertTokensInclude(phone, input, expected);
        assertTokensInclude(phoneEmail, input, expected);
    }

    @Test
    public void testEurope() throws IOException {
        assertBoth("tel:+441344840400", "44", "1344", "1344840400", "441344840400");
    }

    @Test
    public void testGermanCastle() throws IOException {
        assertBoth("tel:+498362930830", "49", "498362930830", "8362930830");
    }

    @Test
    public void testBMWofSydney() throws IOException {
        assertBoth("tel:+61293344555", "61", "293344555", "61293344555");
    }

    @Test
    public void testCoffeeShopInIreland() throws IOException {
        assertBoth("tel:+442890319416", "44", "289", "2890319416", "442890319416");
    }

    @Test
    public void testTelWithCountryCode() throws IOException {
        assertBoth("tel:+17177158163", "1", "717", "7177", "17177158163");
    }

    @Test
    public void testTelWithCountryCode2() throws IOException {
        assertBoth("tel:+12177148350", "1", "217", "2177", "2177148350", "12177148350");
    }

    @Test
    public void testNewTollFreeNumber() throws IOException {
        assertBoth("tel:+18337148350", "1", "833", "8337", "8337148350", "18337148350");
    }

    @Test
    public void testMissingCountryCode() throws IOException {
        assertBoth("tel:8177148350", "817", "8177", "81771", "817714", "8177148350");
    }

    @Test
    public void testSipWithNumericUsername() throws IOException {
        assertBoth("sip:222@autosbcpc", "222");
    }

    @Test
    public void testTruncatedNumber() throws IOException {
        assertBoth("tel:5551234", "5551234");
    }

    @Test
    public void testSipWithAlphabeticUsername() throws IOException {
        assertBoth("sip:abc@autosbcpc", "abc");
    }

    @Test
    public void testGarbageInGarbageOut() throws IOException {
        assertBoth("test", "test");
    }

    @Test
    public void testSipWithCountryCode() throws IOException {
        assertBoth("sip:+14177141363@178.97.105.13;isup-oli=0;pstn-params=808481808882", "417", "4177", "14177");
    }

    @Test
    public void testSipWithTelephoneExtension() throws IOException {
        assertBoth("sip:+13169410766;ext=2233@178.17.10.117:8060", "316", "2233", "1316");
    }

    @Test
    public void testSipWithUsername() throws IOException {
        assertBoth("sip:JeffSIP@178.12.220.18", "JeffSIP");
    }

    @Test
    public void testPhoneNumberWithoutPrefix() throws IOException {
        assertBoth("+14177141363", "14177141363", "417", "4177", "14177");
    }

    @Test
    public void testSipWithoutDomainPart() throws IOException {
        assertBoth("sip:+122882", "122882", "122", "228", "1228", "2288", "12288");
    }

    @Test
    public void testTelPrefix() throws IOException {
        assertBoth("tel:+1228", "1228", "122", "228");
    }

    @Test
    public void testNumberPrefix() throws IOException {
        assertBoth("+1228", "1228", "122", "228");
    }
}

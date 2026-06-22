package org.elasticsearch.index.analysis;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

/**
 * Pins the term-extraction logic that previously relied on commons-lang3 (the inlined
 * all-ASCII-digits ngram gate and the libphonenumber extension extraction).
 */
public class PhoneTermExtractorTest {

    private final PhoneTermExtractor extractor = new PhoneTermExtractor();

    @Test
    public void testNonNumericInputProducesNoNgrams() {
        List<String> tokens = extractor.extractTerms("abc");
        assertTrue("raw value should be present", tokens.contains("abc"));
        // The ngram loop must not run for non-digit input.
        assertFalse("must not ngram non-digit input", tokens.contains("a"));
        assertFalse("must not ngram non-digit input", tokens.contains("ab"));
    }

    @Test
    public void testCountryCodeNgrams() {
        List<String> tokens = extractor.extractTerms("tel:+12177148350");
        assertTrue(tokens.contains("1"));
        assertTrue(tokens.contains("217"));
        assertTrue(tokens.contains("2177"));
        assertTrue(tokens.contains("12177"));
    }

    @Test
    public void testExtensionIsExtracted() {
        List<String> tokens = extractor.extractTerms("sip:+13169410766;ext=2233@178.17.10.117:8060");
        assertTrue("phone extension should be a token", tokens.contains("2233"));
    }
}

package org.elasticsearch.plugins.analysis.phone;

import static org.junit.Assert.assertEquals;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

/** Locks in the analyzer and tokenizer names the plugin registers (referenced in index mappings). */
public class PhonePluginTest {

    private final PhonePlugin plugin = new PhonePlugin();

    @Test
    public void testRegisteredAnalyzerNames() {
        assertEquals(new TreeSet<>(Set.of("phone", "phone_search", "phone_email")),
                new TreeSet<>(plugin.getAnalyzers().keySet()));
    }

    @Test
    public void testRegisteredTokenizerNames() {
        assertEquals(new TreeSet<>(Set.of("phone_tokenizer", "phone_email_tokenizer", "phone_search_tokenizer")),
                new TreeSet<>(plugin.getTokenizers().keySet()));
    }
}

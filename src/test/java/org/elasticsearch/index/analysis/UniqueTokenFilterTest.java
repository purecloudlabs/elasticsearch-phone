package org.elasticsearch.index.analysis;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.tests.analysis.BaseTokenStreamTestCase;
import org.junit.Test;

public class UniqueTokenFilterTest extends BaseTokenStreamTestCase {

    /**
     * The vendored UniqueTokenFilter must drop duplicate tokens across the whole stream. Tokens are
     * collected directly rather than via assertAnalyzesTo, because TermExtractorTokenizer emits
     * synthetic tokens and does not track character offsets.
     */
    @Test
    public void testRemovesDuplicatesAcrossStream() throws IOException {
        final TermExtractor withDuplicates = input -> Arrays.asList("a", "b", "a", "c", "b", "a", "c");
        Tokenizer tokenizer = new TermExtractorTokenizer(withDuplicates);
        tokenizer.setReader(new StringReader("ignored"));

        List<String> out = new ArrayList<>();
        try (TokenStream ts = new UniqueTokenFilter(tokenizer)) {
            CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
            ts.reset();
            while (ts.incrementToken()) {
                out.add(term.toString());
            }
            ts.end();
        }

        assertEquals(Arrays.asList("a", "b", "c"), out);
    }
}

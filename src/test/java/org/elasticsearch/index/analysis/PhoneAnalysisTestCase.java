package org.elasticsearch.index.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.tests.analysis.BaseTokenStreamTestCase;

/**
 * Shared helpers for the phone/email analyzer unit tests. Exercises the analyzers directly via
 * Lucene's test framework so no Elasticsearch node is required.
 */
public abstract class PhoneAnalysisTestCase extends BaseTokenStreamTestCase {

    protected final Analyzer phone = new PhoneAnalyzer();
    protected final Analyzer phoneEmail = new PhoneEmailAnalyzer();
    protected final Analyzer phoneSearch = new PhoneSearchAnalyzer();

    @Override
    public void tearDown() throws Exception {
        phone.close();
        phoneEmail.close();
        phoneSearch.close();
        super.tearDown();
    }

    /** Runs {@code analyzer} over {@code text} and returns the produced tokens in order. */
    protected static List<String> tokens(Analyzer analyzer, String text) throws IOException {
        List<String> result = new ArrayList<>();
        try (TokenStream ts = analyzer.tokenStream("field", text)) {
            CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
            ts.reset();
            while (ts.incrementToken()) {
                String token = term.toString();
                assertFalse("analyzer produced an empty token for input: " + text, token.isEmpty());
                result.add(token);
            }
            ts.end();
        }
        return result;
    }

    protected void assertTokensInclude(Analyzer analyzer, String input, String... expected) throws IOException {
        List<String> actual = tokens(analyzer, input);
        for (String exp : expected) {
            assertTrue("expected token '" + exp + "' for input '" + input + "' but got " + actual, actual.contains(exp));
        }
    }

    /**
     * Mirrors a {@code match} query with the AND operator: the field matches the query iff every
     * token the search analyzer produces for the query is also an indexed token of the value.
     */
    protected boolean matches(Analyzer indexAnalyzer, String indexValue, String queryValue) throws IOException {
        List<String> indexed = tokens(indexAnalyzer, indexValue);
        List<String> query = tokens(phoneSearch, queryValue);
        return !query.isEmpty() && indexed.containsAll(query);
    }
}

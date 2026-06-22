package org.elasticsearch.index.analysis;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public final class TermExtractorTokenizer extends Tokenizer {

    private final List<TermExtractor> extractors;
    private Iterator<String> tokenIterator;
    private final CharTermAttribute term = addAttribute(CharTermAttribute.class);

    public TermExtractorTokenizer(TermExtractor... extractors) {
        this(Arrays.asList(extractors));
    }

    public TermExtractorTokenizer(List<TermExtractor> extractors) {
        this.extractors = Objects.requireNonNull(extractors, "extractors are required");
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        tokenIterator = null;
    }

    @Override
    public boolean incrementToken() throws IOException {
        clearAttributes();
        if (tokenIterator == null) {
            tokenIterator = getTokens().iterator();
        }
        if (tokenIterator.hasNext()) {
            term.append(tokenIterator.next());
            return true;
        }
        return false;
    }

    private List<String> getTokens() throws IOException {
        final List<String> tokens = new ArrayList<>();
        final String inputString = readFully(input);
        for (TermExtractor extractor : extractors) {
            tokens.addAll(extractor.extractTerms(inputString));
        }
        return tokens;
    }

    private static String readFully(Reader reader) throws IOException {
        final StringBuilder sb = new StringBuilder();
        final char[] buffer = new char[1024];
        int read;
        while ((read = reader.read(buffer)) != -1) {
            sb.append(buffer, 0, read);
        }
        return sb.toString();
    }
}

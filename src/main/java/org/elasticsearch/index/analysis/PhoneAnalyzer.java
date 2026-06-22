package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

public class PhoneAnalyzer extends Analyzer {

    @Override
    protected TokenStreamComponents createComponents(String field) {
        Tokenizer tokenizer = new TermExtractorTokenizer(new PhoneTermExtractor());
        return new TokenStreamComponents(tokenizer, new UniqueTokenFilter(tokenizer));
    }
}

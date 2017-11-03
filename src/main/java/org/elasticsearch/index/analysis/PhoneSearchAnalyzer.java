package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

public class PhoneSearchAnalyzer extends Analyzer {
    
    @Override
    protected TokenStreamComponents createComponents(String field) {
        Tokenizer tokenizer = new TermExtractorTokenizer(new PhoneSearchTermExtractor());
        return new TokenStreamComponents(tokenizer, tokenizer);
    }
}

package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.miscellaneous.UniqueTokenFilter;

public class PhoneAnalyzer extends Analyzer {
    
    @Override
    protected TokenStreamComponents createComponents(String field) {
        Tokenizer tokenizer = new TermExtractorTokenizer(new PhoneTermExtractor());
        return new TokenStreamComponents(tokenizer, new UniqueTokenFilter(tokenizer));
    }
}

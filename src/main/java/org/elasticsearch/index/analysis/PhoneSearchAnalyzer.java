package org.elasticsearch.index.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;

public class PhoneSearchAnalyzer extends Analyzer {
    
    @Override
    protected TokenStreamComponents createComponents(String field, Reader reader) {
        Tokenizer tokenizer = new TermExtractorTokenizer(reader, new PhoneSearchTermExtractor());
        return new TokenStreamComponents(tokenizer, new LowerCaseFilter(tokenizer));
    }
}

package org.elasticsearch.index.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

public class PhoneAnalyzer extends Analyzer {
	
    @Override
    protected TokenStreamComponents createComponents(String field, Reader reader) {
        Tokenizer tokenizer = new PhoneTokenizer(reader);
        return new TokenStreamComponents(tokenizer);
    }
}
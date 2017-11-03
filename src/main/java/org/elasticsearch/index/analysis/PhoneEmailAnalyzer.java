package org.elasticsearch.index.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.UniqueTokenFilter;

/**
 * Analyzer for fields that may contain email addresses or phone numbers.
 */
public class PhoneEmailAnalyzer extends Analyzer {
    
    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        TermExtractorTokenizer tokenizer = new TermExtractorTokenizer(reader, new PhoneTermExtractor(), new EmailTermExtractor());
        return new TokenStreamComponents(tokenizer, new UniqueTokenFilter(tokenizer));
    }
}

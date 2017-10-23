package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

public class PhoneEmailTokenizerFactory extends AbstractTokenizerFactory {

    public PhoneEmailTokenizerFactory(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(indexSettings, name, settings);
    }

    @Override
    public Tokenizer create() {
        return new TermExtractorTokenizer(new PhoneSearchTermExtractor(), new EmailTermExtractor());
    }
}

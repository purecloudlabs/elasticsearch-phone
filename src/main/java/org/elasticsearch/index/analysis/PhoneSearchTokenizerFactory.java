package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

public class PhoneSearchTokenizerFactory extends AbstractTokenizerFactory {

    // The four-arg signature is required by AnalysisModule.AnalysisProvider so this can be
    // referenced as PhoneSearchTokenizerFactory::new; only the name is needed by the base class.
    public PhoneSearchTokenizerFactory(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(name);
    }

    @Override
    public Tokenizer create() {
        return new TermExtractorTokenizer(new PhoneSearchTermExtractor());
    }
}

package org.elasticsearch.plugins.analysis.phone;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.elasticsearch.index.analysis.PhoneSearchAnalyzer;

public class PhoneSearchAnalyzerProvider extends AbstractIndexAnalyzerProvider<PhoneSearchAnalyzer> {

    private final PhoneSearchAnalyzer analyzer = new PhoneSearchAnalyzer();

    // The four-arg signature is required by AnalysisModule.AnalysisProvider so this can be
    // referenced as PhoneSearchAnalyzerProvider::new; only the name is needed by the base class.
    public PhoneSearchAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(name);
    }

    @Override
    public PhoneSearchAnalyzer get() {
        return analyzer;
    }
}

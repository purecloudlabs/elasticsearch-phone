package org.elasticsearch.plugins.analysis.phone;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.elasticsearch.index.analysis.PhoneAnalyzer;

public class PhoneAnalyzerProvider extends AbstractIndexAnalyzerProvider<PhoneAnalyzer> {

    private final PhoneAnalyzer analyzer = new PhoneAnalyzer();

    // The four-arg signature is required by AnalysisModule.AnalysisProvider so this can be
    // referenced as PhoneAnalyzerProvider::new; only the name is needed by the base class.
    public PhoneAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(name);
    }

    @Override
    public PhoneAnalyzer get() {
        return analyzer;
    }
}

package org.elasticsearch.plugins.analysis.phone;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.elasticsearch.index.analysis.PhoneEmailAnalyzer;

public class PhoneEmailAnalyzerProvider extends AbstractIndexAnalyzerProvider<PhoneEmailAnalyzer> {

    private final PhoneEmailAnalyzer analyzer = new PhoneEmailAnalyzer();

    // The four-arg signature is required by AnalysisModule.AnalysisProvider so this can be
    // referenced as PhoneEmailAnalyzerProvider::new; only the name is needed by the base class.
    public PhoneEmailAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(name);
    }

    @Override
    public PhoneEmailAnalyzer get() {
        return analyzer;
    }
}

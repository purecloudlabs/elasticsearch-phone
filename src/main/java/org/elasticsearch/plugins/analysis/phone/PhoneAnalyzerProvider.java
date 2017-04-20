package org.elasticsearch.plugins.analysis.phone;

import java.io.IOException;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.elasticsearch.index.analysis.PhoneAnalyzer;

public class PhoneAnalyzerProvider extends AbstractIndexAnalyzerProvider<PhoneAnalyzer> {

    protected PhoneAnalyzer analyzer = new PhoneAnalyzer();

    @Inject
    public PhoneAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) throws IOException {
        super(indexSettings, name, settings);
    }

    @Override
    public PhoneAnalyzer get() {
        return analyzer;
    }

}
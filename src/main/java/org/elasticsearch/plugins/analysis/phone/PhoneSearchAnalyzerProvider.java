package org.elasticsearch.plugins.analysis.phone;

import java.io.IOException;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.elasticsearch.index.analysis.PhoneSearchAnalyzer;

public class PhoneSearchAnalyzerProvider extends AbstractIndexAnalyzerProvider<PhoneSearchAnalyzer> {
    private final PhoneSearchAnalyzer analyzer = new PhoneSearchAnalyzer();
    
    @Inject
    public PhoneSearchAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) throws IOException {
        super(indexSettings, name, settings);
    }
    
    @Override
    public PhoneSearchAnalyzer get() {
        return analyzer;
    }
}

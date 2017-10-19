package org.elasticsearch.plugins.analysis.phone;

import java.io.IOException;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.elasticsearch.index.analysis.PhoneEmailAnalyzer;
import org.elasticsearch.index.settings.IndexSettings;

public class PhoneEmailAnalyzerProvider extends AbstractIndexAnalyzerProvider<PhoneEmailAnalyzer> {
    public static final String NAME = "phone-email";
    private PhoneEmailAnalyzer analyzer = new PhoneEmailAnalyzer();
    
    @Inject
    public PhoneEmailAnalyzerProvider(Index index, @IndexSettings Settings indexSettings, Environment env, @Assisted String name, @Assisted Settings settings)
            throws IOException {
        super(index, indexSettings, name, settings);
    }
    
    @Override
    public PhoneEmailAnalyzer get() {
        return analyzer;
    }
    
}

package org.elasticsearch.plugins.analysis.phone;

import static java.util.Collections.singletonMap;

import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.elasticsearch.index.analysis.AnalyzerProvider;
import org.elasticsearch.index.analysis.PhoneTokenizerFactory;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.indices.analysis.AnalysisModule.AnalysisProvider;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;

public class PhonePlugin extends Plugin implements AnalysisPlugin {

    public static final String NAME = "phone-plugin";

    /* Return a description of this plugin. */
    public String description() {
        return "Makes a best attempt at tokenizing a phone number or sip address";
    }

    public String name() {
        return NAME;
    }

    @Override
    public Map<String, AnalysisProvider<TokenizerFactory>> getTokenizers() {
        return singletonMap("phone_tokenizer", PhoneTokenizerFactory::new);
    }

    @Override
    public Map<String, AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> getAnalyzers() {
        return singletonMap("phone", PhoneAnalyzerProvider::new);
    }
}
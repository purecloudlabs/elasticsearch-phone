package org.elasticsearch.plugins.analysis.phone;

import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.elasticsearch.index.analysis.AnalyzerProvider;
import org.elasticsearch.index.analysis.PhoneEmailTokenizerFactory;
import org.elasticsearch.index.analysis.PhoneSearchTokenizerFactory;
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
        Map<String, AnalysisProvider<TokenizerFactory>> tokenizerMap = new HashMap<>();
        tokenizerMap.put("phone_tokenizer", PhoneTokenizerFactory::new);
        tokenizerMap.put("phone_email_tokenizer", PhoneEmailTokenizerFactory::new);
        tokenizerMap.put("phone_search_tokenizer", PhoneSearchTokenizerFactory::new);
        return tokenizerMap;
    }
    
    @Override
    public Map<String, AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> getAnalyzers() {
        Map<String, AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> analyzerMap = new HashMap<>();
        analyzerMap.put("phone", PhoneAnalyzerProvider::new);
        analyzerMap.put("phone-search", PhoneSearchAnalyzerProvider::new);
        analyzerMap.put("phone-email", PhoneEmailAnalyzerProvider::new);
        return analyzerMap;
    }
}

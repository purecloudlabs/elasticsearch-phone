package org.elasticsearch.plugins.analysis.phone;

import org.elasticsearch.index.analysis.AnalysisModule;

public class PhoneEmailBinderProcessor extends AnalysisModule.AnalysisBinderProcessor {
    
    @Override
    public void processAnalyzers(AnalyzersBindings analyzersBindings) {
        analyzersBindings.processAnalyzer(PhoneEmailAnalyzerProvider.NAME, PhoneEmailAnalyzerProvider.class);
    }
}

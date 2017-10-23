package org.elasticsearch.plugins.analysis.phone;
import  org.elasticsearch.index.analysis.AnalysisModule;

public class CustomAnalysisBinderProcessor extends AnalysisModule.AnalysisBinderProcessor {

    @Override
    public void processAnalyzers(AnalyzersBindings analyzersBindings) {
        analyzersBindings.processAnalyzer(PhoneSearchAnalyzerProvider.NAME, PhoneSearchAnalyzerProvider.class);
        analyzersBindings.processAnalyzer(PhoneEmailAnalyzerProvider.NAME, PhoneEmailAnalyzerProvider.class);
        analyzersBindings.processAnalyzer(PhoneAnalyzerProvider.NAME, PhoneAnalyzerProvider.class);
    }
}
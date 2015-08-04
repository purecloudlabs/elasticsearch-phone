package org.elasticsearch.plugins.analysis.phone;
import  org.elasticsearch.index.analysis.AnalysisModule;

public class PhoneBinderProcessor extends AnalysisModule.AnalysisBinderProcessor {

    @Override
    public void processAnalyzers(AnalyzersBindings analyzersBindings) {
    	analyzersBindings.processAnalyzer(PhoneAnalyzerProvider.NAME, PhoneAnalyzerProvider.class);
    }
}
package org.elasticsearch.plugins.analysis.phone;

import org.elasticsearch.common.inject.Module;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.plugins.AbstractPlugin;
import org.elasticsearch.plugins.Plugin;

public class PhonePlugin extends AbstractPlugin implements Plugin {
    
    /* Return a description of this plugin. */
    @Override
    public String description() {
        return "Provides analyzers for phone numbers";
    }
    
    public void onModule(AnalysisModule analysisModule) {
        analysisModule.addProcessor(new CustomAnalysisBinderProcessor());
    }
    
    @Override
    public void processModule(Module module) {
        if (module instanceof AnalysisModule) {
            onModule((AnalysisModule) module);
        }
    }
    
    @Override
    public String name() {
        return "phone-plugin";
    }
}

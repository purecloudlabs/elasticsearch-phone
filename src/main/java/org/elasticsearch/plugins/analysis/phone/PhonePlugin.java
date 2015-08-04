package org.elasticsearch.plugins.analysis.phone;

import org.elasticsearch.common.inject.Module;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.plugins.AbstractPlugin;
import org.elasticsearch.plugins.Plugin;

public class PhonePlugin extends AbstractPlugin implements Plugin {

    /* Return a description of this plugin. */
    public String description() {
        return "Makes a best attempt at tokenizing a phone number or sip address";
    }

    /* This is the function that will register our analyzer with Elasticsearch. */
    public void onModule(AnalysisModule analysisModule) {
        analysisModule.addProcessor(new PhoneBinderProcessor());
    }
    
    @Override 
    public void processModule(Module module) {
        if (module instanceof AnalysisModule) {
            AnalysisModule analysisModule = (AnalysisModule) module;
            analysisModule.addProcessor(new PhoneBinderProcessor());
        }
    }
    
	public String name() {
		return "phone-plugin";
	}
}
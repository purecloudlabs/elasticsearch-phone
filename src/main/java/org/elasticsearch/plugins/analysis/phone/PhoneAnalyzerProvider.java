package org.elasticsearch.plugins.analysis.phone;


import java.io.IOException;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.elasticsearch.index.analysis.PhoneAnalyzer;
import org.elasticsearch.index.settings.IndexSettings;

public class PhoneAnalyzerProvider extends AbstractIndexAnalyzerProvider<PhoneAnalyzer> {
	protected PhoneAnalyzer analyzer = new PhoneAnalyzer();
	public static final String NAME = "phone";

	@Inject
	public PhoneAnalyzerProvider(Index index, @IndexSettings Settings indexSettings, Environment env, @Assisted String name, @Assisted Settings settings) throws IOException {
		super(index, indexSettings, name, settings);
	}

	public PhoneAnalyzer getAnalyzer() {
		return analyzer;
	}

	public PhoneAnalyzer get() {
		return analyzer;    
	}

	public static String getName() {
		return NAME;
	}

}
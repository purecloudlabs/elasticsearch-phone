package org.elasticsearch.index.analysis;

import java.util.List;

public interface TermExtractor {
    
    List<String> extractTerms(String input);
}

package org.elasticsearch.index.analysis;

import java.util.ArrayList;
import java.util.List;

/**
 * Term extractor for the search analysis side. This doesn't perform the ngram-tokenization of
 * {@link PhoneTermExtractor} and instead just rips off "tel:" or "sip:" prefixes and a leading "+".
 */
public class PhoneSearchTermExtractor implements TermExtractor {
    
    @Override
    public List<String> extractTerms(String input) {
        List<String> tokens = new ArrayList<>();
        // Rip off the "tel:" or "sip:" prefix
        if (input.indexOf("tel:") == 0 || input.indexOf("sip:") == 0) {
            tokens.add(input.substring(0, 4));
            input = input.substring(4);
        }
        if (input.startsWith("+")) {
            tokens.add(input.substring(1));
        } else {
            tokens.add(input);
        }
        return tokens;
    }
    
}

package org.elasticsearch.index.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extractor that assumes email addresses as input and splits them into their components.
 */
public class EmailTermExtractor implements TermExtractor {
    
    private final List<Matcher> matchers = new ArrayList<>();
    
    public EmailTermExtractor() {
        matchers.add(Pattern.compile("(\\p{L}+)").matcher(""));
        matchers.add(Pattern.compile("(\\d+)").matcher(""));
    }
    
    @Override
    public List<String> extractTerms(String input) {
        List<String> tokens = new ArrayList<String>();
        if (input.indexOf("tel:") == 0 || input.indexOf("sip:") == 0) {
            // No tokenization for SIP URIs or other phone numbers
            return tokens;
        }
        // Preserve the original input
        tokens.add(input);
        int posAt = input.indexOf('@');
        if (posAt <= 0) {
            return tokens;
        }
        String userPart = input.substring(0, posAt);
        processPart(userPart, tokens);
        String domainPart = input.substring(posAt + 1);
        processPart(domainPart, tokens);
        return tokens;
    }
    
    private void processPart(String part, List<String> tokens) {
        tokens.add(part);
        for (Matcher matcher : matchers) {
            matcher.reset(part);
            while (matcher.find()) {
                tokens.add(matcher.group());
            }
        }
    }
}

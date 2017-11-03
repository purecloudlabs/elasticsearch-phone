package org.elasticsearch.index.analysis;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

public class PhoneTermExtractor implements TermExtractor {
    
    @Override
    public List<String> extractTerms(String input) {
        List<String> tokens = new ArrayList<String>();
        tokens.add(input);
        // Rip off the "tel:" or "sip:" prefix
        if (input.indexOf("tel:") == 0 || input.indexOf("sip:") == 0) {
            tokens.add(input.substring(0, 4));
            input = input.substring(4);
        }
        int startIndex = input.startsWith("+") ? 1 : 0;
        // Add the complete input but skip a leading +
        tokens.add(input.substring(startIndex));
        // Drop anything after @. Most likely there's nothing of interest
        int posAt = input.indexOf('@');
        if (posAt != -1) {
            input = input.substring(0, posAt);
        }
        
        // Add a token for the raw unmanipulated address. Note this could be a username (sip) instead of telephone
        // number so take it as is
        tokens.add(input.substring(startIndex));
        
        // Let google's libphone try to parse it
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        PhoneNumber numberProto = null;
        String countryCode = null;
        try {
            // ZZ is the generic "I don't know the country code" region. Google's libphone library will try to infer it.
            numberProto = phoneUtil.parse(input, "ZZ");
            if (numberProto != null) {
                // Libphone likes it!
                countryCode = String.valueOf(numberProto.getCountryCode());
                input = String.valueOf(numberProto.getNationalNumber());
                
                // Add Country code, extension, and the number as tokens
                tokens.add(countryCode);
                if (!StringUtils.isEmpty(numberProto.getExtension())) {
                    tokens.add(numberProto.getExtension());
                }
                
                tokens.add(input);
            }
        } catch (NumberParseException e) {
            // Libphone didn't like it, no biggie. We'll just ngram the number as it is.
        } catch (StringIndexOutOfBoundsException e) {
            // Libphone didn't like it, no biggie. We'll just ngram the number as it is.
        }
        
        // ngram the phone number EG 19198243333 produces 9, 91, 919, etc
        if (NumberUtils.isNumber(input)) {
            for (int count = 1; count <= input.length(); count++) {
                String token = input.substring(0, count);
                tokens.add(token);
                if (countryCode != null) {
                    // If there was a country code, add more ngrams such that 19198243333 produces 19, 191, 1919, etc
                    tokens.add(countryCode + token);
                }
            }
        }
        return tokens;
    }
}

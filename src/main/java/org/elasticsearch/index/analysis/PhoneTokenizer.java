package org.elasticsearch.index.analysis;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import  org.apache.lucene.analysis.Tokenizer;
import  org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.elasticsearch.common.lang3.StringUtils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import  java.io.IOException;
import  java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class PhoneTokenizer extends Tokenizer {

	// The raw input
	private String stringToTokenize = null;

	// Position in the tokens array. We build all the tokens and return them one at a time as incrementToken gets called. 
	private int position = 0;

	/**
	 * The tokens are determined on the first iteration and then returned one at a time
	 * thereafter.
	 */

	private List<String> tokens = null;

	// The base class grabs the charTermAttribute each time incrementToken returns
	protected CharTermAttribute charTermAttribute = addAttribute(CharTermAttribute.class);

	public PhoneTokenizer(Reader reader) {
		super(reader);
	}

	@Override
	public final boolean incrementToken() throws IOException {
		// Clear anything that is already saved in this.charTermAttribute
		this.charTermAttribute.setEmpty();

		if(tokens == null) {
			// It's the 1st iteration, chop it up into tokens. 
			generateTokens();
		}

		// Return those tokens
		return returnTokensOneAtATime();
	}

	private boolean returnTokensOneAtATime() {
		// Token have already been generated. Return them 1 at a time
		if(tokens != null) {
			if(this.position == tokens.size()) {
				// No more tokens
				return false;
			}

			// return each token, 1 at a time
			this.charTermAttribute.append(tokens.get(this.position));
			this.position += 1;
			return true;
		}
		return false;
	}

	private void generateTokens() {
		String uri = getStringToTokenize();

		tokens = new ArrayList<String>();
		tokens.add(getStringToTokenize());

		// Rip off the "tel:" or "sip:" prefix
		if (uri.indexOf("tel:") != -1 || uri.indexOf("sip:") != -1) {
			uri = uri.substring(4);
		} else {
			// If it's not formatted at least this correctly then the whole string is 1 token. Sorry, put a tel: or sip: at the beginning so we know how to treat it
			tokens.add(getStringToTokenize());
			return;
		}

		// Drop anything after @. Most likely there's nothing of interest
		String[] parts = StringUtils.split(uri, "@");
		if(parts.length == 0) {
			return;
		}
		
		String number = parts[0];

		// Add a token for the raw unmanipulated address. Note this could be a username (sip) instead of telephone number so take it as is
		tokens.add(number);	

		// Let google's libphone try to parse it
		PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
		PhoneNumber numberProto = null;
		String countryCode = null;
		try{
			// ZZ is the generic "I don't know the country code" region. Google's libphone library will try to infer it.
			numberProto = phoneUtil.parse(number, "ZZ");
			if(numberProto != null) {
				// Libphone likes it! 
				countryCode = String.valueOf(numberProto.getCountryCode());
				number = String.valueOf(numberProto.getNationalNumber());

				// Add Country code, extension, and the number as tokens
				tokens.add(countryCode);
				if(!StringUtils.isEmpty(numberProto.getExtension())) {
					tokens.add(numberProto.getExtension());	
				}

				tokens.add(number);
			} 
		} catch(NumberParseException e) {
			// Libphone didn't like it, no biggie. We'll just ngram the number as it is. 
		}	

		// ngram the phone number EG 19198243333 produces 9, 91, 919, etc 
		if(NumberUtils.isNumber(number)) {
			for(int count = 1; count <= number.length(); count++) {
				String token = number.substring(0, count);
				tokens.add(token);
				if(countryCode != null) {
					// If there was a country code, add more ngrams such that 19198243333 produces 19, 191, 1919, etc
					tokens.add(countryCode + token);	
				}
			}
		}
	}

	/**
	 *  Read the input into a local variable
	 * @return
	 */
	private String getStringToTokenize() {
		if(this.stringToTokenize == null) {
			try {
				this.stringToTokenize = IOUtils.toString(input);
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return this.stringToTokenize; 
	}

	/**
	 * Nuke all state after each use (lucene will re-use an instance of this tokenizer over and over again)
	 */
	@Override
	public final void reset() throws IOException {
		super.reset();
		this.position = 0;
		tokens = null;
		this.stringToTokenize = null;
		clearAttributes();
	}
}
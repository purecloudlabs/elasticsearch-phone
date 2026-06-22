/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Adapted from Elasticsearch's org.elasticsearch.lucene.analysis.miscellaneous.UniqueTokenFilter,
 * which is no longer published as a reusable dependency. Vendored here so the phone analyzers can
 * keep de-duplicating tokens without depending on Elasticsearch internals.
 */
package org.elasticsearch.index.analysis;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

/**
 * A token filter that drops duplicate tokens. By default duplicates are removed across the whole
 * stream; with {@code onlyOnSamePosition} set, only tokens at the same position are de-duplicated.
 */
public final class UniqueTokenFilter extends TokenFilter {

    private final CharTermAttribute termAttribute = addAttribute(CharTermAttribute.class);
    private final PositionIncrementAttribute posIncAttribute = addAttribute(PositionIncrementAttribute.class);
    private final Set<String> previous = new HashSet<>();
    private final boolean onlyOnSamePosition;

    public UniqueTokenFilter(TokenStream in) {
        this(in, false);
    }

    public UniqueTokenFilter(TokenStream in, boolean onlyOnSamePosition) {
        super(in);
        this.onlyOnSamePosition = onlyOnSamePosition;
    }

    @Override
    public boolean incrementToken() throws IOException {
        while (input.incrementToken()) {
            final String term = termAttribute.toString();
            final boolean duplicate;
            if (onlyOnSamePosition) {
                final int posIncrement = posIncAttribute.getPositionIncrement();
                if (posIncrement > 0) {
                    previous.clear();
                }
                duplicate = posIncrement == 0 && previous.contains(term);
            } else {
                duplicate = previous.contains(term);
            }
            previous.add(term);
            if (!duplicate) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        previous.clear();
    }
}

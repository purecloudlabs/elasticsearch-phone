package org.elasticsearch.index.analysis;

import java.io.IOException;

import org.junit.Test;

/**
 * Verifies email tokenization. The {@code phone} analyzer keeps the address mostly intact, while
 * the {@code phone_email} analyzer additionally splits the user and domain parts into components.
 * Ported from the email cases of the original PhoneTokenizerIntegrationTest.
 */
public class EmailAnalyzerTest extends PhoneAnalysisTestCase {

    @Test
    public void testEmailSimple() throws IOException {
        assertTokensInclude(phone, "user@domain.com", "user@domain.com", "user");
        assertTokensInclude(phoneEmail, "user@domain.com", "user@domain.com", "user", "domain.com", "domain");
    }

    @Test
    public void testEmailWithNumber() throws IOException {
        assertTokensInclude(phone, "user123@domain.com", "user123@domain.com", "user123");
        assertTokensInclude(phoneEmail, "user123@domain.com",
                "user123@domain.com", "user123", "user", "123", "domain.com", "domain");
    }

    @Test
    public void testEmailUserNameWithMultipleParts() throws IOException {
        assertTokensInclude(phone, "user.name.with.parts@domain.com",
                "user.name.with.parts@domain.com", "user.name.with.parts");
        assertTokensInclude(phoneEmail, "user.name.with.parts@domain.com",
                "user.name.with.parts@domain.com", "user.name.with.parts", "user", "name", "with", "parts", "domain.com", "domain");
    }
}

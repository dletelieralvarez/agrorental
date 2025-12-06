package com.example.web_seguro.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.Key;

import org.junit.jupiter.api.Test;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.WeakKeyException;

public class ConstantsTest {
   @Test
    void testGetSigningKeyB64_InvalidBase64() {
        String invalidBase64 = "###$$$@@@"; // no es válido

        assertThrows(DecodingException.class, () -> {
            Constants.getSigningKeyB64(invalidBase64);
        });
    }

    @Test
void testGetSigningKey_String() {
    // 32 chars = 256 bits (cada char = 8 bits)
    String strongSecret = "12345678901234567890123456789012";

    Key key = Constants.getSigningKey(strongSecret);

    assertNotNull(key);
}

@Test
void testGetSigningKey_String_WeakKey() {
    String weakSecret = "short-key";

    assertThrows(WeakKeyException.class, () -> {
        Constants.getSigningKey(weakSecret);
    });
}
}

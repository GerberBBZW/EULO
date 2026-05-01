package com.eulo;

import com.eulo.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit-Tests für JwtUtil — Token-Generierung und Validierung.
 */
@DisplayName("JwtUtil Unit Tests")
class JwtUtilTest {

    private static final String SECRET = "test-secret-key-for-unit-tests-must-be-32chars!";
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET, 86_400_000L); // 24h
    }

    @Test
    @DisplayName("generateToken → Token ist nicht leer")
    void generateToken_returnsNonEmptyString() {
        String token = jwtUtil.generateToken("user-123");
        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("isValid → gültiger Token ist valid")
    void isValid_withValidToken_returnsTrue() {
        String token = jwtUtil.generateToken("user-123");
        assertThat(jwtUtil.isValid(token)).isTrue();
    }

    @Test
    @DisplayName("isValid → manipulierter Token ist ungültig")
    void isValid_withTamperedToken_returnsFalse() {
        String token = jwtUtil.generateToken("user-123");
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";
        assertThat(jwtUtil.isValid(tampered)).isFalse();
    }

    @Test
    @DisplayName("isValid → komplett falscher Token ist ungültig")
    void isValid_withGarbageToken_returnsFalse() {
        assertThat(jwtUtil.isValid("this.is.not.a.jwt")).isFalse();
    }

    @Test
    @DisplayName("extractUserId → gibt korrekte User-ID zurück")
    void extractUserId_returnsCorrectSubject() {
        String token = jwtUtil.generateToken("user-42");
        assertThat(jwtUtil.extractUserId(token)).isEqualTo("user-42");
    }

    @Test
    @DisplayName("abgelaufener Token → isValid gibt false zurück")
    void isValid_withExpiredToken_returnsFalse() {
        JwtUtil shortLivedUtil = new JwtUtil(SECRET, 1L); // 1ms Ablaufzeit
        String token = shortLivedUtil.generateToken("user-exp");
        // Token sofort abgelaufen
        assertThat(shortLivedUtil.isValid(token)).isFalse();
    }
}

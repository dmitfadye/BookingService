package com.example.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Sha256PasswordHasherTest {

    private Sha256PasswordHasher hasher;

    @BeforeEach
    void setUp() {
        hasher = new Sha256PasswordHasher();
    }

    @Test
    void hashesPasswordAndDoesNotStoreItInPlainText() {
        String hash = hasher.hash("secret123");

        assertThat(hash).startsWith("sha256$");
        assertThat(hash).doesNotContain("secret123");
    }

    @Test
    void matchesCorrectPassword() {
        String hash = hasher.hash("secret123");

        assertThat(hasher.matches("secret123", hash)).isTrue();
    }

    @Test
    void rejectsWrongPassword() {
        String hash = hasher.hash("secret123");

        assertThat(hasher.matches("other", hash)).isFalse();
    }

    @Test
    void usesDifferentSaltForEachHash() {
        String first = hasher.hash("secret123");
        String second = hasher.hash("secret123");

        assertThat(first).isNotEqualTo(second);
        assertThat(hasher.matches("secret123", first)).isTrue();
        assertThat(hasher.matches("secret123", second)).isTrue();
    }

    @Test
    void rejectsMalformedHash() {
        assertThat(hasher.matches("secret123", "not-a-hash")).isFalse();
    }

    @Test
    void rejectsBlankPassword() {
        assertThatThrownBy(() -> hasher.hash("   "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

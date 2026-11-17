package com.example.booking.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TextUtilsTest {

    @Test
    void normalizesEmailToLowerCaseAndTrims() {
        String email = TextUtils.normalizeEmail("  Ivan.Petrov@MAIL.RU ");

        assertThat(email).isEqualTo("ivan.petrov@mail.ru");
    }

    @Test
    void rejectsInvalidEmail() {
        assertThatThrownBy(() -> TextUtils.normalizeEmail("invalid-email"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Некорректный e-mail");
    }

    @Test
    void normalizesPersonNameAndCapitalizesWords() {
        String name = TextUtils.normalizePersonName("  иванов   алексей   петрович ");

        assertThat(name).isEqualTo("Иванов Алексей Петрович");
    }

    @Test
    void masksEmail() {
        String masked = TextUtils.maskEmail("ivan.petrov@mail.ru");

        assertThat(masked).isEqualTo("i***@mail.ru");
    }

    @Test
    void requireTextRejectsNullAndBlankValues() {
        assertThatThrownBy(() -> TextUtils.requireText(null, "name"))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> TextUtils.requireText("   ", "name"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }
}

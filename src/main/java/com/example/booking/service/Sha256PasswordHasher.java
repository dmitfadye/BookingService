package com.example.booking.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

/**
 * Хеширование паролей алгоритмом SHA-256 со случайной солью.
 *
 * <p>Формат хранения: {@code sha256$<соль>$<хеш>}. В лабораторной работе №4
 * реализация может быть заменена на BCrypt без изменения интерфейса.</p>
 */
@Service
public class Sha256PasswordHasher implements PasswordHasher {

    private static final String ALGORITHM = "SHA-256";
    private static final String PREFIX = "sha256";
    private static final int SALT_LENGTH_BYTES = 16;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String hash(String rawPassword) {
        validatePassword(rawPassword);
        byte[] salt = new byte[SALT_LENGTH_BYTES];
        secureRandom.nextBytes(salt);
        byte[] digest = digest(salt, rawPassword);
        Base64.Encoder encoder = Base64.getEncoder().withoutPadding();
        return PREFIX + "$" + encoder.encodeToString(salt) + "$" + encoder.encodeToString(digest);
    }

    @Override
    public boolean matches(String rawPassword, String passwordHash) {
        if (rawPassword == null || rawPassword.isEmpty() || passwordHash == null) {
            return false;
        }
        String[] parts = passwordHash.split("\\$");
        if (parts.length != 3 || !PREFIX.equals(parts[0])) {
            return false;
        }
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] salt = decoder.decode(parts[1]);
            byte[] expected = decoder.decode(parts[2]);
            byte[] actual = digest(salt, rawPassword);
            return MessageDigest.isEqual(expected, actual);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private byte[] digest(byte[] salt, String rawPassword) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM);
            messageDigest.update(salt);
            return messageDigest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Алгоритм " + ALGORITHM + " недоступен", exception);
        }
    }

    private void validatePassword(String rawPassword) {
        Objects.requireNonNull(rawPassword, "Пароль не может быть null");
        if (rawPassword.isBlank()) {
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }
    }
}

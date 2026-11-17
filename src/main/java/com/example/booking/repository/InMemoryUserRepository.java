package com.example.booking.repository;

import com.example.booking.model.User;
import com.example.booking.model.UserRole;
import com.example.booking.util.TextUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory реализация хранилища пользователей.
 *
 * <p>Для хранения используется {@link ConcurrentHashMap}, а для контроля
 * уникальности e-mail — {@link Set}, созданный на основе ConcurrentHashMap.</p>
 */
@Repository
public class InMemoryUserRepository extends AbstractInMemoryRepository<User>
        implements UserRepository {

    private final Set<String> registeredEmails = ConcurrentHashMap.newKeySet();

    @Override
    protected void beforeSave(User user) {
        User existing = findById(user.getId()).orElse(null);
        if (existing != null && existing.getEmail().equals(user.getEmail())) {
            return;
        }
        if (!registeredEmails.add(user.getEmail())) {
            throw new IllegalArgumentException(
                    "Пользователь с e-mail " + user.getEmail() + " уже существует");
        }
        if (existing != null) {
            registeredEmails.remove(existing.getEmail());
        }
    }

    @Override
    protected void afterDelete(User user) {
        registeredEmails.remove(user.getEmail());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String normalized = TextUtils.normalizeEmail(email);
        List<User> users = snapshot();
        for (User user : users) {
            if (user.getEmail().equals(normalized)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<User> findByRole(UserRole role) {
        List<User> result = new ArrayList<>();
        List<User> users = snapshot();
        for (User user : users) {
            if (user.getRole() == role) {
                result.add(user);
            }
        }
        result.sort((first, second) -> first.getId().compareTo(second.getId()));
        return result;
    }

    @Override
    public List<User> findActiveUsers() {
        List<User> result = new ArrayList<>();
        List<User> users = snapshot();
        for (User user : users) {
            if (user.isActive()) {
                result.add(user);
            }
        }
        result.sort((first, second) -> first.getId().compareTo(second.getId()));
        return result;
    }
}

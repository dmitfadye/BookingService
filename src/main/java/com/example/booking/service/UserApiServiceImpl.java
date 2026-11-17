package com.example.booking.service;

import com.example.booking.dto.UserRequest;
import com.example.booking.dto.UserResponse;
import com.example.booking.model.AdminUser;
import com.example.booking.model.CustomerUser;
import com.example.booking.model.ManagerUser;
import com.example.booking.model.User;
import com.example.booking.model.UserRole;
import com.example.booking.repository.UserJpaRepository;
import com.example.booking.util.TextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Реализация каталога пользователей на Spring Data JPA.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserApiServiceImpl implements UserApiService {

    private final UserJpaRepository userJpaRepository;
    private final PasswordHasher passwordHasher;

    @Override
    public List<UserResponse> findAll(UserRole role, Boolean active) {
        List<User> users;
        if (active != null) {
            users = userJpaRepository.findByActive(active);
        } else {
            users = userJpaRepository.findAll();
        }
        return users.stream()
                .filter(user -> role == null || user.getRole() == role)
                .map(this::toResponse)
                .toList();
    }

    @Override
    public UserResponse findById(Long id) {
        return toResponse(getUser(id));
    }

    @Override
    @Transactional
    public UserResponse create(UserRequest request) {
        String email = TextUtils.normalizeEmail(request.email());
        if (userJpaRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new IllegalStateException("Пользователь с e-mail " + email + " уже существует");
        }
        User user = buildUser(null, request, LocalDateTime.now(), true);
        return toResponse(userJpaRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        User existing = getUser(id);
        if (request.role() != existing.getRole()) {
            throw new IllegalArgumentException("Нельзя изменить роль пользователя");
        }
        String email = TextUtils.normalizeEmail(request.email());
        userJpaRepository.findByEmailIgnoreCase(email).ifPresent(other -> {
            if (!other.getId().equals(id)) {
                throw new IllegalStateException("Пользователь с e-mail " + email + " уже существует");
            }
        });
        User updated = buildUser(id, request, existing.getCreatedAt(), existing.isActive());
        return toResponse(userJpaRepository.save(updated));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userJpaRepository.delete(getUser(id));
    }

    private User getUser(Long id) {
        return userJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + id));
    }

    private User buildUser(Long id, UserRequest request, LocalDateTime createdAt, boolean active) {
        String passwordHash = passwordHasher.hash(request.password());
        switch (request.role()) {
            case ADMIN:
                return new AdminUser(id, request.fullName(), request.email(), passwordHash,
                        active, createdAt,
                        TextUtils.requireText(request.position(), "position"));
            case MANAGER:
                return new ManagerUser(id, request.fullName(), request.email(), passwordHash,
                        active, createdAt,
                        TextUtils.requireText(request.department(), "department"));
            case CUSTOMER:
                return new CustomerUser(id, request.fullName(), request.email(), passwordHash,
                        active, createdAt,
                        TextUtils.requireText(request.phone(), "phone"),
                        TextUtils.requireText(request.company(), "company"));
            default:
                throw new IllegalArgumentException("Неизвестная роль: " + request.role());
        }
    }

    private UserResponse toResponse(User user) {
        String position = null;
        String department = null;
        String phone = null;
        String company = null;
        if (user instanceof AdminUser) {
            position = ((AdminUser) user).getPosition();
        } else if (user instanceof ManagerUser) {
            department = ((ManagerUser) user).getDepartment();
        } else if (user instanceof CustomerUser) {
            phone = ((CustomerUser) user).getPhone();
            company = ((CustomerUser) user).getCompany();
        }
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt(),
                position,
                department,
                phone,
                company
        );
    }
}

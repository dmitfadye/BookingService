package com.example.booking.service;

import com.example.booking.model.AdminUser;
import com.example.booking.model.CustomerUser;
import com.example.booking.model.ManagerUser;
import com.example.booking.model.User;
import com.example.booking.model.UserRole;
import com.example.booking.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Реализация сервиса управления пользователями.
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    /**
     * Создаёт сервис.
     *
     * @param userRepository хранилище пользователей
     * @param passwordHasher хешер паролей
     */
    public UserServiceImpl(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public AdminUser registerAdmin(String fullName, String email, String rawPassword,
                                   String position) {
        Long id = userRepository.nextId();
        AdminUser user = new AdminUser(id, fullName, email, passwordHasher.hash(rawPassword),
                true, LocalDateTime.now(), position);
        AdminUser saved = (AdminUser) userRepository.save(user);
        LOGGER.info("Зарегистрирован администратор #{}: {}", saved.getId(), saved.getEmail());
        return saved;
    }

    @Override
    public ManagerUser registerManager(String fullName, String email, String rawPassword,
                                       String department) {
        Long id = userRepository.nextId();
        ManagerUser user = new ManagerUser(id, fullName, email, passwordHasher.hash(rawPassword),
                true, LocalDateTime.now(), department);
        ManagerUser saved = (ManagerUser) userRepository.save(user);
        LOGGER.info("Зарегистрирован менеджер #{}: {}", saved.getId(), saved.getEmail());
        return saved;
    }

    @Override
    public CustomerUser registerCustomer(String fullName, String email, String rawPassword,
                                         String phone, String company) {
        Long id = userRepository.nextId();
        CustomerUser user = new CustomerUser(id, fullName, email, passwordHasher.hash(rawPassword),
                true, LocalDateTime.now(), phone, company);
        CustomerUser saved = (CustomerUser) userRepository.save(user);
        LOGGER.info("Зарегистрирован клиент #{}: {}", saved.getId(), saved.getEmail());
        return saved;
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Пользователь с id " + id + " не найден"));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> findByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void deactivate(Long id) {
        User user = getById(id);
        if (!user.isActive()) {
            throw new IllegalStateException("Пользователь #" + id + " уже деактивирован");
        }
        user.deactivate();
        userRepository.save(user);
        LOGGER.info("Пользователь #{} деактивирован", id);
    }

    @Override
    public boolean delete(Long id) {
        boolean deleted = userRepository.deleteById(id);
        if (deleted) {
            LOGGER.info("Пользователь #{} удалён", id);
        }
        return deleted;
    }
}

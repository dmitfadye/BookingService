package com.example.booking.service;

import com.example.booking.TestFixtures;
import com.example.booking.model.User;
import com.example.booking.model.UserRole;
import com.example.booking.repository.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceImplTest {

    private InMemoryUserRepository repository;
    private UserService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
        service = new UserServiceImpl(repository, new Sha256PasswordHasher());
    }

    @Test
    void registersCustomerWithHashedPassword() {
        User user = service.registerCustomer("петров иван", "PETROV@MAIL.RU",
                "secret123", "+7 900 000-00-00", "ООО «Тест»");

        assertThat(user.getRole()).isEqualTo(UserRole.CUSTOMER);
        assertThat(user.getEmail()).isEqualTo("petrov@mail.ru");
        assertThat(user.getPasswordHash()).doesNotContain("secret123");
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void registersAllUserRoles() {
        service.registerAdmin("админ", "admin@mail.ru", "pass", "Администратор");
        service.registerManager("менеджер", "manager@mail.ru", "pass", "Отдел");
        service.registerCustomer("клиент", "customer@mail.ru", "pass", "phone", "company");

        assertThat(service.findByRole(UserRole.ADMIN)).hasSize(1);
        assertThat(service.findByRole(UserRole.MANAGER)).hasSize(1);
        assertThat(service.findByRole(UserRole.CUSTOMER)).hasSize(1);
    }

    @Test
    void findsUserByEmailIgnoringCase() {
        User user = service.registerCustomer("клиент", "customer@mail.ru",
                "pass", "phone", "company");

        assertThat(service.findByEmail("CUSTOMER@MAIL.RU")).contains(user);
    }

    @Test
    void rejectsDuplicateEmail() {
        service.registerCustomer("клиент", "customer@mail.ru", "pass", "phone", "company");

        assertThatThrownBy(() -> service.registerCustomer("другой", "CUSTOMER@MAIL.RU",
                "pass", "phone", "company"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("уже существует");
    }

    @Test
    void deactivatesUser() {
        User user = service.registerCustomer("клиент", "customer@mail.ru",
                "pass", "phone", "company");

        service.deactivate(user.getId());

        assertThat(service.getById(user.getId()).isActive()).isFalse();
    }

    @Test
    void rejectsRepeatedDeactivation() {
        User user = service.registerCustomer("клиент", "customer@mail.ru",
                "pass", "phone", "company");
        service.deactivate(user.getId());

        assertThatThrownBy(() -> service.deactivate(user.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deletesUser() {
        User user = service.registerCustomer("клиент", "customer@mail.ru",
                "pass", "phone", "company");

        boolean deleted = service.delete(user.getId());

        assertThat(deleted).isTrue();
        assertThat(service.findAll()).isEmpty();
    }

    @Test
    void throwsWhenUserNotFound() {
        assertThatThrownBy(() -> service.getById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    void returnsAllUsers() {
        service.registerAdmin("админ", "admin@mail.ru", "pass", "Администратор");
        service.registerCustomer("клиент", "customer@mail.ru", "pass", "phone", "company");

        List<User> users = service.findAll();

        assertThat(users).hasSize(2);
    }
}

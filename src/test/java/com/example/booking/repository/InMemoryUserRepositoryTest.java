package com.example.booking.repository;

import com.example.booking.TestFixtures;
import com.example.booking.model.User;
import com.example.booking.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryUserRepositoryTest {

    private InMemoryUserRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
    }

    @Test
    void savesAndFindsUserById() {
        User user = TestFixtures.customer(repository.nextId(), "customer@mail.ru");

        repository.save(user);

        assertThat(repository.findById(user.getId())).contains(user);
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void rejectsDuplicateEmailIgnoringCase() {
        User first = TestFixtures.customer(repository.nextId(), "customer@mail.ru");
        User second = TestFixtures.admin(repository.nextId(), "CUSTOMER@MAIL.RU");
        repository.save(first);

        assertThatThrownBy(() -> repository.save(second))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("уже существует");
    }

    @Test
    void findsUserByEmailIgnoringCase() {
        User user = TestFixtures.customer(repository.nextId(), "customer@mail.ru");
        repository.save(user);

        Optional<User> found = repository.findByEmail("CUSTOMER@MAIL.RU");

        assertThat(found).contains(user);
    }

    @Test
    void findsUsersByRole() {
        repository.save(TestFixtures.admin(repository.nextId(), "admin@mail.ru"));
        repository.save(TestFixtures.manager(repository.nextId(), "manager@mail.ru"));
        repository.save(TestFixtures.customer(repository.nextId(), "customer@mail.ru"));

        List<User> customers = repository.findByRole(UserRole.CUSTOMER);

        assertThat(customers).hasSize(1);
        assertThat(customers.get(0).getEmail()).isEqualTo("customer@mail.ru");
    }

    @Test
    void findsOnlyActiveUsers() {
        User active = TestFixtures.customer(repository.nextId(), "active@mail.ru");
        User inactive = TestFixtures.customer(repository.nextId(), "inactive@mail.ru");
        inactive.deactivate();
        repository.save(active);
        repository.save(inactive);

        List<User> result = repository.findActiveUsers();

        assertThat(result).containsExactly(active);
    }

    @Test
    void deletesUserAndReleasesEmail() {
        User user = TestFixtures.customer(repository.nextId(), "customer@mail.ru");
        repository.save(user);

        boolean deleted = repository.deleteById(user.getId());

        assertThat(deleted).isTrue();
        assertThat(repository.existsById(user.getId())).isFalse();
        assertThat(repository.findByEmail("customer@mail.ru")).isEmpty();

        User sameEmail = TestFixtures.customer(repository.nextId(), "customer@mail.ru");
        repository.save(sameEmail);
        assertThat(repository.findByEmail("customer@mail.ru")).contains(sameEmail);
    }

    @Test
    void generatesIncreasingIdentifiers() {
        Long first = repository.nextId();
        Long second = repository.nextId();

        assertThat(second).isGreaterThan(first);
    }

    @Test
    void returnsUsersSortedById() {
        repository.save(TestFixtures.customer(5L, "five@mail.ru"));
        repository.save(TestFixtures.customer(2L, "two@mail.ru"));

        List<User> users = repository.findAll();

        assertThat(users).extracting(User::getId).containsExactly(2L, 5L);
    }
}

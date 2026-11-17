package com.example.booking.model;

import com.example.booking.TestFixtures;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    @Test
    void normalizesFullNameAndEmail() {
        User user = new CustomerUser(1L, "  иванов   алексей ", "IVAN@MAIL.RU",
                "hash", true, java.time.LocalDateTime.now(),
                "+7 900 000-00-00", "ООО «Тест»");

        assertThat(user.getFullName()).isEqualTo("Иванов Алексей");
        assertThat(user.getEmail()).isEqualTo("ivan@mail.ru");
        assertThat(user.getRole()).isEqualTo(UserRole.CUSTOMER);
    }

    @Test
    void adminCanManageUsersAndWorkspaces() {
        User user = TestFixtures.admin(1L, "admin@mail.ru");

        assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(user.canManageUsers()).isTrue();
        assertThat(user.canManageWorkspaces()).isTrue();
        assertThat(user.canCreateBookings()).isTrue();
        assertThat(user.getPermissionsDescription()).contains("Полный доступ");
    }

    @Test
    void managerCanManageWorkspacesButNotUsers() {
        User user = TestFixtures.manager(1L, "manager@mail.ru");

        assertThat(user.getRole()).isEqualTo(UserRole.MANAGER);
        assertThat(user.canManageUsers()).isFalse();
        assertThat(user.canManageWorkspaces()).isTrue();
    }

    @Test
    void customerCannotManageUsersOrWorkspaces() {
        User user = TestFixtures.customer(1L, "customer@mail.ru");

        assertThat(user.getRole()).isEqualTo(UserRole.CUSTOMER);
        assertThat(user.canManageUsers()).isFalse();
        assertThat(user.canManageWorkspaces()).isFalse();
        assertThat(user.canCreateBookings()).isTrue();
    }

    @Test
    void supportsActivationAndDeactivation() {
        User user = TestFixtures.customer(1L, "customer@mail.ru");

        user.deactivate();
        assertThat(user.isActive()).isFalse();

        user.activate();
        assertThat(user.isActive()).isTrue();
    }

    @Test
    void changesFullNameWithNormalization() {
        User user = TestFixtures.customer(1L, "customer@mail.ru");

        user.changeFullName("  соколова   мария ");

        assertThat(user.getFullName()).isEqualTo("Соколова Мария");
    }

    @Test
    void comparesUsersByEmail() {
        User first = TestFixtures.customer(1L, "same@mail.ru");
        User second = TestFixtures.customer(2L, "SAME@mail.ru");

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    void rejectsInvalidEmail() {
        assertThatThrownBy(() -> TestFixtures.customer(1L, "bad-email"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsNonPositiveId() {
        assertThatThrownBy(() -> TestFixtures.customer(0L, "customer@mail.ru"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("положительным");
    }
}

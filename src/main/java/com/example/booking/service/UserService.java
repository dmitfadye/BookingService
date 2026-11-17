package com.example.booking.service;

import com.example.booking.model.AdminUser;
import com.example.booking.model.CustomerUser;
import com.example.booking.model.ManagerUser;
import com.example.booking.model.User;
import com.example.booking.model.UserRole;

import java.util.List;
import java.util.Optional;

/**
 * Сервис управления пользователями.
 */
public interface UserService {

    /**
     * Регистрирует администратора.
     *
     * @param fullName    ФИО
     * @param email       e-mail
     * @param rawPassword пароль в открытом виде
     * @param position    должность
     * @return созданный администратор
     */
    AdminUser registerAdmin(String fullName, String email, String rawPassword, String position);

    /**
     * Регистрирует менеджера.
     *
     * @param fullName    ФИО
     * @param email       e-mail
     * @param rawPassword пароль в открытом виде
     * @param department  отдел
     * @return созданный менеджер
     */
    ManagerUser registerManager(String fullName, String email, String rawPassword,
                                String department);

    /**
     * Регистрирует клиента.
     *
     * @param fullName    ФИО
     * @param email       e-mail
     * @param rawPassword пароль в открытом виде
     * @param phone       телефон
     * @param company     компания
     * @return созданный клиент
     */
    CustomerUser registerCustomer(String fullName, String email, String rawPassword,
                                  String phone, String company);

    /**
     * Возвращает пользователя по идентификатору.
     *
     * @param id идентификатор
     * @return пользователь
     */
    User getById(Long id);

    /**
     * Возвращает всех пользователей.
     *
     * @return список пользователей
     */
    List<User> findAll();

    /**
     * Ищет пользователей по роли.
     *
     * @param role роль
     * @return список пользователей
     */
    List<User> findByRole(UserRole role);

    /**
     * Ищет пользователя по e-mail.
     *
     * @param email e-mail
     * @return пользователь или пустой Optional
     */
    Optional<User> findByEmail(String email);

    /**
     * Деактивирует пользователя.
     *
     * @param id идентификатор
     */
    void deactivate(Long id);

    /**
     * Удаляет пользователя.
     *
     * @param id идентификатор
     * @return {@code true}, если пользователь удалён
     */
    boolean delete(Long id);
}

package com.example.booking.controller;

import com.example.booking.dto.UserRequest;
import com.example.booking.dto.UserResponse;
import com.example.booking.model.UserRole;
import com.example.booking.service.UserApiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST-контроллер каталога пользователей.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserApiService userApiService;

    /**
     * Возвращает пользователей с фильтрами.
     *
     * @param role   роль или null
     * @param active признак активности или null
     * @return список пользователей
     */
    @GetMapping
    public List<UserResponse> findAll(@RequestParam(required = false) UserRole role,
                                      @RequestParam(required = false) Boolean active) {
        return userApiService.findAll(role, active);
    }

    /**
     * Возвращает пользователя по идентификатору.
     *
     * @param id идентификатор
     * @return пользователь
     */
    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable Long id) {
        return userApiService.findById(id);
    }

    /**
     * Создаёт пользователя.
     *
     * @param request запрос
     * @return созданный пользователь
     */
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userApiService.create(request));
    }

    /**
     * Обновляет пользователя.
     *
     * @param id      идентификатор
     * @param request запрос
     * @return обновлённый пользователь
     */
    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id,
                               @Valid @RequestBody UserRequest request) {
        return userApiService.update(id, request);
    }

    /**
     * Удаляет пользователя.
     *
     * @param id идентификатор
     * @return пустой ответ
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userApiService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

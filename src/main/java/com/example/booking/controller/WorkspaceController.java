package com.example.booking.controller;

import com.example.booking.dto.WorkspaceRequest;
import com.example.booking.dto.WorkspaceResponse;
import com.example.booking.model.WorkspaceType;
import com.example.booking.service.WorkspaceApiService;
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
 * REST-контроллер каталога рабочих пространств.
 */
@RestController
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceApiService workspaceApiService;

    /**
     * Возвращает пространства с фильтрами.
     *
     * @param type   тип или null
     * @param active признак доступности или null
     * @return список пространств
     */
    @GetMapping
    public List<WorkspaceResponse> findAll(@RequestParam(required = false) WorkspaceType type,
                                           @RequestParam(required = false) Boolean active) {
        return workspaceApiService.findAll(type, active);
    }

    /**
     * Возвращает пространство по идентификатору.
     *
     * @param id идентификатор
     * @return пространство
     */
    @GetMapping("/{id}")
    public WorkspaceResponse findById(@PathVariable Long id) {
        return workspaceApiService.findById(id);
    }

    /**
     * Создаёт пространство.
     *
     * @param request запрос
     * @return созданное пространство
     */
    @PostMapping
    public ResponseEntity<WorkspaceResponse> create(@Valid @RequestBody WorkspaceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(workspaceApiService.create(request));
    }

    /**
     * Обновляет пространство.
     *
     * @param id      идентификатор
     * @param request запрос
     * @return обновлённое пространство
     */
    @PutMapping("/{id}")
    public WorkspaceResponse update(@PathVariable Long id,
                                    @Valid @RequestBody WorkspaceRequest request) {
        return workspaceApiService.update(id, request);
    }

    /**
     * Удаляет пространство.
     *
     * @param id идентификатор
     * @return пустой ответ
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workspaceApiService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

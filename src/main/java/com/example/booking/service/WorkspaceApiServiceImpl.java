package com.example.booking.service;

import com.example.booking.dto.WorkspaceRequest;
import com.example.booking.dto.WorkspaceResponse;
import com.example.booking.model.CoworkingDesk;
import com.example.booking.model.MeetingRoom;
import com.example.booking.model.Workspace;
import com.example.booking.model.WorkspaceType;
import com.example.booking.repository.WorkspaceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Реализация каталога рабочих пространств на Spring Data JPA.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkspaceApiServiceImpl implements WorkspaceApiService {

    private final WorkspaceJpaRepository workspaceJpaRepository;

    @Override
    public List<WorkspaceResponse> findAll(WorkspaceType type, Boolean active) {
        List<Workspace> workspaces;
        if (active != null) {
            workspaces = workspaceJpaRepository.findByActive(active);
        } else {
            workspaces = workspaceJpaRepository.findAll();
        }
        return workspaces.stream()
                .filter(workspace -> type == null || workspace.getType() == type)
                .map(this::toResponse)
                .toList();
    }

    @Override
    public WorkspaceResponse findById(Long id) {
        return toResponse(getWorkspace(id));
    }

    @Override
    @Transactional
    public WorkspaceResponse create(WorkspaceRequest request) {
        Workspace workspace = buildWorkspace(null, request, true);
        return toResponse(workspaceJpaRepository.save(workspace));
    }

    @Override
    @Transactional
    public WorkspaceResponse update(Long id, WorkspaceRequest request) {
        Workspace existing = getWorkspace(id);
        if (request.type() != existing.getType()) {
            throw new IllegalArgumentException("Нельзя изменить тип пространства");
        }
        Workspace updated = buildWorkspace(id, request, existing.isActive());
        return toResponse(workspaceJpaRepository.save(updated));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        workspaceJpaRepository.delete(getWorkspace(id));
    }

    private Workspace getWorkspace(Long id) {
        return workspaceJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пространство не найдено: " + id));
    }

    private Workspace buildWorkspace(Long id, WorkspaceRequest request, boolean active) {
        switch (request.type()) {
            case MEETING_ROOM:
                return new MeetingRoom(id, request.name(), request.location(),
                        request.capacity(), request.pricePerHour(), active,
                        request.projector() != null && request.projector(),
                        request.videoConference() != null && request.videoConference());
            case COWORKING_DESK:
                return new CoworkingDesk(id, request.name(), request.location(),
                        request.capacity(), request.pricePerHour(), active,
                        request.monitorIncluded() != null && request.monitorIncluded(),
                        request.nearWindow() != null && request.nearWindow());
            default:
                throw new IllegalArgumentException("Неизвестный тип пространства: " + request.type());
        }
    }

    private WorkspaceResponse toResponse(Workspace workspace) {
        Boolean projector = null;
        Boolean videoConference = null;
        Boolean monitorIncluded = null;
        Boolean nearWindow = null;
        if (workspace instanceof MeetingRoom) {
            MeetingRoom room = (MeetingRoom) workspace;
            projector = room.isProjector();
            videoConference = room.isVideoConference();
        } else if (workspace instanceof CoworkingDesk) {
            CoworkingDesk desk = (CoworkingDesk) workspace;
            monitorIncluded = desk.isMonitorIncluded();
            nearWindow = desk.isNearWindow();
        }
        return new WorkspaceResponse(
                workspace.getId(),
                workspace.getName(),
                workspace.getLocation(),
                workspace.getCapacity(),
                workspace.getPricePerHour(),
                workspace.getType(),
                workspace.isActive(),
                projector,
                videoConference,
                monitorIncluded,
                nearWindow
        );
    }
}

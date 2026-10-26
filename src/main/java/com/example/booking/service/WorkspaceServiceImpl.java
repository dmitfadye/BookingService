package com.example.booking.service;

import com.example.booking.model.CoworkingDesk;
import com.example.booking.model.MeetingRoom;
import com.example.booking.model.Workspace;
import com.example.booking.model.WorkspaceType;
import com.example.booking.repository.WorkspaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Реализация сервиса управления рабочими пространствами.
 */
@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceServiceImpl.class);

    private final WorkspaceRepository workspaceRepository;

    /**
     * Создаёт сервис.
     *
     * @param workspaceRepository хранилище пространств
     */
    public WorkspaceServiceImpl(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    @Override
    public MeetingRoom addMeetingRoom(String name, String location, int capacity,
                                      BigDecimal pricePerHour, boolean projector,
                                      boolean videoConference) {
        Long id = workspaceRepository.nextId();
        MeetingRoom room = new MeetingRoom(id, name, location, capacity, pricePerHour,
                true, projector, videoConference);
        MeetingRoom saved = (MeetingRoom) workspaceRepository.save(room);
        LOGGER.info("Добавлена переговорная #{}: {}", saved.getId(), saved.getName());
        return saved;
    }

    @Override
    public CoworkingDesk addCoworkingDesk(String name, String location, BigDecimal pricePerHour,
                                          boolean monitorIncluded, boolean nearWindow) {
        Long id = workspaceRepository.nextId();
        CoworkingDesk desk = new CoworkingDesk(id, name, location, 1, pricePerHour,
                true, monitorIncluded, nearWindow);
        CoworkingDesk saved = (CoworkingDesk) workspaceRepository.save(desk);
        LOGGER.info("Добавлено коворкинг-место #{}: {}", saved.getId(), saved.getName());
        return saved;
    }

    @Override
    public Workspace getById(Long id) {
        return workspaceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Пространство с id " + id + " не найдено"));
    }

    @Override
    public List<Workspace> findAll() {
        return workspaceRepository.findAll();
    }

    @Override
    public List<Workspace> findByType(WorkspaceType type) {
        return workspaceRepository.findByType(type);
    }

    @Override
    public void changePrice(Long id, BigDecimal newPrice) {
        Workspace workspace = getById(id);
        workspace.changePrice(newPrice);
        workspaceRepository.save(workspace);
        LOGGER.info("Для пространства #{} установлена цена {} руб./час",
                id, workspace.getPricePerHour());
    }

    @Override
    public void deactivate(Long id) {
        Workspace workspace = getById(id);
        if (!workspace.isActive()) {
            throw new IllegalStateException("Пространство #" + id + " уже деактивировано");
        }
        workspace.deactivate();
        workspaceRepository.save(workspace);
        LOGGER.info("Пространство #{} деактивировано", id);
    }

    @Override
    public boolean delete(Long id) {
        boolean deleted = workspaceRepository.deleteById(id);
        if (deleted) {
            LOGGER.info("Пространство #{} удалено", id);
        }
        return deleted;
    }
}

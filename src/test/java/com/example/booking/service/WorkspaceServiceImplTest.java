package com.example.booking.service;

import com.example.booking.model.CoworkingDesk;
import com.example.booking.model.MeetingRoom;
import com.example.booking.model.Workspace;
import com.example.booking.model.WorkspaceType;
import com.example.booking.repository.InMemoryWorkspaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkspaceServiceImplTest {

    private InMemoryWorkspaceRepository repository;
    private WorkspaceService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryWorkspaceRepository();
        service = new WorkspaceServiceImpl(repository);
    }

    @Test
    void addsMeetingRoom() {
        MeetingRoom room = service.addMeetingRoom("Зал", "3 этаж", 8,
                new BigDecimal("1200.00"), true, false);

        assertThat(room.getType()).isEqualTo(WorkspaceType.MEETING_ROOM);
        assertThat(service.findAll()).containsExactly(room);
    }

    @Test
    void addsCoworkingDesk() {
        CoworkingDesk desk = service.addCoworkingDesk("Место", "2 этаж",
                new BigDecimal("300.00"), true, true);

        assertThat(desk.getType()).isEqualTo(WorkspaceType.COWORKING_DESK);
        assertThat(desk.getCapacity()).isEqualTo(1);
    }

    @Test
    void findsWorkspacesByType() {
        service.addMeetingRoom("Зал", "3 этаж", 8,
                new BigDecimal("1200.00"), true, false);
        service.addCoworkingDesk("Место", "2 этаж",
                new BigDecimal("300.00"), true, true);

        List<Workspace> rooms = service.findByType(WorkspaceType.MEETING_ROOM);

        assertThat(rooms).hasSize(1);
    }

    @Test
    void changesPrice() {
        Workspace workspace = service.addMeetingRoom("Зал", "3 этаж", 8,
                new BigDecimal("1200.00"), true, false);

        service.changePrice(workspace.getId(), new BigDecimal("1500.00"));

        assertThat(service.getById(workspace.getId()).getPricePerHour())
                .isEqualByComparingTo("1500.00");
    }

    @Test
    void deactivatesWorkspace() {
        Workspace workspace = service.addMeetingRoom("Зал", "3 этаж", 8,
                new BigDecimal("1200.00"), true, false);

        service.deactivate(workspace.getId());

        assertThat(service.getById(workspace.getId()).isActive()).isFalse();
    }

    @Test
    void rejectsRepeatedDeactivation() {
        Workspace workspace = service.addMeetingRoom("Зал", "3 этаж", 8,
                new BigDecimal("1200.00"), true, false);
        service.deactivate(workspace.getId());

        assertThatThrownBy(() -> service.deactivate(workspace.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void deletesWorkspace() {
        Workspace workspace = service.addMeetingRoom("Зал", "3 этаж", 8,
                new BigDecimal("1200.00"), true, false);

        boolean deleted = service.delete(workspace.getId());

        assertThat(deleted).isTrue();
        assertThat(service.findAll()).isEmpty();
    }

    @Test
    void throwsWhenWorkspaceNotFound() {
        assertThatThrownBy(() -> service.getById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("не найдено");
    }
}

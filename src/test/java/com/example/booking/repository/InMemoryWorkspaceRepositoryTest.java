package com.example.booking.repository;

import com.example.booking.TestFixtures;
import com.example.booking.model.Workspace;
import com.example.booking.model.WorkspaceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryWorkspaceRepositoryTest {

    private InMemoryWorkspaceRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryWorkspaceRepository();
    }

    @Test
    void savesAndFindsWorkspace() {
        Workspace workspace = TestFixtures.meetingRoom(repository.nextId());

        repository.save(workspace);

        assertThat(repository.findById(workspace.getId())).contains(workspace);
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void findsWorkspacesByType() {
        repository.save(TestFixtures.meetingRoom(repository.nextId()));
        repository.save(TestFixtures.meetingRoom(repository.nextId()));
        repository.save(TestFixtures.desk(repository.nextId()));

        List<Workspace> rooms = repository.findByType(WorkspaceType.MEETING_ROOM);
        List<Workspace> desks = repository.findByType(WorkspaceType.COWORKING_DESK);

        assertThat(rooms).hasSize(2);
        assertThat(desks).hasSize(1);
    }

    @Test
    void findsOnlyActiveWorkspaces() {
        Workspace active = TestFixtures.meetingRoom(repository.nextId());
        Workspace inactive = TestFixtures.desk(repository.nextId());
        inactive.deactivate();
        repository.save(active);
        repository.save(inactive);

        List<Workspace> result = repository.findActive();

        assertThat(result).containsExactly(active);
    }

    @Test
    void deletesWorkspace() {
        Workspace workspace = TestFixtures.meetingRoom(repository.nextId());
        repository.save(workspace);

        boolean deleted = repository.deleteById(workspace.getId());

        assertThat(deleted).isTrue();
        assertThat(repository.findById(workspace.getId())).isEmpty();
    }

    @Test
    void returnsWorkspacesSortedById() {
        repository.save(TestFixtures.meetingRoom(7L));
        repository.save(TestFixtures.desk(3L));

        List<Workspace> workspaces = repository.findAll();

        assertThat(workspaces).extracting(Workspace::getId).containsExactly(3L, 7L);
    }
}

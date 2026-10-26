package com.example.booking.repository;

import com.example.booking.model.Workspace;
import com.example.booking.model.WorkspaceType;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory реализация хранилища рабочих пространств.
 */
@Repository
public class InMemoryWorkspaceRepository extends AbstractInMemoryRepository<Workspace>
        implements WorkspaceRepository {

    @Override
    public List<Workspace> findByType(WorkspaceType type) {
        List<Workspace> result = new ArrayList<>();
        List<Workspace> workspaces = snapshot();
        for (Workspace workspace : workspaces) {
            if (workspace.getType() == type) {
                result.add(workspace);
            }
        }
        result.sort((first, second) -> first.getId().compareTo(second.getId()));
        return result;
    }

    @Override
    public List<Workspace> findActive() {
        List<Workspace> result = new ArrayList<>();
        List<Workspace> workspaces = snapshot();
        for (Workspace workspace : workspaces) {
            if (workspace.isActive()) {
                result.add(workspace);
            }
        }
        result.sort((first, second) -> first.getId().compareTo(second.getId()));
        return result;
    }
}

package com.example.booking.model;

import com.example.booking.TestFixtures;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkspaceTest {

    @Test
    void meetingRoomHasCorrectTypeAndEquipment() {
        Workspace workspace = TestFixtures.meetingRoom(1L);

        assertThat(workspace.getType()).isEqualTo(WorkspaceType.MEETING_ROOM);
        assertThat(workspace.getEquipmentDescription()).contains("проектор: да");
        assertThat(workspace.canAccommodate(8)).isTrue();
    }

    @Test
    void coworkingDeskHasCorrectTypeAndEquipment() {
        Workspace workspace = TestFixtures.desk(1L);

        assertThat(workspace.getType()).isEqualTo(WorkspaceType.COWORKING_DESK);
        assertThat(workspace.getEquipmentDescription()).contains("монитор: да");
        assertThat(workspace.canAccommodate(2)).isFalse();
    }

    @Test
    void changesPriceWithScale() {
        Workspace workspace = TestFixtures.meetingRoom(1L);

        workspace.changePrice(new BigDecimal("1250.5"));

        assertThat(workspace.getPricePerHour()).isEqualByComparingTo("1250.50");
    }

    @Test
    void rejectsNonPositivePrice() {
        Workspace workspace = TestFixtures.meetingRoom(1L);

        assertThatThrownBy(() -> workspace.changePrice(BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("больше нуля");
    }

    @Test
    void supportsActivationAndDeactivation() {
        Workspace workspace = TestFixtures.meetingRoom(1L);

        workspace.deactivate();
        assertThat(workspace.isActive()).isFalse();

        workspace.activate();
        assertThat(workspace.isActive()).isTrue();
    }
}

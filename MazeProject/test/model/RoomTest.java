package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for Room coordinate storage, door slots, and entrance/exit
 * flags. Pure model logic — no database required.
 */
class RoomTest {


    private Room room;

    @BeforeEach
    void setUp() {
        room = new Room(2, 3);
    }

    @Test
    void constructorStoresRowAndColumn() {
        assertEquals(2, room.getRow());
        assertEquals(3, room.getCol());
    }

    @Test
    void newRoomHasNoDoors() {
        assertTrue(room.getDoors().isEmpty());
        for (Direction d : Direction.values()) {
            assertNull(room.getDoor(d.ordinal()), "door slot " + d + " should start empty");
        }
    }

    @Test
    void setDoorIsReturnedByGetDoor() {
        Door east = new Door(room, new Room(2, 4));
        room.setDoor(Direction.EAST.ordinal(), east);
        assertSame(east, room.getDoor(Direction.EAST.ordinal()));
    }

    @Test
    void getDoorsReturnsOnlyTheNonNullDoors() {
        Door north = new Door(room, new Room(1, 3));
        Door south = new Door(room, new Room(3, 3));
        room.setDoor(Direction.NORTH.ordinal(), north);
        room.setDoor(Direction.SOUTH.ordinal(), south);

        List<Door> doors = room.getDoors();
        assertEquals(2, doors.size());
        assertTrue(doors.contains(north));
        assertTrue(doors.contains(south));
    }

    @Test
    void entranceFlagDefaultsFalseAndCanBeSet() {
        assertFalse(room.isEntrance());
        room.setEntrance();
        assertTrue(room.isEntrance());
    }

    @Test
    void exitFlagDefaultsFalseAndCanBeSet() {
        assertFalse(room.isExit());
        room.setExit();
        assertTrue(room.isExit());
    }
}

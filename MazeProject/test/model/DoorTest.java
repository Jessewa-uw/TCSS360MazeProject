package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for Door state transitions and connectivity. Pure model
 * logic, no database required.
 */
class DoorTest {

    private Room origin;

    private Room destination;
    private Door door;

    @BeforeEach
    void setUp() {
        origin = new Room(0, 0);
        destination = new Room(0, 1);
        door = new Door(origin, destination);
    }

    @Test
    void newDoorIsLockedAndUnanswered() {
        assertTrue(door.isLocked(), "a fresh door should start locked/unanswered");
        assertFalse(door.isOpen());
        assertFalse(door.isBlocked());
    }

    @Test
    void unlockOpensTheDoor() {
        door.unlock();
        assertTrue(door.isOpen());
        assertFalse(door.isLocked());
        assertFalse(door.isBlocked());
    }

    @Test
    void blockBlocksTheDoor() {
        door.block();
        assertTrue(door.isBlocked());
        assertFalse(door.isOpen());
        assertFalse(door.isLocked());
    }

    @Test
    void getOtherSideReturnsOppositeRoom() {
        assertSame(destination, door.getOtherSide(origin));
        assertSame(origin, door.getOtherSide(destination));
    }

    @Test
    void exposesOriginAndDestination() {
        assertSame(origin, door.getMyOrigin());
        assertSame(destination, door.getMyDestination());
    }

    @Test
    void questionIsStoredAndRetrieved() {
        Question q = new SAQuestion("prompt", "answer", 1);
        door.setMyQuestion(q);
        assertSame(q, door.getMyQuestion());
    }

    @Test
    void setExitAndEntranceFlipTheirFlags() {
        assertFalse(door.myExit);
        assertFalse(door.myEntrance);

        door.setExit();
        door.setEntrance();

        assertTrue(door.myExit);
        assertTrue(door.myEntrance);
    }
}

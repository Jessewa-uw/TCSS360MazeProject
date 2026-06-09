package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for GameState, the serializable save snapshot.
 * Building the snapshot needs a real Maze, which loads questions
 * from DB. See MazeTest for the database/classpath
 * requirement; both ship with the project.
 */
class GameStateTest {

    private Maze maze;


    @BeforeEach
    void setUp() throws SQLException {
        maze = new Maze();
    }

    @Test
    void exposesTheMazeAndCurrentRoom() {
        Room here = maze.getEntrance();
        GameState state = new GameState(maze, here);
        assertSame(maze, state.getMaze());
        assertSame(here, state.getCurrentRoom());
    }

    @Test
    void serializationPreservesSharedRoomIdentity() throws IOException, ClassNotFoundException {
        GameState restored = roundTrip(new GameState(maze, maze.getEntrance()));

        Room current = restored.getCurrentRoom();
        Room gridCell = restored.getMaze().getRoom(current.getRow(), current.getCol());
        assertSame(gridCell, current);
    }

    @Test
    void serializationPreservesDoorState() throws IOException, ClassNotFoundException {
        Door opened = firstDoorOf(maze.getEntrance());
        opened.unlock();

        GameState restored = roundTrip(new GameState(maze, maze.getEntrance()));
        Door restoredDoor = firstDoorOf(restored.getCurrentRoom());
        assertTrue(restoredDoor.isOpen(), "an opened door should stay open after a round trip");
    }

    private static Door firstDoorOf(Room room) {
        for (Direction d : Direction.values()) {
            Door door = room.getDoor(d.ordinal());
            if (door != null) {
                return door;
            }
        }
        throw new IllegalStateException("room has no doors");
    }

    /** Serializes an object to a byte array and reads it back. */
    @SuppressWarnings("unchecked")
    private static <T> T roundTrip(T object) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(object);
        }
        try (ObjectInputStream in =
                     new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            return (T) in.readObject();
        }
    }
}

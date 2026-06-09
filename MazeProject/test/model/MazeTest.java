package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for Maze generation and reachability.
 * Constructing a Maze loads questions from DB
 * via the bundled sqlite-jdbc driver, so these tests require the project's
 * database on the working directory and the jar on the classpath. Both ship with
 * the project, so they run as-is inside IntelliJ.
 */
class MazeTest {

    private Maze maze;


    @BeforeEach
    void setUp() throws SQLException {
        maze = new Maze();
    }

    @Test
    void entranceIsTopLeftAndFlagged() {
        Room entrance = maze.getEntrance();
        assertEquals(0, entrance.getRow());
        assertEquals(0, entrance.getCol());
        assertTrue(entrance.isEntrance());
    }

    /**
     * Regression guard: the connectivity repair must always give the entrance at
     * least one door. Repeated because generation is randomized.
     */
    @RepeatedTest(50)
    void entranceAlwaysHasAtLeastOneDoor() throws SQLException {
        Room entrance = new Maze().getEntrance();
        int doorCount = 0;
        for (Direction d : Direction.values()) {
            if (entrance.getDoor(d.ordinal()) != null) {
                doorCount++;
            }
        }
        assertTrue(doorCount >= 1, "entrance ended up with no doors");
    }

    @RepeatedTest(50)
    void exitIsReachableFromEntrance() throws SQLException {
        Maze m = new Maze();
        assertTrue(m.checkPossible(m.getEntrance()),
                "exit should be reachable from the entrance after generation");
    }

    @Test
    void getRoomReturnsTheLiveGridCell() {
        assertSame(maze.getEntrance(), maze.getRoom(0, 0));
    }

    @Test
    void getRoomRejectsOutOfBounds() {
        assertThrows(IllegalArgumentException.class, () -> maze.getRoom(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> maze.getRoom(0, Maze.SIZE));
    }


    @Test
    void getDoorsHasNoNullsAndNoDuplicates() {
        List<Door> doors = maze.getDoors();
        assertFalse(doors.isEmpty());
        for (Door d : doors) {
            assertNotNull(d);
        }
        Set<Door> unique = new HashSet<>(doors);
        assertEquals(doors.size(), unique.size());
    }

    @Test
    void newMazeStartsRunning() {
        assertTrue(maze.isRunning());
    }
}

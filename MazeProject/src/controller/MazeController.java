package controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import javax.swing.JComponent;

import model.Door;
import model.Maze;
import model.Room;
import model.Direction;


public class MazeController{

    public interface MazeControllerListener {
        void onPlayerMoved(Room newRoom);
        void onGameWon();
        void onGameOver(String reason);
        void onInvalidMove(Direction direction);
        void onDoorAttempt(Door door, boolean correct);
    }
    private final Maze maze;
    private Room currentRoom;
    private final MazeControllerListener listener;

    public MazeController(Maze maze, MazeControllerListener listener) {
        this.maze = maze;
        this.listener = listener;
        this.currentRoom = maze.getEntrance();
    }
    public void attachKeyListener(JComponent component) {
        component.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if      (code == KeyEvent.VK_W || code == KeyEvent.VK_UP)    move(Direction.NORTH);
                else if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) move(Direction.EAST);
                else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN)  move(Direction.SOUTH);
                else if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT)  move(Direction.WEST);
            }
        });
    }

    public void move(Direction direction) {
        if (!maze.isRunning()) return;

        Door door = currentRoom.getDoor(direction.ordinal());

        if (door == null || door.isBlocked()) {
            listener.onInvalidMove(direction);
            return;
        }
        if (door.isOpen()) {
            advancePlayer(door);
            return;
        }
        listener.onDoorAttempt(door, false);
    }

    public void submitCorrectAnswer(Door door) {
        door.unlock();
        advancePlayer(door);
    }
    public void submitWrongAnswer(Door door) {
        if (!maze.checkPossible(currentRoom)) {
            maze.setRunning(false);
            listener.onGameOver("No path remains... You are trapped!");
        }
    }
    public Room getCurrentRoom() {
        return currentRoom;
    }

    /**
     * Places the player in the given room. Used when restoring a saved game so
     * the player resumes where they left off rather than at the entrance.
     *
     * @param room the room to start in
     */
    public void setCurrentRoom(Room room) {
        currentRoom = room;
    }
    private void advancePlayer(Door door) {
        currentRoom = door.getMyDestination().equals(currentRoom)
                ? door.getMyOrigin()
                : door.getMyDestination();
        listener.onPlayerMoved(currentRoom);

        if (currentRoom.isExit()) {
            maze.setRunning(false);
            listener.onGameWon();
        }
    }
}

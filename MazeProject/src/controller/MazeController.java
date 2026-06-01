package MazeProject.src.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import javax.swing.JButton;
import javax.swing.JComponent;

import MazeProject.src.model.Lockpick;
import MazeProject.src.model.Door;
import MazeProject.src.model.Maze;
import MazeProject.src.model.Room;
import MazeProject.src.model.Direction;
import MazeProject.src.model.Question;




public class MazeController {

    public interface MazeControllerListener {
        void onPlayerMoved(Room newRoom);

        void onGameWon();

        void onGameOver(String reason);

        void onInvalidMove(Direction direction);

        void onDoorAttempt(Door door, boolean correct);

        void onLockpickUsed(int remaining);
    }

    private final Maze maze;
    private final Lockpick lockpick;
    private Room currentRoom;
    private final MazeControllerListener listener;

    public MazeController(Maze maze, Lockpick lockpick, MazeControllerListener listener) {
        this.maze = maze;
        this.lockpick = lockpick;
        this.listener = listener;
        this.currentRoom = maze.getEntrance();
    }

    public void attachKeyListener(JComponent component) {
        component.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) move(Direction.NORTH);
                else if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) move(Direction.EAST);
                else if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) move(Direction.SOUTH);
                else if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) move(Direction.WEST);
            }
        });
    }

    public void attachButtonListeners(JButton north, JButton east, JButton south, JButton west) {
        if (north != null) north.addActionListener(e -> move(Direction.NORTH));
        if (east != null) east.addActionListener(e -> move(Direction.EAST));
        if (south != null) south.addActionListener(e -> move(Direction.SOUTH));
        if (west != null) west.addActionListener(e -> move(Direction.WEST));
    }

    public void move(Direction direction) {
        if (!maze.isRunning()) return;

        Door door = currentRoom.getDoor(direction.ordinal());

        if (door == null) {
            listener.onInvalidMove(direction);
            return;
        }
        if (door.isLocked()) {
            listener.onDoorAttempt(door, false);
            return;
        }
        advancePlayer(door);
    }

    public boolean submitAnswer(Door door, String answer) {
        boolean picksRemain = lockpick.pick();
        listener.onLockpickUsed(lockpick.getCount());

        if (!picksRemain) {
            maze.setRunning(false);
            return false;
        }

        if (door.getMyQuestion() != null && door.getMyQuestion().checkAnswer(answer)) {
            door.setUnlocked();
            advancePlayer(door);
            return true;
        }
        if (!maze.checkPossible(currentRoom)) {
            maze.setRunning(false);
        }
        return false;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }
    private void advancePlayer(Door door) {
        currentRoom = door.getMyDestination().equals(currentRoom)
                ? door.getMyOrigin()
                : door.getMyDestination();
        listener.onPlayerMoved(currentRoom);

        if (currentRoom.isExit()) {
            maze.setWon(true);
            maze.setRunning(false);
            listener.onGameWon();
        }
    }
}

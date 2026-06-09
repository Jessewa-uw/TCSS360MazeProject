package view;

import model.*;

import javax.swing.*;
import java.awt.*;

/**
 * A top-down rendering of the whole maze grid, used as the corner minimap on
 * the in-game screen. It is a pure view: it paints the maze and highlights the
 * player's current room, but holds no game logic. The enclosing screen tells it
 * where the player is via {@link #setCurrentRoom(Room)}.
 */
public class MazeGUI2D extends JPanel {

    /** Default room edge length in pixels (full-size rendering). */
    public static final int DEFAULT_ROOM_SIZE = 200;

    private final Maze myMaze;
    private Room currentRoom;

    private final int roomSize;
    private final int gap;
    private final int doorWidth;
    private final int doorHeight;

    /** Builds a full-size map. */
    public MazeGUI2D(Maze theMaze) {
        this(theMaze, DEFAULT_ROOM_SIZE);
    }

    /**
     * Builds a map with a custom room size, letting the same renderer serve as
     * both a full view and a shrunken minimap.
     *
     * @param theMaze     the maze to draw
     * @param theRoomSize edge length of each room in pixels
     */
    public MazeGUI2D(Maze theMaze, int theRoomSize) {
        myMaze = theMaze;
        currentRoom = myMaze.getEntrance();

        roomSize = theRoomSize;
        gap = roomSize / 3;
        doorWidth = gap;
        doorHeight = Math.max(3, roomSize / 33);

        setPreferredSize(new Dimension(Maze.SIZE * roomSize, Maze.SIZE * roomSize));
    }

    /**
     * Highlights the given room as the player's location and repaints.
     *
     * @param room the room the player now occupies
     */
    public void setCurrentRoom(Room room) {
        currentRoom = room;
        repaint();
    }

    /**
     * Paints full maze grid by iterating every cell
     * @param theG the <code>Graphics</code> object to protect
     */
    @Override
    protected void paintComponent(Graphics theG) {
        super.paintComponent(theG);
        final Graphics2D g2d = (Graphics2D) theG;

        for (int r = 0; r < Maze.SIZE; r++) {
            for (int c = 0; c < Maze.SIZE; c++) {
                Room room = myMaze.getRoom(r, c);
                drawRoom(g2d, room, c * roomSize, r * roomSize);
            }
        }
    }

    /**
     * Draws a single room cell at the given pixel coordinates
     * @param g 2D graphics content
     * @param room room to draw
     * @param x pixel x coordinate of rooms top left corner
     * @param y pixel y coordinate of rooms top left corner
     */
    private void drawRoom(Graphics2D g, Room room, int x, int y) {
        g.setStroke(new BasicStroke(Math.max(1, roomSize / 50)));

        if (room.isEntrance()) {
            g.setColor(Color.GREEN);
        } else if (room.isExit()) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.BLACK);
        }
        g.fillRect(x, y, roomSize, roomSize);

        drawNorth(g, room, x, y);
        drawSouth(g, room, x, y);
        drawEast(g, room, x, y);
        drawWest(g, room, x, y);

        if (room == currentRoom) {
            int oval = roomSize / 3;
            int off = (roomSize - oval) / 2;
            g.setColor(Color.BLUE);
            g.fillOval(x + off, y + off, oval, oval);
        }
    }

    /**
     * Draws the north wall of the room
     * @param g 2D graphics content
     * @param room room to draw
     * @param x pixel x coordinate of rooms top left corner
     * @param y pixel y coordinate of rooms top left corner
     */
    private void drawNorth(Graphics2D g, Room room, int x, int y) {
        g.setColor(Color.BLACK);
        if (room.getDoor(Direction.NORTH.ordinal()) == null) {
            g.drawLine(x, y, x + roomSize, y);
        } else {
            g.drawLine(x, y, x + gap, y);
            g.drawLine(x + roomSize - gap, y, x + roomSize, y);
            g.setColor(new Color(51, 27, 27));
            g.fillRect(x + (roomSize - doorWidth) / 2, y - doorHeight / 2, doorWidth, doorHeight);
        }
    }

    /**
     * Draws south wall of the room
     * @param g 2D graphics content
     * @param room room to draw
     * @param x pixel x coordinate of rooms top left corner
     * @param y pixel y coordinate of rooms top left corner
     */
    private void drawSouth(Graphics2D g, Room room, int x, int y) {
        g.setColor(Color.BLACK);
        if (room.getDoor(Direction.SOUTH.ordinal()) == null) {
            g.drawLine(x, y + roomSize, x + roomSize, y + roomSize);
        } else {
            g.drawLine(x, y + roomSize, x + gap, y + roomSize);
            g.drawLine(x + roomSize - gap, y + roomSize, x + roomSize, y + roomSize);
            g.setColor(new Color(51, 27, 27));
            g.fillRect(x + (roomSize - doorWidth) / 2, y + roomSize - doorHeight / 2, doorWidth, doorHeight);
        }
    }

    /**
     * Draws the west wall of the room
     * @param g 2D graphics content
     * @param room room to draw
     * @param x pixel x coordinate of rooms top left corner
     * @param y pixel y coordinate of rooms top left corner
     */
    private void drawWest(Graphics2D g, Room room, int x, int y) {
        g.setColor(Color.BLACK);
        if (room.getDoor(Direction.WEST.ordinal()) == null) {
            g.drawLine(x, y, x, y + roomSize);
        } else {
            g.drawLine(x, y, x, y + gap);
            g.drawLine(x, y + roomSize - gap, x, y + roomSize);
            g.setColor(new Color(51, 27, 27));
            g.fillRect(x - doorHeight / 2, y + (roomSize - gap) / 2, doorHeight, gap);
        }
    }

    /**
     * Draws east wall of the room
     * @param g 2D graphics content
     * @param room room to draw
     * @param x pixel x coordinate of rooms top left corner
     * @param y pixel y coordinate of rooms top left corner
     */
    private void drawEast(Graphics2D g, Room room, int x, int y) {
        g.setColor(Color.BLACK);
        if (room.getDoor(Direction.EAST.ordinal()) == null) {
            g.drawLine(x + roomSize, y, x + roomSize, y + roomSize);
        } else {
            g.drawLine(x + roomSize, y, x + roomSize, y + gap);
            g.drawLine(x + roomSize, y + roomSize - gap, x + roomSize, y + roomSize);
            g.setColor(new Color(51, 27, 27));
            g.fillRect(x + roomSize - doorHeight / 2, y + (roomSize - gap) / 2, doorHeight, gap);
        }
    }
}
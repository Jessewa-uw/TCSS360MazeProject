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
    private Room myCurrentRoom;

    private final int myRoomSize;
    private final int myGap;
    private final int myDoorWidth;
    private final int myDoorHeight;

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
        myCurrentRoom = myMaze.getEntrance();

        myRoomSize = theRoomSize;
        myGap = myRoomSize / 3;
        myDoorWidth = myGap;
        myDoorHeight = Math.max(3, myRoomSize / 33);

        setPreferredSize(new Dimension(Maze.SIZE * myRoomSize, Maze.SIZE * myRoomSize));
    }

    /**
     * Highlights the given room as the player's location and repaints.
     *
     * @param theRoom the room the player now occupies
     */
    public void setCurrentRoom(Room theRoom) {
        myCurrentRoom = theRoom;
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
                drawRoom(g2d, room, c * myRoomSize, r * myRoomSize);
            }
        }
    }

    /**
     * Draws a single room cell at the given pixel coordinates
     * @param theG 2D graphics content
     * @param theRoom room to draw
     * @param theX pixel x coordinate of rooms top left corner
     * @param theY pixel y coordinate of rooms top left corner
     */
    private void drawRoom(Graphics2D theG, Room theRoom, int theX, int theY) {
        theG.setStroke(new BasicStroke(Math.max(1, myRoomSize / 50)));

        if (theRoom.isEntrance()) {
            theG.setColor(Color.GREEN);
        } else if (theRoom.isExit()) {
            theG.setColor(Color.RED);
        } else {
            theG.setColor(Color.BLACK);
        }
        theG.fillRect(theX, theY, myRoomSize, myRoomSize);

        drawNorth(theG, theRoom, theX, theY);
        drawSouth(theG, theRoom, theX, theY);
        drawEast(theG, theRoom, theX, theY);
        drawWest(theG, theRoom, theX, theY);

        if (theRoom == myCurrentRoom) {
            int oval = myRoomSize / 3;
            int off = (myRoomSize - oval) / 2;
            theG.setColor(Color.BLUE);
            theG.fillOval(theX + off, theY + off, oval, oval);
        }
    }

    /**
     * Draws the north wall of the room
     * @param theG 2D graphics content
     * @param theRoom room to draw
     * @param theX pixel x coordinate of rooms top left corner
     * @param theY pixel y coordinate of rooms top left corner
     */
    private void drawNorth(Graphics2D theG, Room theRoom, int theX, int theY) {
        theG.setColor(Color.BLACK);
        if (theRoom.getDoor(Direction.NORTH.ordinal()) == null) {
            theG.drawLine(theX, theY, theX + myRoomSize, theY);
        } else {
            theG.drawLine(theX, theY, theX + myGap, theY);
            theG.drawLine(theX + myRoomSize - myGap, theY, theX + myRoomSize, theY);
            theG.setColor(new Color(51, 27, 27));
            theG.fillRect(theX + (myRoomSize - myDoorWidth) / 2, theY - myDoorHeight / 2, myDoorWidth, myDoorHeight);
        }
    }

    /**
     * Draws south wall of the room
     * @param theG 2D graphics content
     * @param theRoom room to draw
     * @param theX pixel x coordinate of rooms top left corner
     * @param theY pixel y coordinate of rooms top left corner
     */
    private void drawSouth(Graphics2D theG, Room theRoom, int theX, int theY) {
        theG.setColor(Color.BLACK);
        if (theRoom.getDoor(Direction.SOUTH.ordinal()) == null) {
            theG.drawLine(theX, theY + myRoomSize, theX + myRoomSize, theY + myRoomSize);
        } else {
            theG.drawLine(theX, theY + myRoomSize, theX + myGap, theY + myRoomSize);
            theG.drawLine(theX + myRoomSize - myGap, theY + myRoomSize, theX + myRoomSize, theY + myRoomSize);
            theG.setColor(new Color(51, 27, 27));
            theG.fillRect(theX + (myRoomSize - myDoorWidth) / 2, theY + myRoomSize - myDoorHeight / 2, myDoorWidth, myDoorHeight);
        }
    }

    /**
     * Draws the west wall of the room
     * @param theG 2D graphics content
     * @param theRoom room to draw
     * @param theX pixel x coordinate of rooms top left corner
     * @param theY pixel y coordinate of rooms top left corner
     */
    private void drawWest(Graphics2D theG, Room theRoom, int theX, int theY) {
        theG.setColor(Color.BLACK);
        if (theRoom.getDoor(Direction.WEST.ordinal()) == null) {
            theG.drawLine(theX, theY, theX, theY + myRoomSize);
        } else {
            theG.drawLine(theX, theY, theX, theY + myGap);
            theG.drawLine(theX, theY + myRoomSize - myGap, theX, theY + myRoomSize);
            theG.setColor(new Color(51, 27, 27));
            theG.fillRect(theX - myDoorHeight / 2, theY + (myRoomSize - myGap) / 2, myDoorHeight, myGap);
        }
    }

    /**
     * Draws east wall of the room
     * @param theG 2D graphics content
     * @param theRoom room to draw
     * @param theX pixel x coordinate of rooms top left corner
     * @param theY pixel y coordinate of rooms top left corner
     */
    private void drawEast(Graphics2D theG, Room theRoom, int theX, int theY) {
        theG.setColor(Color.BLACK);
        if (theRoom.getDoor(Direction.EAST.ordinal()) == null) {
            theG.drawLine(theX + myRoomSize, theY, theX + myRoomSize, theY + myRoomSize);
        } else {
            theG.drawLine(theX + myRoomSize, theY, theX + myRoomSize, theY + myGap);
            theG.drawLine(theX + myRoomSize, theY + myRoomSize - myGap, theX + myRoomSize, theY + myRoomSize);
            theG.setColor(new Color(51, 27, 27));
            theG.fillRect(theX + myRoomSize - myDoorHeight / 2, theY + (myRoomSize - myGap) / 2, myDoorHeight, myGap);
        }
    }
}
package view;

import model.*;

import javax.swing.*;
import java.awt.*;


public class MazeGUI2D extends JPanel {


    private static final int ROOM_SIZE = 100;

    private final Maze myMaze;
    private final Room currentRoom;
    final static int GAP = ROOM_SIZE / 3;
    final static int doorWidth = GAP;
    final static int doorHeight = 6;

    public MazeGUI2D(Maze theMaze) {

        myMaze = theMaze;
        currentRoom = myMaze.getEntrance();

        setPreferredSize(new Dimension(
                Maze.SIZE * ROOM_SIZE,
                Maze.SIZE * ROOM_SIZE
        ));
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics theG) {
        super.paintComponent(theG);
        final Graphics2D g2d = (Graphics2D) theG;

        for (int r = 0; r < Maze.SIZE; r++) {
            for (int c = 0; c < Maze.SIZE; c++) {

                Room room = myMaze.getRoom(r, c);

                int x = c * ROOM_SIZE;
                int y = r * ROOM_SIZE;

                drawRoom(g2d, room, x, y);
            }
        }
    }

    private void drawRoom(Graphics2D g, Room room, int x, int y) {
        g.setStroke(new BasicStroke(4));


        if (room.isEntrance()) {
            g.setColor(Color.GREEN);
        }
        else if (room.isExit()) {
            g.setColor(Color.RED);
        }
        else {
            g.setColor(Color.BLACK);
        }

        g.fillRect(x, y, ROOM_SIZE, ROOM_SIZE);

        DrawNorth(g, room, x, y);
        DrawSouth(g, room, x, y);
        DrawEast(g, room, x, y);
        DrawWest(g, room, x, y);

        if (room == currentRoom) {

            g.setColor(Color.BLUE);

            g.fillOval(
                    x + 35,
                    y + 35,
                    30,
                    30
            );
        }
    }
    private void DrawNorth(Graphics2D g, Room room, int x, int y) {
        g.setColor(Color.BLACK);
        if (room.getDoor(Direction.NORTH.ordinal()) == null) {
            g.drawLine(x, y, x + ROOM_SIZE, y);
        } else {
            g.drawLine(x, y, x + GAP, y);
            g.drawLine(x + ROOM_SIZE - GAP, y, x + ROOM_SIZE, y);
            g.setColor(new Color(51, 27, 27));
            g.fillRect(x + (ROOM_SIZE - doorWidth) / 2, y - doorHeight / 2 ,
                    doorWidth, doorHeight);
        }
    }
    private void DrawSouth(Graphics2D g, Room room, int x, int y) {
        g.setColor(Color.BLACK);
        if (room.getDoor(Direction.SOUTH.ordinal()) == null) {
            g.drawLine(x, y + ROOM_SIZE,
                    x + ROOM_SIZE, y + ROOM_SIZE);
        } else {
            g.drawLine(x, y + ROOM_SIZE, x + GAP, y + ROOM_SIZE);
            g.drawLine(x + ROOM_SIZE - GAP, y + ROOM_SIZE,
                    x + ROOM_SIZE, y  + ROOM_SIZE);
            g.setColor(new Color(51, 27, 27));
            g.fillRect(x + (ROOM_SIZE - doorWidth) / 2, y + ROOM_SIZE - doorHeight / 2,
                    doorWidth, doorHeight);
        }
    }
    private void DrawWest(Graphics2D g, Room room, int x, int y) {
        g.setColor(Color.BLACK);
        if (room.getDoor(Direction.WEST.ordinal()) == null) {
            g.drawLine(x, y, x, y + ROOM_SIZE);
        } else {
            g.drawLine(x, y, x, y + GAP);
            g.drawLine(x, y + ROOM_SIZE - GAP, x, y  + ROOM_SIZE);
            g.setColor(new Color(51, 27, 27));
            g.fillRect(x - 3, y + (ROOM_SIZE - GAP) / 2,
                    6, GAP);
        }
    }
    private void DrawEast(Graphics2D g, Room room, int x, int y) {
        g.setColor(Color.BLACK);
        if (room.getDoor(Direction.EAST.ordinal()) == null) {
            g.drawLine(x + ROOM_SIZE, y,
                    x + ROOM_SIZE, y + ROOM_SIZE);
        } else {
            g.drawLine(x + ROOM_SIZE, y, x + ROOM_SIZE, y + GAP);
            g.drawLine(x + ROOM_SIZE, y + ROOM_SIZE - GAP,
                    x + ROOM_SIZE, y + ROOM_SIZE);
            g.setColor(new Color(51, 27, 27));
            g.fillRect(x + ROOM_SIZE - 3, y + (ROOM_SIZE - GAP) / 2,
                    6, GAP);
        }
    }
}

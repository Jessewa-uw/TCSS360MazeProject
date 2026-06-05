package view;

import model.Direction;
import model.Door;
import model.Room;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A large rendering of the single room the player currently occupies, with its
 * four doors color-coded by state. This is the focal point of the in-game
 * screen and the component that holds keyboard focus for movement.
 */
public class RoomView extends JPanel {

    private static final Color WALL = new Color(40, 40, 40);
    private static final Color FLOOR = new Color(25, 25, 30);
    private static final Color OPEN = new Color(60, 180, 75);
    private static final Color BLOCKED = new Color(200, 50, 50);
    private static final Color LOCKED = new Color(150, 110, 60);

    private Room currentRoom;

    public RoomView(Room theStartRoom) {
        currentRoom = theStartRoom;
        setPreferredSize(new Dimension(420, 420));
        setBackground(Color.BLACK);
        setFocusable(true);
        // Clicking the room reclaims keyboard focus so movement keys work.
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });
    }

    /**
     * Shows a different room (e.g. after the player moves) and repaints.
     *
     * @param room the room to display
     */
    public void setRoom(Room room) {
        currentRoom = room;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics theG) {
        super.paintComponent(theG);
        Graphics2D g = (Graphics2D) theG;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = Math.min(getWidth(), getHeight()) - 80;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;
        int doorSpan = size / 3;

        // Floor.
        g.setColor(FLOOR);
        g.fillRect(x, y, size, size);

        // Walls.
        g.setColor(WALL);
        g.setStroke(new BasicStroke(10));
        g.drawRect(x, y, size, size);

        drawDoor(g, currentRoom.getDoor(Direction.NORTH.ordinal()),
                x + (size - doorSpan) / 2, y - 5, doorSpan, 10);
        drawDoor(g, currentRoom.getDoor(Direction.SOUTH.ordinal()),
                x + (size - doorSpan) / 2, y + size - 5, doorSpan, 10);
        drawDoor(g, currentRoom.getDoor(Direction.WEST.ordinal()),
                x - 5, y + (size - doorSpan) / 2, 10, doorSpan);
        drawDoor(g, currentRoom.getDoor(Direction.EAST.ordinal()),
                x + size - 5, y + (size - doorSpan) / 2, 10, doorSpan);

        // Direction labels.
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 14f));
        g.drawString("N (W)", x + size / 2 - 18, y - 12);
        g.drawString("S", x + size / 2 - 4, y + size + 22);
        g.drawString("W", x - 28, y + size / 2 + 5);
        g.drawString("E", x + size + 14, y + size / 2 + 5);

        // Player marker.
        int marker = size / 4;
        g.setColor(Color.CYAN);
        g.fillOval(x + (size - marker) / 2, y + (size - marker) / 2, marker, marker);

        // Entrance / exit annotation.
        if (currentRoom.isExit()) {
            g.setColor(Color.RED);
            g.drawString("EXIT", x + size / 2 - 16, y + size / 2 - marker / 2 - 6);
        } else if (currentRoom.isEntrance()) {
            g.setColor(Color.GREEN);
            g.drawString("START", x + size / 2 - 20, y + size / 2 - marker / 2 - 6);
        }
    }

    /**
     * Draws one door opening, colored by its state. A {@code null} door means a
     * solid wall, so nothing is drawn.
     */
    private void drawDoor(Graphics2D g, Door door, int x, int y, int w, int h) {
        if (door == null) {
            return;
        }
        if (door.isOpen()) {
            g.setColor(OPEN);
        } else if (door.isBlocked()) {
            g.setColor(BLOCKED);
        } else {
            g.setColor(LOCKED);
        }
        g.fillRect(x, y, w, h);
    }
}

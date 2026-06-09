package view;

import model.Direction;
import model.Door;
import model.Room;

import javax.imageio.ImageIO;
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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * A large rendering of the single room the player currently occupies. The room
 * is drawn by compositing a stack of transparent 100x100 PNG layers (walls,
 * exterior, floor, layout, and one layer per open/blocked door), scaled up to
 * fill the panel — the same layering technique prototyped in {@code RoomDemo}.
 * <p>
 * Because {@link Room} carries no art info, each room's base appearance is
 * derived from a hash of its {@code (row, col)} so it looks varied but stays
 * identical across repaints and save/load. If the PNG assets are missing, the
 * view falls back to a simple vector rendering so the game still runs.
 * <p>
 * This is the focal point of the in-game screen and the component that holds
 * keyboard focus for movement.
 */
public class RoomView extends JPanel {

    /** Folder (relative to the working directory) holding the layer PNGs. */
    private static final String ASSET_DIR = "resources/rooms";

    /** Native size of every layer PNG, in pixels. */
    private static final int TILE = 100;

    private static final String[] COLORS =
            {"rouge", "lime", "green", "periwinkle", "brown", "maroon"};
    private static final String[] FLOORS =
            {"red-oak-floors", "lt-brown-floors", "brown-floors"};
    private static final String[] LAYOUTS = {"room-1", "room-2"};


    private static final long COLOR_SALT = 0x9E3779B97F4A7C15L;
    private static final long FLOOR_SALT = 0xC2B2AE3D27D4EB4FL;
    private static final long LAYOUT_SALT = 0x165667B19E3779F9L;


    private static final Color WALL = new Color(40, 40, 40);
    private static final Color FLOOR = new Color(25, 25, 30);
    private static final Color OPEN = new Color(60, 180, 75);
    private static final Color BLOCKED = new Color(200, 50, 50);
    private static final Color LOCKED = new Color(150, 110, 60);

    private final File assetDir = new File(ASSET_DIR);
    private final Map<String, BufferedImage> imageCache = new HashMap<>();

    private Room currentRoom;

    public RoomView(Room theStartRoom) {
        currentRoom = theStartRoom;
        setPreferredSize(new Dimension(420, 420));
        setBackground(Color.BLACK);
        setFocusable(true);

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
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        int size = Math.min(getWidth(), getHeight()) - 40;
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        boolean drewArt = true;
        try {
            for (String layer : layersFor(currentRoom)) {
                g.drawImage(load(layer), x, y, size, size, null);
            }
        } catch (RuntimeException missingAsset) {
            drewArt = false;
        }

        if (!drewArt) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            drawFallbackRoom(g, x, y, size);
        }

        drawOverlay(g, x, y, size);
    }
    /**
     * Builds the ordered (bottom-to-top) list of layer names for a room: the
     * hashed walls color, the exterior, the floor, the layout, and a layer for
     * each door. A {@code null} door is a solid wall and contributes no layer;
     * an open or unanswered door uses the plain door layer; a blocked door uses
     * the blocked variant.
     * <p>
     * The exterior takes the color of the room to the south (seen beyond the
     * south wall). When a south door connects the rooms the exterior sits below
     * the floor so it shows through the opening; with no south door the floor is
     * laid first and the exterior painted over it as a solid backdrop.
     */
    private List<String> layersFor(Room room) {
        List<String> layers = new ArrayList<>();
        layers.add(colorOf(room) + "-walls");

        Door southDoor = room.getDoor(Direction.SOUTH.ordinal());
        Room southRoom = southDoor != null ? southDoor.getOtherSide(room) : null;
        String exterior = (southRoom != null ? colorOf(southRoom) : colorOf(room)) + "-exterior";
        String floor = floorOf(room);

        if (southDoor != null) {
            layers.add(exterior);
            layers.add(floor);
        } else {
            layers.add(floor);
            layers.add(exterior);
        }

        layers.add(layoutOf(room));

        for (Direction d : Direction.values()) {
            Door door = room.getDoor(d.ordinal());
            if (door == null) {
                continue; // wall
            }
            String prefix = d.name().toLowerCase();
            if (door.isBlocked()) {
                layers.add(prefix + "-door-blocked");
            } else {
                layers.add(prefix + "-door");
            }
        }
        return layers;
    }

    private static String colorOf(Room room) {
        return COLORS[pickIndex(room, COLOR_SALT, COLORS.length)];
    }

    private static String floorOf(Room room) {
        return FLOORS[pickIndex(room, FLOOR_SALT, FLOORS.length)];
    }

    private static String layoutOf(Room room) {
        return LAYOUTS[pickIndex(room, LAYOUT_SALT, LAYOUTS.length)];
    }

    /**
     * Deterministically picks an index in {@code [0, n)} from a room's position
     * and a per-attribute salt, so the look is stable across repaints and
     * save/load yet decorrelated between color, floor, and layout.
     */
    private static int pickIndex(Room room, long salt, int n) {
        long seed = room.getRow() * 73856093L ^ room.getCol() * 19349663L ^ salt;
        return new Random(seed).nextInt(n);
    }

    /** Loads and caches one layer PNG, throwing if the file is missing. */
    private BufferedImage load(String name) {
        BufferedImage img = imageCache.get(name);
        if (img == null) {
            try {
                img = ImageIO.read(new File(assetDir, name + ".png"));
                if (img == null) {
                    throw new IOException("not an image");
                }
            } catch (IOException e) {
                throw new RuntimeException("missing layer: " + name + ".png", e);
            }
            imageCache.put(name, img);
        }
        return img;
    }

    private void drawOverlay(Graphics2D g, int x, int y, int size) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int marker = size / 5;
        g.setColor(Color.CYAN);
        g.fillOval(x + (size - marker) / 2, y + (size - marker) / 2, marker, marker);

        g.setFont(g.getFont().deriveFont(Font.BOLD, 14f));
        if (currentRoom.isExit()) {
            g.setColor(Color.RED);
            g.drawString("EXIT", x + size / 2 - 16, y + size / 2 - marker / 2 - 6);
        } else if (currentRoom.isEntrance()) {
            g.setColor(Color.GREEN);
            g.drawString("START", x + size / 2 - 20, y + size / 2 - marker / 2 - 6);
        }
    }


    private void drawFallbackRoom(Graphics2D g, int x, int y, int size) {
        int doorSpan = size / 3;

        g.setColor(FLOOR);
        g.fillRect(x, y, size, size);
        g.setColor(WALL);
        g.setStroke(new BasicStroke(10));
        g.drawRect(x, y, size, size);

        drawFallbackDoor(g, currentRoom.getDoor(Direction.NORTH.ordinal()),
                x + (size - doorSpan) / 2, y - 5, doorSpan, 10);
        drawFallbackDoor(g, currentRoom.getDoor(Direction.SOUTH.ordinal()),
                x + (size - doorSpan) / 2, y + size - 5, doorSpan, 10);
        drawFallbackDoor(g, currentRoom.getDoor(Direction.WEST.ordinal()),
                x - 5, y + (size - doorSpan) / 2, 10, doorSpan);
        drawFallbackDoor(g, currentRoom.getDoor(Direction.EAST.ordinal()),
                x + size - 5, y + (size - doorSpan) / 2, 10, doorSpan);

        g.setColor(Color.LIGHT_GRAY);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 14f));
        g.drawString("N (W)", x + size / 2 - 18, y - 12);
        g.drawString("S", x + size / 2 - 4, y + size + 22);
        g.drawString("W", x - 28, y + size / 2 + 5);
        g.drawString("E", x + size + 14, y + size / 2 + 5);
    }

    private void drawFallbackDoor(Graphics2D g, Door door, int x, int y, int w, int h) {
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

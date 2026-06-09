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


    private static final String[] COLORS =
            {"rouge", "lime", "green", "periwinkle", "brown", "maroon"};
    private static final String[] FLOORS =
            {"red-oak-floors", "lt-brown-floors", "brown-floors"};
    private static final String[] LAYOUTS = {"room-1", "room-2"};

    private static final String PLAYER = "player";


    private static final long COLOR_SALT = 0x9E3779B97F4A7C15L;
    private static final long FLOOR_SALT = 0xC2B2AE3D27D4EB4FL;
    private static final long LAYOUT_SALT = 0x165667B19E3779F9L;


    private static final Color WALL = new Color(40, 40, 40);
    private static final Color FLOOR = new Color(25, 25, 30);
    private static final Color OPEN = new Color(60, 180, 75);
    private static final Color BLOCKED = new Color(200, 50, 50);
    private static final Color LOCKED = new Color(150, 110, 60);

    private final File myAssetDir = new File(ASSET_DIR);
    private final Map<String, BufferedImage> myImageCache = new HashMap<>();

    private Room myCurrentRoom;

    /**
     * Constructs a room view display of the starting room
     * @param theStartRoom the room to initially display
     */
    public RoomView(Room theStartRoom) {
        myCurrentRoom = theStartRoom;
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
     * @param theRoom the room to display
     */
    public void setRoom(Room theRoom) {
        myCurrentRoom = theRoom;
        repaint();
    }

    /**
     * Composites rooms PNG layers into a panel
     * @param theG the <code>Graphics</code> object to protect
     */
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
            for (String layer : layersFor(myCurrentRoom)) {
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
    private List<String> layersFor(Room theRoom) {
        List<String> layers = new ArrayList<>();
        layers.add(colorOf(theRoom) + "-walls");

        Door southDoor = theRoom.getDoor(Direction.SOUTH.ordinal());
        Room southRoom = southDoor != null ? southDoor.getOtherSide(theRoom) : null;
        String exterior = (southRoom != null ? colorOf(southRoom) : colorOf(theRoom)) + "-exterior";
        String floor = floorOf(theRoom);

        if (southDoor != null) {
            layers.add(exterior);
            layers.add(floor);
        } else {
            layers.add(floor);
            layers.add(exterior);
        }

        layers.add(layoutOf(theRoom));

        for (Direction d : Direction.values()) {
            Door door = theRoom.getDoor(d.ordinal());
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
        layers.add(PLAYER);
        return layers;
    }

    /**
     * Returns the wall color variant name
     * @param theRoom the room to look up
     * @return one of the colors
     */
    private static String colorOf(Room theRoom) {
        return COLORS[pickIndex(theRoom, COLOR_SALT, COLORS.length)];
    }

    /**
     * Returns floor texture variant name
     * @param theRoom the room to look up
     * @return one of the floors
     */
    private static String floorOf(Room theRoom) {
        return FLOORS[pickIndex(theRoom, FLOOR_SALT, FLOORS.length)];
    }

    /**
     * Returns layout variant
     * @param theRoom the room to look up
     * @return one of the layouts
     */
    private static String layoutOf(Room theRoom) {
        return LAYOUTS[pickIndex(theRoom, LAYOUT_SALT, LAYOUTS.length)];
    }

    /**
     * Deterministically picks an index in {@code [0, n)} from a room's position
     * and a per-attribute salt, so the look is stable across repaints and
     * save/load yet decorrelated between color, floor, and layout.
     */
    private static int pickIndex(Room theRoom, long theSalt, int theN) {
        long seed = theRoom.getRow() * 73856093L ^ theRoom.getCol() * 19349663L ^ theSalt;
        return new Random(seed).nextInt(theN);
    }

    /** Loads and caches one layer PNG, throwing if the file is missing. */
    private BufferedImage load(String theName) {
        BufferedImage img = myImageCache.get(theName);
        if (img == null) {
            try {
                img = ImageIO.read(new File(myAssetDir, theName + ".png"));
                if (img == null) {
                    throw new IOException("not an image");
                }
            } catch (IOException e) {
                throw new RuntimeException("missing layer: " + theName + ".png", e);
            }
            myImageCache.put(theName, img);
        }
        return img;
    }


    /**
     * Draws geometric placeholder room
     * @param theG the 2D graphics context
     * @param theX the pixel x coordinate of the rooms top left corner
     * @param theY the pixel y coordinate of the rooms top left corner
     * @param theSize the edge length of the square room
     */
    private void drawFallbackRoom(Graphics2D theG, int theX, int theY, int theSize) {
        int doorSpan = theSize / 3;

        theG.setColor(FLOOR);
        theG.fillRect(theX, theY, theSize, theSize);
        theG.setColor(WALL);
        theG.setStroke(new BasicStroke(10));
        theG.drawRect(theX, theY, theSize, theSize);

        drawFallbackDoor(theG, myCurrentRoom.getDoor(Direction.NORTH.ordinal()),
                theX + (theSize - doorSpan) / 2, theY - 5, doorSpan, 10);
        drawFallbackDoor(theG, myCurrentRoom.getDoor(Direction.SOUTH.ordinal()),
                theX + (theSize - doorSpan) / 2, theY + theSize - 5, doorSpan, 10);
        drawFallbackDoor(theG, myCurrentRoom.getDoor(Direction.WEST.ordinal()),
                theX - 5, theY + (theSize - doorSpan) / 2, 10, doorSpan);
        drawFallbackDoor(theG, myCurrentRoom.getDoor(Direction.EAST.ordinal()),
                theX + theSize - 5, theY + (theSize - doorSpan) / 2, 10, doorSpan);

        theG.setColor(Color.LIGHT_GRAY);
        theG.setFont(theG.getFont().deriveFont(Font.BOLD, 14f));
        theG.drawString("N (W)", theX + theSize / 2 - 18, theY - 12);
        theG.drawString("S", theX + theSize / 2 - 4, theY + theSize + 22);
        theG.drawString("W", theX - 28, theY + theSize / 2 + 5);
        theG.drawString("E", theX + theSize + 14, theY + theSize / 2 + 5);
    }

    /**
     * Draws single door indicator
     * @param theG the 2D graphics context
     * @param theDoor the door to represent
     * @param theX the pixel x coordinate of the rooms top left corner
     * @param theY the pixel y coordinate of the rooms top left corner
     * @param theW the width of the door
     * @param theH the height of the door
     */
    private void drawFallbackDoor(Graphics2D theG, Door theDoor, int theX, int theY, int theW, int theH) {
        if (theDoor == null) {
            return;
        }
        if (theDoor.isOpen()) {
            theG.setColor(OPEN);
        } else if (theDoor.isBlocked()) {
            theG.setColor(BLOCKED);
        } else {
            theG.setColor(LOCKED);
        }
        theG.fillRect(theX, theY, theW, theH);
    }
}

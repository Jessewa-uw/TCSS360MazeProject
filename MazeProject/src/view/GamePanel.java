package view;

import controller.MazeController;
import controller.SaveManager;
import model.Direction;
import model.Door;
import model.GameState;
import model.Maze;
import model.Question;
import model.Room;

import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;

/**
 * The in-game screen and the gameplay view-coordinator. It owns the large
 * {@link RoomView}, the corner {@link MazeGUI2D} minimap, and the HUD, and it
 * is the controller's listener — translating game events into view updates.
 * <p>
 * The minimap corner doubles as the question surface: attempting an unanswered
 * door swaps a {@link QuestionPanel} in over the minimap, and answering (or
 * cancelling) swaps the minimap back.
 */
public class GamePanel extends JPanel implements MazeController.MazeControllerListener {

    /** Room edge length for the shrunken corner minimap. */
    private static final int MINIMAP_ROOM_SIZE = 60;
    /**
     * key for the minimap card
     */
    private static final String MAP_CARD = "map";
    /**
     * key for the question card
     */
    private static final String QUESTION_CARD = "question";

    private final Maze myMaze;
    private final MazeController myController;
    private final MazeGUI2D myMinimap;
    private final RoomView myRoomView;
    private final JLabel myStatus = new JLabel();

    private final CardLayout myCornerCards = new CardLayout();
    private final JPanel myCorner = new JPanel(myCornerCards);
    private final JPanel myQuestionHolder = new JPanel(new BorderLayout());

    /** Starts a fresh game at the maze entrance. */
    public GamePanel(GameWindow theWindow, Maze theMaze) {
        this(theWindow, theMaze, theMaze.getEntrance());
    }

    /**
     * Builds the in-game screen with the player placed in {@code theStartRoom},
     * letting a loaded game resume from a saved position.
     *
     * @param theWindow  GameWindow used for transition
     * @param theMaze the maze to play
     * @param theStartRoom  the room to place the player in
     */
    public GamePanel(GameWindow theWindow, Maze theMaze, Room theStartRoom) {
        myMaze = theMaze;
        setLayout(new BorderLayout());

        myMinimap = new MazeGUI2D(theMaze, MINIMAP_ROOM_SIZE);
        myMinimap.setCurrentRoom(theStartRoom);
        myRoomView = new RoomView(theStartRoom);
        myController = new MazeController(theMaze, this);
        myController.setMyCurrentRoom(theStartRoom);
        myController.attachKeyListener(myRoomView);

        add(buildHud(theWindow), BorderLayout.NORTH);
        add(myRoomView, BorderLayout.CENTER);
        add(buildCorner(), BorderLayout.EAST);

        updateStatus(myController.getMyCurrentRoom());
    }

    /** Requests keyboard focus for the room view so movement keys are received. */
    public void focusGame() {
        myRoomView.requestFocusInWindow();
    }

    /**
     * Invoked when player successfully moves into new room
     * @param newRoom the room the player has entered.
     */
    @Override
    public void onPlayerMoved(Room newRoom) {
        myMinimap.setCurrentRoom(newRoom);
        myRoomView.setRoom(newRoom);
        updateStatus(newRoom);
        showMap();
        myRoomView.requestFocusInWindow();
    }

    /**
     * Invoked when the player enters the exit room
     */
    @Override
    public void onGameWon() {
        showMap();
        JOptionPane.showMessageDialog(this,
                "You reached the exit. You win!",
                "Victory", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Invoked when the game ends in defeat
     * @param theReason Description of why the game ended
     */
    @Override
    public void onGameOver(String theReason) {
        showMap();
        JOptionPane.showMessageDialog(this,
                theReason, "Game Over", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Invoked when the player attempts to move in a direction with no
     * usable door
     * @param theDirection the direction the player tried to move in
     */
    @Override
    public void onInvalidMove(Direction theDirection) {
        // No usable door that way — audible feedback, stay put.
        Toolkit.getDefaultToolkit().beep();
    }

    /**
     * Invoked when player reaches a locked door
     * @param theDoor the attempted door
     * @param theCorrect if the answer to the question was correct or not
     */
    @Override
    public void onDoorAttempt(Door theDoor, boolean theCorrect) {
        Question question = theDoor.getMyQuestion();
        if (question == null) {
            // No question guarding this door — walk straight through.
            myController.submitCorrectAnswer(theDoor);
            return;
        }
        QuestionPanel panel = new QuestionPanel(question,
                answer -> handleAnswer(theDoor, answer),
                this::handleCancel);
        myQuestionHolder.removeAll();
        myQuestionHolder.add(panel, BorderLayout.CENTER);
        myQuestionHolder.revalidate();
        myQuestionHolder.repaint();
        showQuestion();
        panel.focusInput();
    }

    /**
     * Evaluates player's answer for the locked door
     * @param door the door whose question was answered
     * @param answer the player's submitted answer
     */
    private void handleAnswer(Door door, String answer) {
        Question question = door.getMyQuestion();
        if (question.checkAnswer(answer)) {
            myController.submitCorrectAnswer(door);
        } else {
            door.block();
            myRoomView.repaint();
            myMinimap.repaint();
            showMap();
            myRoomView.requestFocusInWindow();
            myController.submitWrongAnswer(door);
        }
    }

    /**
     * Dismisses question panel and returns to minimap without an
     * answer submission
     */
    private void handleCancel() {
        showMap();
        myRoomView.requestFocusInWindow();
    }

    /** Writes the current game (maze progress + position) to the save slot. */
    private void saveGame() {
        try {
            SaveManager.save(new GameState(myMaze, myController.getMyCurrentRoom()));
            JOptionPane.showMessageDialog(this,
                    "Game saved.", "Save", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not save the game:\n" + e.getMessage(),
                    "Save Failed", JOptionPane.ERROR_MESSAGE);
        }
        myRoomView.requestFocusInWindow();
    }

    /**
     * Builds HUD panel placed at the top of the screen
     * @param theWindow window used by menu button to navigate
     * @return constructed HUD
     */
    private JPanel buildHud(GameWindow theWindow) {
        JPanel hud = new JPanel(new BorderLayout());
        hud.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        hud.add(myStatus, BorderLayout.WEST);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton save = new JButton("Save");
        save.addActionListener(e -> saveGame());
        JButton menu = new JButton("Menu");
        menu.addActionListener(e -> theWindow.showWelcome());
        buttons.add(save);
        buttons.add(Box.createHorizontalStrut(4));
        buttons.add(menu);

        hud.add(buttons, BorderLayout.EAST);
        return hud;
    }

    /**
     * Builds corner panel that houses minimap and question cards
     * @return the constructed corner
     */
    private JPanel buildCorner() {
        JPanel mapCard = new JPanel(new BorderLayout());
        mapCard.setBorder(BorderFactory.createTitledBorder("Map"));
        mapCard.add(myMinimap, BorderLayout.CENTER);

        myCorner.add(mapCard, MAP_CARD);
        myCorner.add(myQuestionHolder, QUESTION_CARD);
        myCorner.setPreferredSize(new Dimension(
                Maze.SIZE * MINIMAP_ROOM_SIZE + 24, Maze.SIZE * MINIMAP_ROOM_SIZE + 24));
        myCornerCards.show(myCorner, MAP_CARD);
        return myCorner;
    }

    /**
     * Flips corner to show minimap
     */
    private void showMap() {
        myCornerCards.show(myCorner, MAP_CARD);
    }

    /**
     * Flips corner to show active question
     */
    private void showQuestion() {
        myCornerCards.show(myCorner, QUESTION_CARD);
    }

    /**
     * Updates HUD states to show players current grid position
     * @param room the room the player is currently in
     */
    private void updateStatus(Room room) {
        myStatus.setText("Room (" + room.getRow() + ", " + room.getCol() + ")");
    }
}

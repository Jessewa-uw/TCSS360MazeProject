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

    private static final String MAP_CARD = "map";
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
     */
    public GamePanel(GameWindow theWindow, Maze theMaze, Room theStartRoom) {
        myMaze = theMaze;
        setLayout(new BorderLayout());

        myMinimap = new MazeGUI2D(theMaze, MINIMAP_ROOM_SIZE);
        myMinimap.setCurrentRoom(theStartRoom);
        myRoomView = new RoomView(theStartRoom);
        myController = new MazeController(theMaze, this);
        myController.setCurrentRoom(theStartRoom);
        myController.attachKeyListener(myRoomView);

        add(buildHud(theWindow), BorderLayout.NORTH);
        add(myRoomView, BorderLayout.CENTER);
        add(buildCorner(), BorderLayout.EAST);

        updateStatus(myController.getCurrentRoom());
    }

    /** Requests keyboard focus for the room view so movement keys are received. */
    public void focusGame() {
        myRoomView.requestFocusInWindow();
    }

    // ------------------------------------------------------------------
    // MazeControllerListener
    // ------------------------------------------------------------------

    @Override
    public void onPlayerMoved(Room newRoom) {
        myMinimap.setCurrentRoom(newRoom);
        myRoomView.setRoom(newRoom);
        updateStatus(newRoom);
        showMap();
        myRoomView.requestFocusInWindow();
    }

    @Override
    public void onGameWon() {
        showMap();
        JOptionPane.showMessageDialog(this,
                "You reached the exit. You win!",
                "Victory", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void onGameOver(String reason) {
        showMap();
        JOptionPane.showMessageDialog(this,
                reason, "Game Over", JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void onInvalidMove(Direction direction) {
        // No usable door that way — audible feedback, stay put.
        Toolkit.getDefaultToolkit().beep();
    }

    @Override
    public void onDoorAttempt(Door door, boolean correct) {
        Question question = door.getMyQuestion();
        if (question == null) {
            // No question guarding this door — walk straight through.
            myController.submitCorrectAnswer(door);
            return;
        }
        QuestionPanel panel = new QuestionPanel(question,
                answer -> handleAnswer(door, answer),
                this::handleCancel);
        myQuestionHolder.removeAll();
        myQuestionHolder.add(panel, BorderLayout.CENTER);
        myQuestionHolder.revalidate();
        myQuestionHolder.repaint();
        showQuestion();
        panel.focusInput();
    }

    // ------------------------------------------------------------------
    // Question handling
    // ------------------------------------------------------------------

    private void handleAnswer(Door door, String answer) {
        Question question = door.getMyQuestion();
        if (question.checkAnswer(answer)) {
            // Correct: the controller advances the player, which fires
            // onPlayerMoved and restores the minimap + focus.
            myController.submitCorrectAnswer(door);
        } else {
            door.block();
            myRoomView.repaint();
            myMinimap.repaint();
            showMap();
            myRoomView.requestFocusInWindow();
            // May end the game if this seals off the exit; fires onGameOver.
            myController.submitWrongAnswer(door);
        }
    }

    private void handleCancel() {
        // Player backed out — door stays unanswered, no penalty.
        showMap();
        myRoomView.requestFocusInWindow();
    }

    /** Writes the current game (maze progress + position) to the save slot. */
    private void saveGame() {
        try {
            SaveManager.save(new GameState(myMaze, myController.getCurrentRoom()));
            JOptionPane.showMessageDialog(this,
                    "Game saved.", "Save", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not save the game:\n" + e.getMessage(),
                    "Save Failed", JOptionPane.ERROR_MESSAGE);
        }
        myRoomView.requestFocusInWindow();
    }

    // ------------------------------------------------------------------
    // Layout helpers
    // ------------------------------------------------------------------

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

    private void showMap() {
        myCornerCards.show(myCorner, MAP_CARD);
    }

    private void showQuestion() {
        myCornerCards.show(myCorner, QUESTION_CARD);
    }

    private void updateStatus(Room room) {
        myStatus.setText("Room (" + room.getRow() + ", " + room.getCol() + ")");
    }
}

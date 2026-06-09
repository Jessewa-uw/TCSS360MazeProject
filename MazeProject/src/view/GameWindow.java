package view;

import controller.SaveManager;
import model.GameState;
import model.Maze;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.sql.SQLException;

/**
 * The single top-level window for the Trivia Maze application.
 * <p>
 * Owns a {@link CardLayout} and flips between the welcome screen and the
 * in-game screen. This is the navigation coordinator: gameplay logic lives in
 * the controller, while screen flow lives here.
 */
public class GameWindow extends JFrame {

    /** Card name for the welcome screen. */
    private static final String WELCOME = "welcome";

    /** Card name for the in-game screen. */
    private static final String GAME = "game";

    private final CardLayout myCards = new CardLayout();
    private final javax.swing.JPanel myRoot = new javax.swing.JPanel(myCards);

    /** The currently active game screen, rebuilt for every new game. */
    private GamePanel myGamePanel;

    public GameWindow() {
        super("Trivia Maze");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        myRoot.add(new WelcomePanel(this), WELCOME);
        add(myRoot);
        myCards.show(myRoot, WELCOME);

        setMinimumSize(new Dimension(640, 480));
        pack();
        setLocationRelativeTo(null);
    }

    /** Returns to the welcome screen. */
    public void showWelcome() {
        myCards.show(myRoot, WELCOME);
    }

    /**
     * Builds a fresh maze and switches to the in-game screen. Any previous
     * game screen is discarded.
     */
    public void startNewGame() {
        try {
            Maze maze = new Maze();
            swapInGamePanel(new GamePanel(this, maze));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not load questions from the database:\n" + e.getMessage(),
                    "New Game Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Restores the saved game from the single save slot and switches to the
     * in-game screen at the saved position. Tells the player if no save exists.
     */
    public void loadGame() {
        if (!SaveManager.exists()) {
            JOptionPane.showMessageDialog(this,
                    "There is no saved game to load.",
                    "Load Game", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            GameState state = SaveManager.load();
            swapInGamePanel(new GamePanel(this, state.getMaze(), state.getCurrentRoom()));
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                    "Could not load the saved game:\n" + e.getMessage(),
                    "Load Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Replaces the active game card and brings it to the front, focused. */
    private void swapInGamePanel(GamePanel thePanel) {
        if (myGamePanel != null) {
            myRoot.remove(myGamePanel);
        }
        myGamePanel = thePanel;
        myRoot.add(myGamePanel, GAME);
        myCards.show(myRoot, GAME);
        myRoot.revalidate();
        myRoot.repaint();
        SwingUtilities.invokeLater(myGamePanel::focusGame);
    }
}
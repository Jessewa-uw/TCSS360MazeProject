import view.GameWindow;

import javax.swing.SwingUtilities;

/**
 * Application entry point for Maze
 */
public class Main {
    /**
     * Launches the application
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameWindow window = new GameWindow();
            window.setVisible(true);
        });
    }
}
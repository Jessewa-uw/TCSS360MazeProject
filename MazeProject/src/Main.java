import view.GameWindow;

import javax.swing.SwingUtilities;

/**
 * Application entry point for Maze
 */
public class Main {
    /**
     * Launches the application
     * @param theArgs command line arguments
     */
    public static void main(String[] theArgs) {
        SwingUtilities.invokeLater(() -> {
            GameWindow window = new GameWindow();
            window.setVisible(true);
        });
    }
}
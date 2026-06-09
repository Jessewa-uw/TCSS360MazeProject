package view;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;

/**
 * The welcome screen: title plus New Game / Load Game / Exit.
 * Button actions delegate to the {@link GameWindow} coordinator.
 */
public class WelcomePanel extends JPanel {
    /**
     * Constructs welcome screen and wires all button actions
     * @param theWindow coordinator that handles scene transitions
     */
    public WelcomePanel(GameWindow theWindow) {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JPanel column = new JPanel();
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Trivia Maze");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 36f));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton newGame = makeButton("New Game");
        JButton loadGame = makeButton("Load Game");
        JButton exit = makeButton("Exit");

        newGame.addActionListener(e -> theWindow.startNewGame());
        loadGame.addActionListener(e -> theWindow.loadGame());
        exit.addActionListener(e -> System.exit(0));

        column.add(title);
        column.add(Box.createVerticalStrut(30));
        column.add(newGame);
        column.add(Box.createVerticalStrut(10));
        column.add(loadGame);
        column.add(Box.createVerticalStrut(10));
        column.add(exit);

        add(column);
    }

    /**
     * Creates uniformly sized buttons for use in the welcome screen
     * @param theText label to display on button
     * @return configures button ready for action listener
     */
    private static JButton makeButton(String theText) {
        JButton button = new JButton(theText);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 40));
        button.setPreferredSize(new Dimension(200, 40));
        return button;
    }
}
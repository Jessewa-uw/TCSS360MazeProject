package view;

import model.MCQuestion;
import model.Question;
import model.TFQuestion;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * The overlay shown over the minimap when the player attempts an unanswered
 * door. Renders the prompt and an input appropriate to the question type
 * (radio buttons for multiple choice, True/False radios, or a text field for
 * short answer), then reports the player's answer back through callbacks.
 */
public class QuestionPanel extends JPanel {

    private final ButtonGroup myChoices = new ButtonGroup();
    private final JTextField myTextField;

    /**
     * @param theQuestion the question to present
     * @param theOnSubmit receives the player's answer text when Submit is pressed
     * @param theOnCancel run when the player backs out without answering
     */
    public QuestionPanel(Question theQuestion, Consumer<String> theOnSubmit, Runnable theOnCancel) {
        setLayout(new BorderLayout(8, 8));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel header = new JLabel(theQuestion.getType());
        header.setFont(header.getFont().deriveFont(java.awt.Font.ITALIC, 11f));
        header.setForeground(Color.GRAY);

        JLabel prompt = new JLabel("<html><b>" + escape(theQuestion.getMyPrompt()) + "</b></html>");

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(header, BorderLayout.NORTH);
        top.add(prompt, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        JPanel inputs = new JPanel();
        inputs.setOpaque(false);
        inputs.setLayout(new BoxLayout(inputs, BoxLayout.Y_AXIS));

        if (theQuestion instanceof MCQuestion mc) {
            myTextField = null;
            addChoices(inputs, mc.getChoices());
        } else if (theQuestion instanceof TFQuestion) {
            myTextField = null;
            addChoices(inputs, List.of("True", "False"));
        } else {
            myTextField = new JTextField(16);
            myTextField.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel l = new JLabel("Your answer:");
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            inputs.add(l);
            inputs.add(Box.createVerticalStrut(4));
            inputs.add(myTextField);
        }
        add(inputs, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        buttons.setOpaque(false);
        JButton submit = new JButton("Submit");
        JButton cancel = new JButton("Cancel");
        submit.addActionListener(e -> {
            String answer = readAnswer();
            if (answer == null) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            theOnSubmit.accept(answer);
        });
        cancel.addActionListener(e -> theOnCancel.run());
        buttons.add(submit);
        buttons.add(cancel);
        add(buttons, BorderLayout.SOUTH);
    }

    /** Moves keyboard focus into this overlay's first input. */
    public void focusInput() {
        if (myTextField != null) {
            myTextField.requestFocusInWindow();
        } else {
            requestFocusInWindow();
        }
    }

    /**
     * Populates panel with one button per choice, where only one button
     * can be selected
     * @param inputs panel to add the buttons to
     * @param choices list of answer options
     */
    private void addChoices(JPanel inputs, List<String> choices) {
        for (String choice : choices) {
            JRadioButton rb = new JRadioButton(choice);
            rb.setOpaque(false);
            rb.setActionCommand(choice);
            rb.setAlignmentX(Component.LEFT_ALIGNMENT);
            myChoices.add(rb);
            inputs.add(rb);
        }
    }

    /** Returns the chosen/typed answer, or {@code null} if nothing was entered. */
    private String readAnswer() {
        if (myTextField != null) {
            String text = myTextField.getText().trim();
            return text.isEmpty() ? null : text;
        }
        for (AbstractButton b : Collections.list(myChoices.getElements())) {
            if (b.isSelected()) {
                return b.getActionCommand();
            }
        }
        return null;
    }

    /**
     * Escapes the characters that carry special meaning in HTML
     * @param s raw string to escape
     * @return a new string with HTML equivalents
     */
    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}

package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class JSpinnerCreateDemo extends JFrame {
    public JSpinnerCreateDemo() {
        initializeUI();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                () -> new JSpinnerCreateDemo().setVisible(true));
    }

    private void initializeUI() {
        setSize(500, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Creating an array of color name that we'll use as the
        // source of our SpinnerListMode.
        String[] colors = new String[]{
                "Red", "Orange", "Yellow", "Green", "Blue", "Purple"
        };
        SpinnerListModel model = new SpinnerListModel(colors);

        // Create a JSpinner instance with a spinner model as the value.
        // This JSpinner will allow us to select a colour name when we
        // press the JButton below.
        final JSpinner spinner = new JSpinner(model);
        getContentPane().add(spinner, BorderLayout.NORTH);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            String color = (String) spinner.getValue();
            System.out.println("Color = " + color);
        });
        getContentPane().add(okButton, BorderLayout.SOUTH);
    }
}
package mc.jazhdo.programs;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import mc.jazhdo.DB;
import mc.jazhdo.JRoundPanel;
import mc.jazhdo.Main;
import mc.jazhdo.Program;

public class Todo extends Program {
    private final JRoundPanel main = new JRoundPanel(50);
    private final JPanel tasksContainer = new JPanel();

    @Override
    public void start() {
        main.setSize(1200, Main.contentPane.getHeight());
        main.setBackground(Color.getHSBColor(0, 0, 70));

        tasksContainer.setLayout(new BoxLayout(tasksContainer, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Todo List");
        title.setFont(title.getFont().deriveFont(24f));

        ImageIcon settingsIcon;
        try {
            settingsIcon = new ImageIcon(Main.getResource("settings.png"));
        } catch (IOException ex) {
            Main.logger.error("There was an error getting the settings logo. Error: {}", ex.getMessage());
            JOptionPane.showMessageDialog(Main.root, "Error: Getting the settings logo failed.");
            System.exit(500);
            return;
        }

        JButton settingsButton = new JButton(settingsIcon);
        settingsButton.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Open Settings
            }
        });


    }

    private void loadTasks() {
        try (PreparedStatement ps = DB.get().prepareStatement("SELECT * FROM tasks")) {
            ResultSet results = ps.executeQuery();
        } catch (SQLException ex) {
            
        }
    }

    private void initSettings() {
        // TODO: Create settings
    }
}

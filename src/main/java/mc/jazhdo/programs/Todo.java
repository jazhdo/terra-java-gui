package mc.jazhdo.programs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import mc.jazhdo.DB;
import mc.jazhdo.JRoundPanel;
import mc.jazhdo.Main;
import mc.jazhdo.Program;

public class Todo extends Program {
    private final JPanel main = new JPanel();
    private final JPanel tasksContainer = new JPanel();

    @Override
    public void start() {
        // main.setSize(500, Main.contentPane.getHeight());
        Main.logger.info("Height: {}", Main.contentPane.getHeight());
        main.setBackground(Color.getHSBColor(0.0f, 0.0f, 0.50f));
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

        tasksContainer.setLayout(new BoxLayout(tasksContainer, BoxLayout.Y_AXIS));
        tasksContainer.setBackground(Color.BLACK);
        // tasksContainer.setPreferredSize(new Dimension(main.getWidth(), main.getHeight() - 250));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.X_AXIS));
        // header.setPreferredSize(new Dimension(main.getWidth(), 80));

        JLabel title = new JLabel("Todo List");
        title.setFont(title.getFont().deriveFont(60f));

        ImageIcon settingsIcon;
        try {
            settingsIcon = new ImageIcon(Main.getResource("settings_icon.png"));
        } catch (IOException ex) {
            Main.logger.error("There was an error getting the settings logo. Error: {}", ex.getMessage());
            JOptionPane.showMessageDialog(Main.root, "Error: Getting the settings logo failed.");
            System.exit(500);
            return;
        }

        JButton settingsButton = new JButton(settingsIcon);
        settingsButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Open Settings
            }
        });

        // Init tables
        try (Statement s = DB.get().createStatement()) {
            s.executeUpdate("CREATE TABLE IF NOT EXISTS tasks (color INTEGER NOT NULL, content TEXT NOT NULL, done INTEGER NOT NULL DEFAULT 0, timestamp INTEGER NOT NULL DEFAULT (CAST(unixepoch('subsec') * 1000 AS INTEGER))) STRICT");
        } catch (SQLException ex) {
            Main.logger.error("There was an error creating the tasks table. Error: {}", ex.getMessage());
            return;
        }

        header.add(title);
        header.add(settingsButton);
        main.add(header, BorderLayout.NORTH);
        main.add(tasksContainer, BorderLayout.CENTER);
        Main.contentPane.add(main);

        loadTasks();
    }

    private void loadTasks() {
        try (PreparedStatement ps = DB.get().prepareStatement("SELECT * FROM tasks")) {
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                createTask(Color.getHSBColor(results.getInt("color") / 100, 1.0f, 1.0f), results.getString("content"));
            }
        } catch (SQLException ex) {
            Main.logger.info("There was an error getting tasks from the tasks table. Error: {}", ex.getMessage());
        }
    }

    private void createTask(Color bgColor, String content) {
        JRoundPanel bg = new JRoundPanel(50);
        bg.setLayout(new BoxLayout(bg, BoxLayout.X_AXIS));
        bg.setBackground(bgColor);
        bg.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JCheckBox check = new JCheckBox();
        // check.setPreferredSize(new Dimension(250, 250));

        JTextArea contents = new JTextArea(content);
        contents.setBackground(bgColor);
        contents.setEditable(false);
        contents.setWrapStyleWord(true);
        contents.setLineWrap(true);

        bg.add(check);
        bg.add(contents);
        tasksContainer.add(bg);
    }

    private void initSettings() {
        // TODO: Create settings
    }
}

package mc.jazhdo.programs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
        Main.contentPane.setLayout(new BoxLayout(Main.contentPane, BoxLayout.Y_AXIS));

        main.setBackground(Color.getHSBColor(0.0f, 0.0f, 0.70f));
        main.setLayout(new BorderLayout());
        main.setMaximumSize(new Dimension(500, Integer.MAX_VALUE));
        main.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel header = new JPanel();
        header.setLayout(new BorderLayout());
        header.setOpaque(true);
        header.setBackground(Color.CYAN);

        JLabel title = new JLabel("Todo List");
        title.setFont(title.getFont().deriveFont(60f));
        header.add(title, BorderLayout.LINE_START);

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
        header.add(settingsButton, BorderLayout.LINE_END);

        tasksContainer.setLayout(new BorderLayout());
        tasksContainer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        tasksContainer.setOpaque(false);
        tasksContainer.setBackground(Color.BLACK);

        // Init tables
        try (Statement s = DB.get().createStatement()) {
            s.executeUpdate("CREATE TABLE IF NOT EXISTS tasks (color INTEGER NOT NULL, content TEXT NOT NULL, done INTEGER NOT NULL DEFAULT 0, timestamp INTEGER NOT NULL DEFAULT (CAST(unixepoch('subsec') * 1000 AS INTEGER))) STRICT");
        } catch (SQLException ex) {
            Main.logger.error("There was an error creating the tasks table. Error: {}", ex.getMessage());
            return;
        }

        main.add(header, BorderLayout.PAGE_START);
        main.add(tasksContainer, BorderLayout.CENTER);
        // Main.contentPane.add(Box.createHorizontalGlue());
        Main.contentPane.add(main);
        // Main.contentPane.add(Box.createHorizontalGlue());

        loadTasks();
    }

    private void loadTasks() {
        try (PreparedStatement ps = DB.get().prepareStatement("SELECT * FROM tasks")) {
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                createTask(Color.getHSBColor(results.getInt("color") / 100, 0.65f, 0.94f), results.getString("content"), results.getInt("done") == 1);
            }
        } catch (SQLException ex) {
            Main.logger.info("There was an error getting tasks from the tasks table. Error: {}", ex.getMessage());
        }
    }

    private void createTask(Color bgColor, String content, boolean checked) {
        JRoundPanel bg = new JRoundPanel(50);
        bg.setLayout(new BoxLayout(bg, BoxLayout.X_AXIS));
        bg.setBackground(bgColor);
        bg.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        bg.setMaximumSize(new Dimension(500, 100));

        JCheckBox check = new JCheckBox();
        check.setSelected(checked);
        check.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO add this
            }
        });

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

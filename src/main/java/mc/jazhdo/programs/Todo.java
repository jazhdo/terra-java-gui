package mc.jazhdo.programs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import mc.jazhdo.JRoundPanel;
import mc.jazhdo.Main;
import mc.jazhdo.Program;

public class Todo extends Program {
    private static final ImageIcon uncheckedIcon, checkedIcon;
    static {
        try {
            uncheckedIcon = Main.getScaledIconResource("unchecked.png", 32, 32);
            checkedIcon = Main.getScaledIconResource("checked.png", 32, 32);
        } catch (IOException ex) {
            Main.logger.error("There was an error getting the checked and unchecked png resources.");
            JOptionPane.showMessageDialog(Main.root, "Error: Getting the checkbox images failed.");
            throw new ExceptionInInitializerError(ex);
        }
    }
    private final JPanel main = new JPanel(), tasksContainer = new JPanel();

    @Override
    public void start() {
        Main.contentPane.setLayout(new BoxLayout(Main.contentPane, BoxLayout.Y_AXIS));

        main.setLayout(new BorderLayout());
        main.setMaximumSize(new Dimension(500, Integer.MAX_VALUE));
        main.setAlignmentX(Component.CENTER_ALIGNMENT);
        main.addMouseListener(new MouseAdapter() {
            @Override 
            public void mousePressed(MouseEvent e) {
                Main.root.requestFocusInWindow();
            }
        });

        JPanel header = new JPanel();
        header.setLayout(new BorderLayout());
        header.setOpaque(true);
        header.setBackground(Color.CYAN);
        header.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

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

        tasksContainer.setLayout(new BoxLayout(tasksContainer, BoxLayout.Y_AXIS));
        tasksContainer.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        tasksContainer.setBackground(Color.getHSBColor(0.0f, 0.0f, 0.80f));

        main.add(header, BorderLayout.PAGE_START);
        main.add(tasksContainer, BorderLayout.CENTER);
        Main.contentPane.add(main);

        Main.db.run(conn -> {
            // Init tables
            try (Statement s = conn.createStatement()) {
                s.executeUpdate("CREATE TABLE IF NOT EXISTS tasks (color INTEGER NOT NULL, content TEXT NOT NULL, done INTEGER NOT NULL DEFAULT 0, timestamp INTEGER NOT NULL DEFAULT (CAST(unixepoch('subsec') * 1000 AS INTEGER))) STRICT");
            } catch (SQLException ex) {
                Main.logger.error("There was an error creating the tasks table. Error: {}", ex.getMessage());
                return;
            }

            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM tasks")) {
                ResultSet results = ps.executeQuery();
                Main.logger.info("Retrieving tasks...");
                while (results.next()) {
                    float color = results.getInt("color") / 100.0f;
                    String content = results.getString("content");
                    boolean done = results.getInt("done") == 1;
                    createTask(color, content, done);
                }
                Main.logger.info("Finished adding tasks.");
            } catch (SQLException ex) {
                Main.logger.info("There was an error getting tasks from the tasks table. Error: {}", ex.getMessage());
                JOptionPane.showMessageDialog(Main.root, "Error: Failed to get tasks.");
            }
        });
    }

    private void createTask(float bgColor, String content, boolean checked) {
        Main.logger.info("Creating task...");

        JRoundPanel bg = new JRoundPanel(60);
        bg.setLayout(new BoxLayout(bg, BoxLayout.X_AXIS));
        bg.setBackground(Color.getHSBColor(bgColor, 0.65f, 0.94f));
        bg.setMaximumSize(new Dimension(500, 60));
        bg.setFocusable(true);
        bg.setOpaque(false);
        bg.setBorder(BorderFactory.createLineBorder(Main.transparent, 2, true));
        bg.addFocusListener(new FocusAdapter() {
            @Override 
            public void focusGained(FocusEvent e) {
                bg.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2, true));
                bg.setOpaque(false);
            }

            @Override 
            public void focusLost(FocusEvent e) {
                bg.setBorder(BorderFactory.createLineBorder(Main.transparent, 2, true));
            }
        });
        InputMap focusedInputs = bg.getInputMap(JComponent.WHEN_FOCUSED);
        focusedInputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "trigger");
        focusedInputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, InputEvent.SHIFT_DOWN_MASK), "other_trigger");

        ActionMap actionMap = bg.getActionMap();
        actionMap.put("trigger", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bg.dispatchEvent(new MouseEvent(bg, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, 0, 0, 1, false, MouseEvent.BUTTON1));
            }
        });
        actionMap.put("other_trigger", new AbstractAction() {
            @Override 
            public void actionPerformed(ActionEvent e) {
                bg.dispatchEvent(new MouseEvent(bg, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, 0, 0, 1, false, MouseEvent.BUTTON3));
            }
        });

        JLabel check = new JLabel(uncheckedIcon);
        check.setSize(50, 50);
        check.setVisible(true);
        check.setBorder(BorderFactory.createEmptyBorder(0, 13, 0, 10));

        bg.addMouseListener(new MouseAdapter() {
            boolean checkState = checked;
            
            @Override
            public void mousePressed(MouseEvent e) {
                // Button order: Left, Scroll Wheel, Right
                if (e.getButton() == MouseEvent.BUTTON1) {
                    // bg.requestFocusInWindow();
                    checkState = !checkState;
                    check.setIcon(checkState ? checkedIcon : uncheckedIcon);
                    check.setOpaque(false);
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    // TODO open context menu
                    JOptionPane.showMessageDialog(Main.root, "Opening context menu.");
                }
            }
        });

        JLabel contents = new JLabel(content);
        contents.setFont(contents.getFont().deriveFont(Font.PLAIN, 30f));
        contents.setVisible(true);

        bg.add(check);
        bg.add(contents);
        tasksContainer.add(bg);
    }

    // private String point2String(Point input) {
    //     return "(x: " + Double.toString(input.getX()) + ", y: " + Double.toString(input.getY()) + ")";
    // }

    private void initSettings() {
        // TODO: Create settings
    }
}

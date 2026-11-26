package mc.jazhdo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mc.jazhdo.programs.DVD;
import mc.jazhdo.programs.Todo;

public class Main {
    private static final List<String> programs = List.of("DVD Screensaver", "Todo List", "Pomodoro Timer");
    public static final List<Program> openPrograms = new ArrayList<>();

    public static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static final Preferences prefs = Preferences.userNodeForPackage(Main.class);
    public static final Color TRANSPARENT = new Color(0x00ffffff, true);
    public static final DB db = new DB();
    public static final ClassLoader resources = Main.class.getClassLoader();
    public static final Path appDirectory = Paths.get(System.getProperty("user.home"), ".jazhdo-terra-week-6-java-gui");
    static {
        try {
            Files.createDirectories(appDirectory);
        } catch (IOException ex) {
            logger.error("Failed to create the app directory. Shutting down... Error: {}", ex.getMessage());
            System.exit(500);
        }
    }

    public static final Toolkit toolkit = Toolkit.getDefaultToolkit();
    public static final GraphicsEnvironment device = GraphicsEnvironment.getLocalGraphicsEnvironment();

    public static void main(String[] args) {
        logger.info("Initializing...");
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                logger.error("There was an error setting the default system look & feel: {}", ex.getMessage());
            }

            openMenu();

            logger.info("Initialization finished.");
        });
    }

    private static void close(JFrame frame) {
        frame.dispose();

        System.exit(0);
    }

    public static void onClose(Window window, Runnable runnable) {
        window.addWindowListener(new WindowAdapter() {
            @Override 
            public void windowClosing(WindowEvent e) {
                runnable.run();
            }
        });
    }

    private static void openMenu() {
        JFrame frame = newWindow("Java GUI Programs", 400, 300, false);
        JRootPane root = frame.getRootPane();
        Container contentPane = root.getContentPane();
        Component glassPane = root.getGlassPane();

        onClose(frame, () -> close(frame));
        addCtrlOrCmdKeyPlusWCloseKey(frame, root, () -> close(frame));

        contentPane.setLayout(new GridBagLayout());
        contentPane.addMouseListener(new MouseAdapter() {
            @Override 
            public void mousePressed(MouseEvent e) {
                root.requestFocusInWindow();
            }
        });

        JRoundPanel list = new JRoundPanel(50, false);
        list.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(Color.getHSBColor(0, 0, 0.83f));

        JLabel title = new JLabel("Choose a Program:");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        list.add(title);

        for (String programName : programs) {
            JButton button = new JButton(programName);
            button.addActionListener((event) -> {
                openPage(frame, programName);
                logger.info("Opening page: {}", programName);
            });
            list.add(button);
        }

        contentPane.add(list);
        glassPane.setVisible(true);
        frame.setVisible(true);
    }

    private static void openPage(JFrame frame, String pageName) {
        JRootPane root = frame.getRootPane();

        // Get and validate program

        Class<? extends Program> clazz = switch (programs.indexOf(pageName)) {
            case 0 -> DVD.class;
            case 1 -> Todo.class;
            case -1 -> {
                JOptionPane.showMessageDialog(root, "Error: Program " + pageName + " is invalid.");
                yield Program.class;
            }
            default -> {
                JOptionPane.showMessageDialog(root, "Error: Program " + pageName + " has not yet been created.");
                yield Program.class;
            }
        };
        if (clazz == Program.class) return;
        if (!openPrograms.stream().filter(program -> program.getClass() == clazz).toList().isEmpty()) {
            JOptionPane.showMessageDialog(root, "You can only have one window of a program open.");
            return;
        }

        try {
            openPrograms.add((Program) clazz.getConstructors()[0].newInstance(pageName));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            ex.printStackTrace();
            logger.error("There was an error instantiating the program {}. Error: {}", new Object[]{pageName, ex.getMessage()});
            JOptionPane.showMessageDialog(root, "Error: Program " + pageName + " cannot be instantiated.");
        }
    }

    public static void addCtrlOrCmdKeyPlusWCloseKey(Window window, JComponent component, Runnable onclose) {
        InputMap inputs = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actions = component.getActionMap();

        // Inputs
        inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, toolkit.getMenuShortcutKeyMaskEx()), "exit");

        // Actions
        actions.put("exit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onclose.run();
            }
        });
    }

    public static JFrame newWindow(String title, int width, int height) {
        return newWindow(title, width, height, true);
    }
    public static JFrame newWindow(String title, int width, int height, boolean resizable) {
        JFrame frame = new JFrame(title);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setResizable(resizable);
        Point center = device.getCenterPoint();
        center.x -= width / 2;
        center.y -= height / 2;
        frame.setLocation(center);
        return frame;
    }

    public static byte[] getResource(String filename) throws IOException {
        try {
            return resources.getResourceAsStream(filename).readAllBytes();
        } catch (NullPointerException ex) {
            throw new IOException(ex);
        }
    }
    public static ImageIcon getIconResource(String filename) throws IOException {
        return new ImageIcon(getResource(filename));
    }
    public static ImageIcon getScaledIconResource(String filename, int width, int height) throws IOException {
        return new ImageIcon(getIconResource(filename).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }
}

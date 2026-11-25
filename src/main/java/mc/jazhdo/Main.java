package mc.jazhdo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mc.jazhdo.programs.DVD;
import mc.jazhdo.programs.Todo;

public class Main {
    public static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final Map<String, String> programs = Map.of("dvd", "DVD Screensaver", "todo", "Todo List");
    public static final Preferences prefs = Preferences.userNodeForPackage(Main.class);
    public static final ClassLoader resources = Main.class.getClassLoader();

    public static final Toolkit toolkit = Toolkit.getDefaultToolkit();
    public static final GraphicsEnvironment device = GraphicsEnvironment.getLocalGraphicsEnvironment();

    public static final JFrame frame = new JFrame("Java GUI Programs");
    public static final JRootPane root = frame.getRootPane();
    public static final Container contentPane = root.getContentPane();
    public static final Component glassPane = root.getGlassPane();
    static {
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Point center = device.getCenterPoint();
        center.x -= 200;
        center.y -= 150;
        frame.setLocation(center);
    }

    public static void main(String[] args) {
        logger.info("Initializing...");

        initInputs();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            logger.error("There was an error getting the default system look & feel: {}", ex.getMessage());
        }

        openMenu();
        
        glassPane.setVisible(true);
        frame.setVisible(true);
        glassPane.setVisible(true);

        logger.info("Initialization finished.");
    }

    private static void openMenu() {
        contentPane.setLayout(new GridBagLayout());

        MouseAdapter resetFocus = new MouseAdapter() {
            @Override 
            public void mousePressed(MouseEvent e) {
                root.requestFocusInWindow();
            }
        };
        contentPane.addMouseListener(resetFocus);

        JRoundPanel list = new JRoundPanel(50, false);
        list.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(Color.getHSBColor(0, 0, 0.83f));

        JLabel title = new JLabel("Choose a Program:");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        list.add(title);

        for (Entry<String, String> entry : programs.entrySet()) {
            JButton button = new JButton(entry.getValue());
            button.addActionListener((event) -> {
                openPage(entry.getKey());
                logger.info("Opening page: {}", entry.getKey());
            });
            list.add(button);
        }

        contentPane.add(list);
    }

    private static void clearScreen() {
        contentPane.removeAll();
        for (MouseListener listener : contentPane.getMouseListeners()) contentPane.removeMouseListener(listener);
    }

    private static void openPage(String pageName) {
        // Get and validate program
        Class<? extends Program> clazz = switch (pageName) {
            case "dvd" -> DVD.class;
            case "todo" -> Todo.class;
            default -> Program.class;
        };
        if (clazz == Program.class) {
            JOptionPane.showMessageDialog(root, "Error: Program " + pageName + " has not yet been created.");
            return;
        }

        // Setup
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        clearScreen();

        Program program;
        try {
            program = clazz.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            logger.error("There was an error instantiating the program {}. Error: {}", new Object[]{pageName, ex.getMessage()});
            JOptionPane.showMessageDialog(root, "Error: Program " + pageName + " cannot be instantiated.");
            return;
        }

        frame.repaint();
        program.start();
    }

    private static void initInputs() {
        InputMap inputs = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actions = root.getActionMap();

        // Inputs
        inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, toolkit.getMenuShortcutKeyMaskEx()), "exit");

        // Actions
        actions.put("exit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
    }
}

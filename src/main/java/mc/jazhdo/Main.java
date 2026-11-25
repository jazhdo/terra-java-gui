package mc.jazhdo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Map.Entry;

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

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final Map<String, String> programs = Map.of("dvd", "DVD Screensaver", "todo", "Todo List");
    private static final Toolkit toolkit = Toolkit.getDefaultToolkit();
    private static final GraphicsEnvironment device = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private static final JFrame frame = new JFrame("Java GUI Things");
    private static final JRootPane root = frame.getRootPane();
    private static final Container contentPane = root.getContentPane();
    private static final Component glassPane = root.getGlassPane();
    static {
        frame.setSize(400, 300);
        // frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocation(device.getCenterPoint());

        contentPane.setLayout(new GridBagLayout());
    }

    public static void main(String[] args) {
        logger.info("Initializing...");

        initInputs();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            logger.error("There was an error getting the default system look & feel: {}", ex.getMessage());
        }

        loadInitialPage();
        
        glassPane.setVisible(true);
        frame.setVisible(true);
        glassPane.setVisible(true);

        logger.info("Initialization finished.");
    }

    private static void loadInitialPage() {
        root.getGlassPane().addMouseListener(new MouseAdapter() {
            @Override 
            public void mousePressed(MouseEvent e) {
                logger.info("Glass pane pressed.");
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

        for (Entry<String,String> entry : programs.entrySet()) {
            JButton button = new JButton(entry.getValue());
            button.addActionListener((event) -> {
                loadPage(entry.getKey());
                logger.info("Opening page: {}", entry.getKey());
            });
            list.add(button);
        }

        contentPane.add(list);
    }

    private static void loadPage(String pageName) {
        JOptionPane.showMessageDialog(root, "Hello!");
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

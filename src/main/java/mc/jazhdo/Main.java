package mc.jazhdo;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final GraphicsEnvironment device = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private static final JFrame frame = new JFrame("Java GUI Things");
    private static final JRootPane root = frame.getRootPane();
    static {
        frame.setSize(400, 300);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocation(device.getCenterPoint());
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
    }

    public static void main(String[] args) {
        logger.info("Initializing...");

        initInputs();

        JButton testButton = new JButton("Test");
        testButton.setSize(80, 30);
        testButton.setOpaque(true);
        testButton.setBorderPainted(false);
        // testButton.setContentAreaFilled(false);
        // testButton.setFocusPainted(false);
        testButton.addActionListener((event) -> {
            JOptionPane.showMessageDialog(testButton, "TEST SUCCESS");
        });
        testButton.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseEntered(MouseEvent e) {
                logger.info("Mouse entered");
                testButton.setBackground(new Color(0xDD, 0xDD, 0xDD));
                testButton.repaint();
            }
            @Override 
            public void mouseExited(MouseEvent e) {
                logger.info("Mouse exited");
                testButton.setBackground(new Color(0xEE, 0xEE, 0xEE));
                testButton.repaint();
            }
        });
        root.add(testButton);
        
        frame.setVisible(true);
        logger.info("Initialization finished.");
    }

    private static void initInputs() {
        InputMap inputs = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actions = root.getActionMap();

        // Inputs
        inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "exit");

        // Actions
        actions.put("exit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
    }
}

package mc.jazhdo;

import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

public class Main {
    private static final GraphicsEnvironment device = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private static final JFrame screen = new JFrame("Java GUI Things");
    private static final JRootPane root = screen.getRootPane();

    public static void main(String[] args) {
        screen.setSize(400, 300);
        screen.setExtendedState(Frame.MAXIMIZED_BOTH);
        screen.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        initInputs();

        JButton testButton = new JButton("Test");
        testButton.addActionListener((event) -> {
            JOptionPane.showMessageDialog(testButton, "TEST SUCCESS");
        });
        screen.add(testButton);
        
        screen.setLocation(device.getCenterPoint());
        screen.setVisible(true);
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
                screen.dispose();
            }
        });
    }
}

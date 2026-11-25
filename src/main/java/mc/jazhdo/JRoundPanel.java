package mc.jazhdo;

import java.awt.Graphics;

import javax.swing.JPanel;

public class JRoundPanel extends JPanel {
    private final int radius;
    private final boolean border;

    public JRoundPanel(int radius) {
        this(radius, false);
    }

    public JRoundPanel(int radius, boolean border) {
        this.radius = radius;
        this.border = border;
    }

    @Override 
    protected void paintComponent(Graphics oldG) {
        Graphics g = oldG.create();

        g.setColor(getBackground());
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

        if (border) {
            g.setColor(getForeground());
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        }

        g.dispose();
        super.paintComponent(g);
    }
}

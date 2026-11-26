package mc.jazhdo.programs;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import org.slf4j.Logger;

import mc.jazhdo.Main;
import mc.jazhdo.Program;

public class DVD extends Program {
    private final Logger logger = Main.logger;
    private static final String NAME = "DVD Screensaver";

    private void close(JFrame frame) {
        Main.openPrograms.remove(NAME);
        frame.dispose();
    }

    @Override
    public void start() {
        Rectangle maxSize = Main.device.getMaximumWindowBounds();
        JFrame frame = Main.newWindow(NAME, (int) maxSize.getWidth(), (int) maxSize.getHeight());
        Main.onClose(frame, () -> close(frame));
        frame.addMouseListener(new MouseAdapter() {
            @Override 
            public void mousePressed(MouseEvent e) {
                JDialog menu = new JDialog(frame);
                menu.setTitle("Settings");
                menu.setVisible(true);
                
            }
        });
        Container contentPane = frame.getContentPane();

        ImageIcon image;
        try {
            image = changeColor(Main.getIconResource("DVD_logo.png").getImage(), new Color(0xff0000));
        } catch (IOException ex) {
            logger.error("An error occurred while loading the DVD logo image: {}", ex.getMessage());
            JOptionPane.showMessageDialog(frame, "Error: Loading the DVD logo failed.");
            return;
        }

        JLabel logo = new JLabel(image);
        logo.setSize(image.getIconWidth(), image.getIconHeight());
        logo.setVisible(true);

        contentPane.setLayout(null);

        Timer loop = new Timer(10, new ActionListener() {
            private int x = 0, y = 0, vx = 1, vy = 1;
        
            @Override
            public void actionPerformed(ActionEvent e) {
                Dimension screenSize = contentPane.getSize(), logoSize = logo.getSize();
                x += vx;
                y += vy;

                if (x <= 0 || x >= screenSize.getWidth() - logoSize.getWidth()) vx = -vx;
                if (y <= 0 || y >= screenSize.getHeight() - logoSize.getHeight()) vy = -vy;

                logo.setLocation(x, y);
                logo.repaint();
            }
        });

        contentPane.add(logo);

        frame.setVisible(true);

        loop.start();
    }

    private ImageIcon changeColor(Image image, Color color) {
        BufferedImage newImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.setComposite(AlphaComposite.SrcIn);
        g.setColor(color);
        g.fillRect(0, 0, image.getWidth(null), image.getHeight(null));
        g.dispose();
        return new ImageIcon(newImage);
    }
}

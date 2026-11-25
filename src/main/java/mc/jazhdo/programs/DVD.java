package mc.jazhdo.programs;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import org.slf4j.Logger;

import mc.jazhdo.Main;
import mc.jazhdo.Program;

public class DVD extends Program {
    private final Logger logger = Main.logger;

    @Override
    public void start() {
        ImageIcon image;
        try {
            image = new ImageIcon(Main.resources.getResourceAsStream("DVD_logo.png").readAllBytes());
        } catch (IOException ex) {
            logger.error("An error occurred while loading the DVD logo image: {}", ex.getMessage());
            JOptionPane.showMessageDialog(Main.frame, "Error: Loading the DVD logo failed.");
            return;
        }

        JLabel logo = new JLabel(image);
        logo.setSize(image.getIconWidth(), image.getIconHeight());
        logo.setVisible(true);

        Main.contentPane.setLayout(null);

        Timer loop = new Timer(10, new ActionListener() {
            Dimension initialScreenSize = Main.contentPane.getSize();
            private int x = (int) (initialScreenSize.getWidth() / 2), y = (int) (initialScreenSize.getHeight() / 2);
            private int vx = 1, vy = 1;
        
            @Override
            public void actionPerformed(ActionEvent e) {
                Dimension screenSize = Main.contentPane.getSize(), logoSize = logo.getSize();
                x += vx;
                y += vy;

                if (x <= 0 || x >= screenSize.getWidth() - logoSize.getWidth()) vx = -vx;
                if (y <= 0 || y >= screenSize.getHeight() - logoSize.getHeight()) vy = -vy;

                logo.setLocation(x, y);
                logo.repaint();
            }
        });

        Main.contentPane.add(logo);
        loop.start();
    }
}

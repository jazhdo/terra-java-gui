package mc.jazhdo.programs;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mc.jazhdo.Main;
import mc.jazhdo.Program;

public class DVD extends Program {
    private int hue = Main.prefs.getInt("dvd.color.h", 0), saturation = Main.prefs.getInt("dvd.color.s", 0), brightness = Main.prefs.getInt("dvd.color.b", 0);
    private final Image dvdImage;

    public DVD(String name) {
        super(name);

        try {
            dvdImage = Main.getIconResource("DVD_logo.png").getImage();
        } catch (IOException ex) {
            throw new ExceptionInInitializerError(ex);
        }

        Container contentPane = frame.getContentPane();

        ImageIcon image = changeColor(dvdImage, Color.getHSBColor(hue / 100.0f, saturation / 100.0f, brightness / 100.0f));

        JLabel logo = new JLabel(image);
        logo.setSize(image.getIconWidth(), image.getIconHeight());
        logo.setVisible(true);

        frame.addMouseListener(new MouseAdapter() {
            @Override 
            public void mousePressed(MouseEvent e) {
                JDialog menu = new JDialog(frame);
                menu.setTitle("Settings");
                menu.setSize(550, 400);

                JPanel container = new JPanel();
                Main.addCtrlOrCmdKeyPlusWCloseKey(menu, container, () -> menu.dispose());
                container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
                container.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

                JLabel preview = new JLabel(changeColor(dvdImage, Color.getHSBColor(hue / 100.0f, saturation / 100.0f, brightness / 100.0f)));
                container.add(preview);

                JPanel hueBar = new JPanel(new BorderLayout()), saturationBar = new JPanel(new BorderLayout()), brightnessBar = new JPanel(new BorderLayout());
                JSlider hueSlider = new JSlider(JSlider.HORIZONTAL), saturationSlider = new JSlider(JSlider.HORIZONTAL), brightnessSlider = new JSlider(JSlider.HORIZONTAL);
                hueSlider.setValue(hue);
                ChangeListener changeListener = (ChangeEvent e1) -> {
                    hue = hueSlider.getValue();
                    saturation = saturationSlider.getValue();
                    brightness = brightnessSlider.getValue();
                    preview.setIcon(changeColor(dvdImage, Color.getHSBColor(hue / 100.0f, saturation / 100.0f, brightness / 100.0f)));
                };
                hueSlider.addChangeListener(changeListener);
                saturationSlider.setValue(saturation);
                saturationSlider.addChangeListener(changeListener);
                brightnessSlider.setValue(brightness);
                brightnessSlider.addChangeListener(changeListener);
                JLabel hueLabel = new JLabel("Hue"), saturationLabel = new JLabel("Saturation"), brightnessLabel = new JLabel("Brightness");
                Font labelFont = hueLabel.getFont().deriveFont(Font.BOLD, 27f);
                hueLabel.setFont(labelFont);
                saturationLabel.setFont(labelFont);
                brightnessLabel.setFont(labelFont);

                hueBar.add(hueLabel, BorderLayout.LINE_START);
                hueBar.add(hueSlider, BorderLayout.LINE_END);
                saturationBar.add(saturationLabel, BorderLayout.LINE_START);
                saturationBar.add(saturationSlider, BorderLayout.LINE_END);
                brightnessBar.add(brightnessLabel, BorderLayout.LINE_START);
                brightnessBar.add(brightnessSlider, BorderLayout.LINE_END);

                JButton save = new JButton("Save");
                save.addActionListener((ActionEvent e1) -> {
                    save.setText("Saving...");
                    saveColor();
                    image.setImage(changeColor(dvdImage, Color.getHSBColor(hue / 100.0f, saturation / 100.0f, brightness / 100.0f)).getImage());
                    menu.dispose();
                });

                addMany(container, hueBar, saturationBar, brightnessBar, save);
                menu.add(container);
                menu.setVisible(true);
            }
        });

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

    private void saveColor() {
        Main.prefs.putInt("dvd.color.h", hue);
        Main.prefs.putInt("dvd.color.s", saturation);
        Main.prefs.putInt("dvd.color.b", brightness);
    }

    private void addMany(Container container, Container... items) {
        for (Container item : items) container.add(item);
    }

    @Override
    protected Dimension getSize() {
        Rectangle maxSize = Main.device.getMaximumWindowBounds();
        return new Dimension((int) maxSize.getWidth(), (int) maxSize.getHeight());
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

    @Override
    protected void wrapUp() {
        saveColor();
    }
}

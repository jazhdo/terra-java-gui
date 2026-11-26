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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mc.jazhdo.Main;
import mc.jazhdo.Program;

public class DVD extends Program {
    private int hue = Main.prefs.getInt("dvd.color.h", 0), saturation = Main.prefs.getInt("dvd.color.s", 0), brightness = Main.prefs.getInt("dvd.color.b", 0);
    private int bgH = Main.prefs.getInt("dvd.bg.h", 1), bgS = Main.prefs.getInt("dvd.bg.s", 1), bgB = Main.prefs.getInt("dvd.bg.b", 1);
    private final Image dvdImage;
    private boolean settingsOpen = false;

    public DVD(String name) {
        super(name);

        try {
            dvdImage = Main.getIconResource("DVD_logo.png").getImage();
        } catch (IOException ex) {
            throw new ExceptionInInitializerError(ex);
        }

        Container contentPane = frame.getContentPane();
        contentPane.setBackground(Color.getHSBColor(bgH / 100.0f, bgS / 100.0f, bgB / 100.0f));
        ImageIcon image = changeColor(dvdImage, Color.getHSBColor(hue / 100.0f, saturation / 100.0f, brightness / 100.0f));

        JLabel logo = new JLabel(image);
        logo.setSize(image.getIconWidth(), image.getIconHeight());
        logo.setVisible(true);

        frame.addMouseListener(new MouseAdapter() {
            @Override 
            public void mousePressed(MouseEvent e) {
                // Only handle right clicks
                if (e.getButton() != MouseEvent.BUTTON3) return;
                if (settingsOpen) {
                    JOptionPane.showMessageDialog(frame, "Error: You cannot have more than one instance of settings open.");
                    return;
                } else settingsOpen = true;

                JDialog menu = new JDialog(frame);
                menu.setTitle("Settings");
                menu.setSize(550, 400);
                menu.setResizable(false);
                menu.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                menu.addWindowListener(new WindowAdapter() {
                    @Override 
                    public void windowClosing(WindowEvent e) {
                        settingsOpen = false;
                        menu.dispose();
                    }
                });
                menu.setLayout(new BorderLayout());

                JTabbedPane tabs = new JTabbedPane();
                Main.addCtrlOrCmdKeyPlusWCloseKey(menu, tabs, () -> {
                    settingsOpen = false;
                    menu.dispose();
                });

                JPanel dvdColor = new JPanel();
                dvdColor.setLayout(new BorderLayout());
                dvdColor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                JLabel preview = new JLabel(changeColor(dvdImage, Color.getHSBColor(hue / 100.0f, saturation / 100.0f, brightness / 100.0f)));
                dvdColor.add(preview, BorderLayout.PAGE_START);

                JSlider hueSlider = new JSlider(JSlider.HORIZONTAL), saturationSlider = new JSlider(JSlider.HORIZONTAL), brightnessSlider = new JSlider(JSlider.HORIZONTAL);
                ChangeListener changeListener = (ChangeEvent e1) -> {
                    hue = hueSlider.getValue();
                    saturation = saturationSlider.getValue();
                    brightness = brightnessSlider.getValue();
                    preview.setIcon(changeColor(dvdImage, Color.getHSBColor(hue / 100.0f, saturation / 100.0f, brightness / 100.0f)));
                };
                hueSlider.addChangeListener(changeListener);
                saturationSlider.addChangeListener(changeListener);
                brightnessSlider.addChangeListener(changeListener);

                JPanel sliders = new JPanel();
                sliders.setLayout(new BoxLayout(sliders, BoxLayout.Y_AXIS));
                sliders.add(createSlider(hueSlider, new JLabel("Hue"), hue));
                sliders.add(createSlider(saturationSlider, new JLabel("Saturation"), saturation));
                sliders.add(createSlider(brightnessSlider, new JLabel("Brightness"), brightness));
                dvdColor.add(sliders, BorderLayout.CENTER);
                tabs.addTab("DVD Logo", dvdColor);

                JPanel bgColor = new JPanel();
                bgColor.setLayout(new BoxLayout(bgColor, BoxLayout.Y_AXIS));
                bgColor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                bgColor.setBackground(Color.getHSBColor(bgH / 100.0f, bgS / 100.0f, bgB / 100.0f));

                JSlider bgHueSlider = new JSlider(JSlider.HORIZONTAL), bgSaturationSlider = new JSlider(JSlider.HORIZONTAL), bgBrightnessSlider = new JSlider(JSlider.HORIZONTAL);
                JLabel hueLabel = new JLabel("Hue"), saturationLabel = new JLabel("Saturation"), brightnessLabel = new JLabel("Brightness");
                ChangeListener listener = (ChangeEvent e1) -> {
                    bgH = bgHueSlider.getValue();
                    bgS = bgSaturationSlider.getValue();
                    bgB = bgBrightnessSlider.getValue();
                    bgColor.setBackground(Color.getHSBColor(bgH / 100.0f, bgS / 100.0f, bgB / 100.0f));
                    Color textColor = bgB > 50 ? Color.BLACK : Color.WHITE;
                    hueLabel.setForeground(textColor);
                    saturationLabel.setForeground(textColor);
                    brightnessLabel.setForeground(textColor);
                };
                bgHueSlider.addChangeListener(listener);
                bgSaturationSlider.addChangeListener(listener);
                bgBrightnessSlider.addChangeListener(listener);

                bgColor.add(createSlider(bgHueSlider, hueLabel, bgH));
                bgColor.add(createSlider(bgSaturationSlider, saturationLabel, bgS));
                bgColor.add(createSlider(bgBrightnessSlider, brightnessLabel, bgB));
                tabs.addTab("Background", bgColor);

                JButton save = new JButton("Save");
                save.addActionListener((ActionEvent e1) -> {
                    saveColor();
                    image.setImage(changeColor(dvdImage, Color.getHSBColor(hue / 100.0f, saturation / 100.0f, brightness / 100.0f)).getImage());
                    contentPane.setBackground(Color.getHSBColor(bgH / 100.0f, bgS / 100.0f, bgB / 100.0f));
                    settingsOpen = false;
                    menu.dispose();
                });
                save.setFont(save.getFont().deriveFont(Font.BOLD, 20.0f));

                menu.add(tabs, BorderLayout.CENTER);
                menu.add(save, BorderLayout.PAGE_END);
            
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

    private JPanel createSlider(JSlider slider, JLabel label, int startingValue) {
        slider.setValue(startingValue);
        slider.setPreferredSize(new Dimension(342, 47));

        label.setFont(label.getFont().deriveFont(Font.BOLD, 27f));

        JPanel bar = new JPanel(new BorderLayout());
        bar.add(label, BorderLayout.LINE_START);
        bar.add(slider, BorderLayout.LINE_END);
        bar.setBackground(new Color(0x00ffffff, true));
        bar.setOpaque(false);
        bar.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                bar.setOpaque(false);
            }
        });

        return bar;
    }

    private void saveColor() {
        Main.prefs.putInt("dvd.color.h", hue);
        Main.prefs.putInt("dvd.color.s", saturation);
        Main.prefs.putInt("dvd.color.b", brightness);

        Main.prefs.putInt("dvd.bg.h", bgH);
        Main.prefs.putInt("dvd.bg.s", bgS);
        Main.prefs.putInt("dvd.bg.b", bgB);
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
        // saveColor();
    }
}

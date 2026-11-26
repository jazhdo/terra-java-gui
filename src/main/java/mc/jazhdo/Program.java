package mc.jazhdo;

import java.awt.Dimension;

import javax.swing.JFrame;

public abstract class Program {
    protected JFrame frame;
    protected String name;

    public Program(String name) {
        this.name = name;
        frame = Main.newWindow(name, getSize().width, getSize().height);

        Main.onClose(frame, () -> close());
        Main.addCtrlOrCmdKeyPlusWCloseKey(frame, frame.getRootPane(), () -> close());
    }

    protected abstract Dimension getSize();
    protected abstract void wrapUp();

    public void close() {
        wrapUp();
        Main.openPrograms.remove(name);
        frame.dispose();
    }
}

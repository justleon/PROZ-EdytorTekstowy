package oldgui;

import javax.swing.*;

public class LocalMain {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextEditor editor = new TextEditor();
    }
}

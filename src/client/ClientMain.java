package client;
import gui.MainWindow;

import javax.swing.SwingUtilities;

/**
 * Punkt wejściowy do rozpoczęcia pracy klienta Współdzielonego Edytora Tekstowego.
 */

public class ClientMain {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {     // wątek obługi zdarzeń
            public void run() {
                MainWindow main = new MainWindow();
                main.setVisible(true);
            }
        });
    }
}
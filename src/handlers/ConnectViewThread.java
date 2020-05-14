package handlers;

import gui.ConnectView;

import java.io.IOException;

import javax.swing.JOptionPane;

/**
 * ConnectViewThread tworzy nowy wątek i gdy jest aktywny dochodzi do przełączenia ConnectView do WelcomeView.
 * Klient rozpoczyna pracę i nasłuchuje wiadomości od serwera.
 */

public class ConnectViewThread extends Thread {
    private final ConnectView connectView;

    /**
     * Konstruktor
     * @param connectView
     */

    public ConnectViewThread(ConnectView connectView) {
        this.connectView = connectView;
    }

    /**
     *  Rozpoczyna pracę klienta.
     */

    public void run() {

        try {
            connectView.getClient().start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e1){
            JOptionPane.showMessageDialog(null, "Niepoprawne argumenty!", "Error", JOptionPane.ERROR_MESSAGE);
            e1.printStackTrace();
        }
    }
}
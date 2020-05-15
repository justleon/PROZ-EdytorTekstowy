package gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import client.Client;
import debug.Debug;

/**
 * Klasa słuchacza okien.
 * Obsługa zamykania.
 */

public class ExitWindow implements WindowListener {
    private final Client client;
    private final static boolean VERBOSE = Debug.VERBOSE;

    public ExitWindow(Client client){
        this.client = client;
    }


    @Override
    public void windowOpened(WindowEvent paramWindowEvent) {

    }


    /**
     * Wysłanie wiadomości "bye" podczas zamykania okna.
     */

    @Override
    public void windowClosing(WindowEvent paramWindowEvent) {
        if(VERBOSE){
            System.out.println("Zamykanie.");
        }
        if(client != null && !client.getSocket().isClosed()){
            client.sendMessageToServer("bye");
            System.exit(0);
        }
    }


    @Override
    public void windowClosed(WindowEvent paramWindowEvent) {
    }

    @Override
    public void windowIconified(WindowEvent paramWindowEvent) {
    }

    @Override
    public void windowDeiconified(WindowEvent paramWindowEvent) {
    }

    @Override
    public void windowActivated(WindowEvent paramWindowEvent) {
    }

    @Override
    public void windowDeactivated(WindowEvent paramWindowEvent) {
    }
}
package handlers;

import client.Client;
import debug.Debug;

/**
 * Klasa WelcomeViewThread tworzy nowy wątek, który przesyła wejście z WelcomeView do serwera.
 */

public class WelcomeViewThread extends Thread {
    private static final boolean DEBUG = Debug.DEBUG;

    private final String message;
    private final Client client;

    /**
     * Konstruktor
     *
     * @param client klient
     * @param message wiadomość do serwera
     */

    public WelcomeViewThread(Client client, String message) {

        this.message = message;
        this.client = client;
    }

    /**
     * Wysłanie wiadomości do serwera.
     */

    public void run() {
        if (DEBUG) {System.out.println("sending message");}
        client.sendMessageToServer(message);
    }
}
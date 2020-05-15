package handlers;

import javax.swing.SwingWorker;

import client.Client;


/**
 *
 * Klasa MessageSwingWorker rozszerza klasę SwingWorker.
 * Wysyła wiadomości do serwera w wątku w tle.
 */

public class MessageSwingWorker extends SwingWorker<Void, Void>{
    private Client client;
    private String message;
    private boolean sent;

    /**
     * Konstruktor
     *
     * @param client klient
     * @param message wiadomość do serwera
     * @param sent - boolean, kontrola wysłania
     */

    public MessageSwingWorker(Client client, String message, boolean sent){
        this.client = client;
        this.message = message;
        this.sent = sent;
    }

    /**
     * Połączenie z serwerem w tle.
     */

    protected Void doInBackground() {
        client.sendMessageToServer(message);
        done();
        return null;
    }

    /**
     * Aktualizuje GUI po połączeniu z serwerem i kończy operację.
     */

    @Override
    protected void done() {
        client.getMainWindow().repaint();
        sent = false;

    }


}
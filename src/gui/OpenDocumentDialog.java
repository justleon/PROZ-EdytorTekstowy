package gui;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import client.Client;


/**
 * Klasa OpenDocumentView obsługuje otwarcie dokumentu z serwera.
 */

public class OpenDocumentDialog extends JOptionPane {
    private static final long serialVersionUID = 1L;

    /**
     * Tworzy nowy OpenDocumentDialog w postaci JOptionPane, z którego klient może wybrać dokument.
     * Wysyła wiadomość "open" do serwera.
     *
     * @param documentNames lista dokumentów z serwera
     * @param client klient
     */

    public OpenDocumentDialog(ArrayList<String> documentNames, Client client) {

        // jeśli nazwy są NULL - błąd
        if (documentNames == null) {
            JOptionPane.showMessageDialog(null, "Na serwerze nie ma jeszcze żadnych dokumentów.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        else {
            // w przeciwnym przypadku GUI zostanie przełączone do JOption, skąd możliwy będzie wybór dokumentu
            Object[] documentsOnServer = new Object[documentNames.size()];
            for (int i = 0; i < documentNames.size(); i++) {
                documentsOnServer[i] = documentNames.get(i);
            }
            // s - nazwa dokumentu do otwarcia
            String s = (String) JOptionPane.showInputDialog(null, "Wybierz dokument:\n", "Otwarcie dokumentu", JOptionPane.PLAIN_MESSAGE, icon, documentsOnServer, documentsOnServer[0]);

            // wysłanie wiadomości do serwera, jeśli wybór poprawny
            if (s != null) {
                client.sendMessageToServer("open " + s);
            }
        }
    }
}
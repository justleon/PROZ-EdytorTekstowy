package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import client.Client;
import debug.Debug;

/**
 * MainWindow GUI jest podklasą JFrame.
 * Jest podstawowym kontenerem dla ConnectView, DocumentView, WelcomeView, OpenDocumentDialog.
 */

public class MainWindow extends JFrame {

    private static final boolean DEBUG = Debug.DEBUG;
    private static final long serialVersionUID = 1L;
    private WelcomeView welcomeView;
    private DocumentView documentView;
    private ConnectView connectView;
    private OpenDocumentDialog openDocumentDialog;
    private ArrayList<String> documentNames;
    private Client client;
    private String username;

    /**
     * Tworzenie mainWindow.
     * Z pierwszym widokiem będącym connectView.
     * MainWindow pobiera dane od klienta poprzez ConnectView i łączy go z serwerem.
     */

    public MainWindow() {
        setTitle("Współdzielony Dokument Tekstowy");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(250, 200));
        connectView = new ConnectView(this);
        add(connectView, BorderLayout.CENTER);
        pack();
    }

    /**
     * Przełączenie z ConnectView do WelcomeView.
     */

    public void switchToWelcomeView() {
        setVisible(false);
        getContentPane().remove(connectView);

        setPreferredSize(new Dimension(350, 150));
        setMinimumSize(new Dimension(350, 150));
        setMaximumSize(new Dimension(350, 150));
        welcomeView = new WelcomeView(this, client);
        add(welcomeView, BorderLayout.CENTER);

        if (DEBUG) {
            System.out.println("Przejście do WelcomeView.");
        }
        setVisible(true);
    }

    /**
     * Wyświetlenie okna do wprowadzenia nazwy użytkownika i przesłanie wiadomości do serwera.
     */

    public void openUsernameDialog() {
        String username = JOptionPane.showInputDialog("Wprowadź nazwę użytkownika:", "");
        if(username==null){
            JOptionPane.showMessageDialog(null, "Wprowadź poprawną nazwę użytkownika!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        else{
            client.sendMessageToServer("name " + username);
        }
    }

    /**
     * Ustawianie nazwy użytkownika.
     *
     * @param name
     */

    public void setUsername(String name){
        this.username = name;
    }

    /**
     * Zwracanie nazwy użytkowika.
     *
     * @return username
     */

    public String getUsername(){
        return username;
    }

    /**
     * Przełączenie WelcomeView na DocumentView.
     */

    public void switchToDocumentView(String documentName, String documentText) {
        setVisible(false);
        removeAllViews();
        setPreferredSize(new Dimension(600, 500));
        setMinimumSize(new Dimension(600, 500));
        setMaximumSize(new Dimension(600, 500));
        documentView = new DocumentView(this, documentName, documentText);
        this.addWindowListener(new ExitWindow(client));
        getContentPane().add(documentView, BorderLayout.CENTER);
        getContentPane().validate();
        getContentPane().repaint();
        setVisible(true);
        if (DEBUG) {
            System.out.println("Przejście do DocumentView.");
        }
    }

    /**
     * Usuwanie wszystkich widoki.
     */

    private void removeAllViews() {
        if (welcomeView != null) {
            getContentPane().remove(welcomeView);
        }
        if (connectView != null) {
            getContentPane().remove(connectView);
        }
        if (documentView != null) {
            getContentPane().remove(documentView);
        }
    }

    /**
     * Wyświetlenie okna z istniejącymi dokumentami na serwerze.
     *
     * @param documentNames lista nazw dokumentów
     */

    public void displayOpenDocuments(ArrayList<String> documentNames) {
        if (DEBUG) {
            System.out.println("Przejście do OpenDocumentDialog.");
        }
        openDocumentDialog = new OpenDocumentDialog(documentNames, client);
    }

    /**
     * Wysyła polecenie do documentView w celu aktualizacji treści dokumentu.
     *
     * @param documentText treść dokumentu
     * @param editPosition pozycja zmiany
     * @param editLength długość zmiany
     * @param version wersja dokumentu
     */

    public void updateDocument(String documentText, int editPosition, int editLength, String username, int version) {
        if (DEBUG) {
            System.out.println("Aktualizacja dokumentu.");
        }
        if (documentView != null) {
            documentView.updateDocument(documentText, editPosition, editLength, username, version);
            getContentPane().repaint();
        }
    }

    /**
     * Tworzy i wyświetla okno błędu.
     *
     * @param error wiadomość błędu
     */

    public void openVersionErrorView(String error) {
        int n = JOptionPane.showConfirmDialog(null, error, "Error", JOptionPane.ERROR_MESSAGE);
        client.sendMessageToServer("open " + client.getDocumentName());
        if (DEBUG) {
            System.out.println("Wysłano wiadomość.");
        }
    }

    /**
     * Tworzy i wyświetla okno błędu.
     *
     * @param error wiadomość błędu
     */

    public void openErrorView(String error) {
        JOptionPane.showMessageDialog(null, error, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Przypisanie klienta do mainWindow.
     *
     * @param client wątek klienta
     */

    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Zwraca klienta ramki.
     */

    public Client getClient() {
        return client;
    }
}
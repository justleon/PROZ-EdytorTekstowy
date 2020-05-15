package client;

import gui.MainWindow;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import debug.Debug;

/**
 * Klasa Klienta odbiera wiadomości od serwera i przekazuje ją do ClientActionListener do interpretacji.
 */

public class Client {

    private static final boolean DEBUG = Debug.DEBUG;
    private String nameOfDocument;
    private String textOfDocument;
    private int versionOfDocument;
    private String userName;
    private ClientActionListener actionListener;
    private Socket socket;
    private int port;
    private String host;
    private PrintWriter out;
    private MainWindow mainWindow;

    /**
     * Konstruktor
     *
     * @param port port połączenia
     * @param host host połączenia
     * @param main ramka MainWindow
     */

    public Client(int port, String host, MainWindow main) {
        this.port = port;
        this.host = host;
        mainWindow = main;
    }

    /**
     * Uruchomienie welcomeView i listenera, który nasłuchuje wiadomości od serwera.
     *
     * @throws IOException
     */

    public void start() throws IOException {
        socket = new Socket(host, port);    // gniazdo
        //mainWindow.switchToWelcomeView();
        mainWindow.openUsernameDialog();
        // nasłuchiwanie serwera
        if (DEBUG){System.out.println("Klient połączył się z serwerem. ");}
        actionListener = new ClientActionListener(this, socket);
        actionListener.run();
        out = new PrintWriter(socket.getOutputStream());
    }

    /**
     * Ustawienie pola mainWindow na ramkę.
     *
     * @param frame MainWindow
     */

    public void setMainWindow(MainWindow frame) {
        this.mainWindow = frame;
    }

    /**
     * Wysyłanie wiadomości do serwera przez out.println(message)
     *
     * @param message wiadomość do serwera
     */

    public void sendMessageToServer(String message) {
        if (DEBUG) {System.out.println("Wysyłanie wiadomości do serwera.");}
        try {
            out = new PrintWriter(socket.getOutputStream());
            if (DEBUG) {System.out.println("Gniazdo " + socket.getLocalPort());}
            out.write(message + "\n");
            out.flush();
        } catch (IOException e) {
            mainWindow.openErrorView(e.getMessage());
        }
    }

    /**
     * Ustalenie nazwy użytkownika (userName) i otworzenie welcomeView.
     * @param name
     */

    public void setUsername(String name){
        System.out.println("Nadawanie nazwy użytkownika.");
        userName = name;
        mainWindow.setUsername(name);
        mainWindow.switchToWelcomeView();
    }

    /**
     * Zwracanie nazwy użytkownika
     * @return userName
     */

    public String getUsername(){
        return userName;
    }

    /**
     * Zwracanie nazwy dokumentu.
     * @return nameOfDocument
     */

    public String getDocumentName() {
        return nameOfDocument;
    }

    /**
     * Zwracanie treści dokumentu.
     * @return textOfDocument
     */

    public String getText() {
        return textOfDocument;
    }

    /**
     * Zwracanie wersji dokumentu.
     * @return versionOfDocument
     */

    public int getVersion(){
        return versionOfDocument;
    }
    /**
     * Zwracanie gniazda.
     * @return socket
     */

    public Socket getSocket() {
        return socket;
    }

    /**
     * Zwracanie mainWindow.
     * @return mainWindow
     */

    public MainWindow getMainWindow() {
        return mainWindow;
    }

    /**
     * Metoda typu mutator method that zmieniająca nazwę dokumentu.
     * Wywoływana, gdy klient otrzyma odpowiednią wiadomość od serwera.
     *
     * @param name the string that is the new name of the document
     */

    public void updateDocumentName(String name) {
        System.out.println("updating documentName");
        nameOfDocument = name;
    }

    /**
     * Metoda typu mutator method zmieniająca treść dokumentu.
     * Wywoływana, gdy klient otrzyma odpowiednią wiadomość od serwera o zmianach w treści.
     *
     * @param text nowy tekst
     */

    public void updateText(String text) {
        textOfDocument = text;
    }

    /**
     * Metoda typu mutator method zmieniająca numer wersji dokumentu.
     *
     * @param newVersion nowa wersja
     */

    public void updateVersion(int newVersion) {
        versionOfDocument = newVersion;
    }
}


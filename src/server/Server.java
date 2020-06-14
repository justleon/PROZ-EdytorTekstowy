package server;

import handlers.Edit;
import handlers.EditManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import debug.Debug;

/**
 * Serwer nasłuchuje dany port i odbiera wiadomości od klientów.
 * Uaktualnia swój stan i wysyła odpowiednią odpowiedź albo do klienta, który wysłał wiadomość,
 * albo do wszystkich aktywnych klientów w zależności od wiadomości wejściowej.
 *
 * Cały dokument jest utrzymywany na serwerze.
 *
 * Pola prywatne:
 * documentMap: mapa, która mapuje nazwę dokumentu do teksu
 * serverSocket: gniazdo serwera
 * threadList: lista wątków - każdy odpowiada jednemu klientowi
 * usernameList: lista nazw użytkowników
 * editManager: reprezentuje kolejkę zmian
 */

public class Server {
    private static final boolean DEBUG = Debug.DEBUG;
    private final Map<String, StringBuffer> documentMap;
    private final Map<String, Integer> documentVersionMap;
    private ServerSocket serverSocket;
    private ArrayList<OurThreadClass> threadList;
    private ArrayList<String> usernameList;
    private final EditManager editManager;

    /**
     * Tworzenie serwera, który słucha połączeń na porcie.
     *
     * @param port Numer portu.
     *
     * @throws IOException
     */

    public Server(int port, Map<String, StringBuffer> documents, Map<String, Integer> version) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Serwer stworzony. Słuchanie portu  " + port + '.');
        } catch (IOException e) {
            e.printStackTrace();
        }
        documentMap = Collections.synchronizedMap(documents);
        threadList = new ArrayList<OurThreadClass>();
        documentVersionMap = Collections.synchronizedMap(version);
        usernameList = new ArrayList<String>();
        editManager = new EditManager();
    }

    /**
     * Uruchom serwer, który słucha połączeń klientów i obsługuje je.
     *
     * @throws IOException jeśli problem z gniazdem
     */

    public void serve() {
        while (true) {
            try {
                // Blokowanie dopóki połączy się klient.
                Socket socket = serverSocket.accept();
                // Obsługa klienta poprzez stworzenie nowego OurThreadClass wątku dla danego klienta.
                // Dodanie wątku do listy threadList, żeby serwer mógł wysyłać wiadomości do klientów.
                OurThreadClass t = new OurThreadClass(socket, this);
                threadList.add(t);
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return documentMap, pole prywatne Serwera
     */

    public synchronized Map<String, StringBuffer> getDocumentMap() {
        return documentMap;

    }

    /**
     * Sprawdzanie, czy dana nazwa użytkownika jest już na liście.
     * @param name
     * @return false/true
     */

    public synchronized boolean nameIsAvailable(String name){
        return !usernameList.contains(name);
    }

    /**
     * Dodanie użytkownika o zadanej nazwie.
     * @param t wątek
     * @param name nazwa
     */

    public synchronized void addUsername(OurThreadClass t, String name){
        usernameList.add(name);
    }

    /**
     * @return documentVersionMap, pole prywatne Serwera
     */

    public synchronized Map<String, Integer> getDocumentVersionMap() {
        return documentVersionMap;
    }

    /**
     * @return wszystkie nazwy dokumentów z serwera jako jeden String rozdzielone spacją.
     */

    public synchronized String getAllDocuments() {
        String documentNames = "";
        for (String key : documentMap.keySet()) {
            documentNames += " " + key;
        }
        return documentNames;
    }

    /**
     * Obsługa zmian wprowadzonych przez klientów
     *
     * @param documentName nazwa edytowanego dokumentu
     * @param version numer wersji dokumentu wysłany przez klienta
     * @param offset pozycja edycji
     * @return String, który jest przekształconą wiadomością z numerem wersji i porpawionym offset, żeby pasował do bieżącego dokumentu na serwerze
     */

    public synchronized String manageEdit(String documentName, int version, int offset) {
        return editManager.manageEdit(documentName, version, offset);
    }

    /**
     * @return true jeśli documentMap jest pusta, false w przeciwnym przypadku
     */

    public synchronized boolean documentMapisEmpty() {
        return documentMap.isEmpty();
    }

    /**
     * @return true jeśli documentVersionMap jest pusta, false w przeciwnym przypadku
     */

    public synchronized boolean versionMapisEmpty() {
        return documentVersionMap.isEmpty();
    }

    /**
     * Dodanie zmiany do kolejki zmian.
     *
     * @param edit stworzona zmiana
     */

    public synchronized void logEdit(Edit edit) {
        editManager.logEdit(edit);
    }

    /**
     * Usunięcie wątku z threadList
     *
     * @param t usuwany wątek
     */

    public synchronized void removeThread(OurThreadClass t) {
        if (DEBUG) {
            System.out.println("Usuwanie wątku z Listy Wątków.");
        }
        usernameList.remove(t.getUsername());
        threadList.remove(t);
    }

    /**
     * Tworzenie nowego dokumentu i dodanie go do documentMap i do documentVersionMap z numerem 1.
     *
     * @param documentName nazwa dokumentu
     */

    public synchronized void addNewDocument(String documentName) {
        documentMap.put(documentName, new StringBuffer());
        documentVersionMap.put(documentName, 1);
        editManager.createNewlog(documentName);
    }

    /**
     * Uaktualnienie documentName w documentVersionMap.
     * Jeśli documentName nie jest jeszcze kluczem w documentVersionMap, nowa para key-value jest dodawana do mapy.
     *
     * @param documentName nazwa dokumentu
     * @param version numer wersji
     */

    public synchronized void updateVersion(String documentName, int version) {
        documentVersionMap.put(documentName, version);
    }

    /**
     * Zwracanie aktualnej wersji danego dokumentu.
     *
     * @param documentName nazwa dokumentu, którego wersja jest zwracana
     * @return numer wersji odpowiadający documentName w mapie wersji dokumentu
     */

    public synchronized int getVersion(String documentName) {
        return documentVersionMap.get(documentName);
    }

    /**
     * Usuwanie tekstu z danego dokumentu od określonego offsetu do określonego endPosition.
     * Jeśli pozycja startowa jest mniejsza od 0 albo pozycja końcowa jest mniejsza od 1 - wyjątek RuntimeException.
     *
     * @param documentName nazwa dokumentu
     * @param offset pozycja startowa teksu do usunięcia
     * @param endPosition końcowa pozycja tekstu do usunięcia
     *
     * @throws RuntimeException
     */

    public synchronized void delete(String documentName, int offset, int endPosition) {
        if (offset < 0 || endPosition < 1) {
            throw new RuntimeException("Niepoprawne argumenty.");
        }
        documentMap.get(documentName).delete(offset, endPosition);
    }

    /**
     * Wprowadzanie tekstu do określonego dokumentu od określongo offsetu.
     *
     * @param documentName nazwa dokumentu
     * @param offset pozycja startowa tekstu do wprowadzenia
     * @param text tekst do dodania
     */

    public synchronized void insert(String documentName, int offset, String text) {
        documentMap.get(documentName).insert(offset, text);
    }

    /**
     * Zwracanie stringu reprezentującego treść dokumentu
     *
     * @param documentName nazwa dokumentu
     * @return treść dokumentu
     */

    public synchronized String getDocumentText(String documentName) {
        String document = "";
        document = documentMap.get(documentName).toString();
        return document;
    }

    /**
     * Zwracanie długości danego dokumentu
     *
     * @param documentName nazwa dokumentu
     * @return długość dokumentu
     */

    public synchronized int getDocumentLength(String documentName) {
        return documentMap.get(documentName).length();
    }

    /**
     * Wysyła wiadomość od każdego innego wątku z threadList za wyjątkiem tego, który początkowo wysłał wiadomość (brak duplikatów wiadomości)
     * i wątków które są niedostępne.
     *
     * @param message String wysyłany do klientów
     * @param thread wątek wysyłający
     *
     */

    public void returnMessageToEveryOtherClient(String message, OurThreadClass thread) {
        for (OurThreadClass t : threadList) {
            if (!thread.equals(t) && !t.getSocket().isClosed()) {
                // jeśli wątek jest wciąż aktywny i nie jest to wątek wysyłający
                PrintWriter out;
                if (t.getSocket().isConnected()) {
                    synchronized (t) {
                        try {
                            // dla tych wątków otwarcie printWriter i przekazanie wiadomości do gniazda
                            out = new PrintWriter(t.getSocket().getOutputStream(), true);
                            out.println(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}

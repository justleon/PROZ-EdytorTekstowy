package server;

import handlers.Edit;
import handlers.Edit.Type;
import handlers.Encoding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import debug.Debug;

/**
 * Klasa OurThreadClass zajmuje się tworzeniem nowych wątków do obsługi klienta.
 * Kiedy wątek jest aktywny, może słuchać wiadomości, obługiwać wiadomości i wysłać z powrtotem wiadomości serwera do klienta.
 */

public class OurThreadClass extends Thread {

    private static final boolean DEBUG = Debug.DEBUG;
    final Socket socket;
    private boolean alive;
    private String username;
    private final Server server;
    private final String regex = "(bye)|(new [\\w\\d]+)|(look)|(open [\\w\\d]+)|(change .+)|(name [\\w\\d]+)";
    private final String error1 = "Error: Dokument już istnieje.";
    private final String error2 = "Error: Nie ma takiego dokumentu.";
    private final String error3 = "Error: Brak dokumentów.";
    private final String error4 = "Error: Próba wprowadzenia tekstu na niepoprawnej pozycji.";
    private final String error5 = "Error: Nazwa dokumentu musi zostać podana.";
    private final String error6 = "Error: Niepoprawne argumenty.";
    private final String error7 = "Error: Nazwa użytkownika nie jest dostępna.";
    private final boolean sleep = false; // do debugowania

    /**
     * Konstruktor
     *
     * @param socket gniazdo do połączenia z serwerem
     * @param server serwer, z którym klient chce się połączyć
     * @param alive wskazywanie, czy klient jest połączony
     */

    public OurThreadClass(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        this.alive = true;
    }

    /**
     * Uruchomienie serwera, nasłuchiwanie połączeń klientów i ich obsługa.
     *
     * @throws IOException jeśli problem z gniazdem (IOExceptions od klientów NIE zakończają serve()).
     */

    public void run() {
        try {
            handleConnection(socket);
        } catch (IOException e) {
        }
    }

    /**
     * Obsługuje jedno połączenie klienta. Return, gdy klient się rozłączy.
     *
     * Server-to-Client Message Protocol
     *
     * Message          :== (Error|Alldocs |Newdocument | Opendocument | ChangeText| Name)
     * Error            :== Error: .+
     * Alldocs          :== "alldocs " DocumentName
     * Newdocument      :== "new " DocumentName
     * Opendocument     :== "open " DocumentName UserName Version DocumentText
     * ChangeText       :== "change " DocumentName UserName Version ChangePosition ChangeLength DocumentText
     * Name             :== name UserName
     * UserName         :== Chars
     * Version          :== Int+
     * ChangePosition   :== Int+
     * ChangeLength     :== -?Int+
     * DocumentName     :== Chars
     * DocumentText     :== .+
     * Chars            :== [\\w\\d]
     * Int              :== [0-9]
     *
     * @param socket gniazdo, na którym klient jest połączony
     * @throws IOException gdy połączenie ma błąd lub kończy się niespodziewanie
     */

    private void handleConnection(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        try {
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                String output = handleRequest(line);
                // do debugowania:
                if (sleep) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // jeśli wiadomość "bye", zakończ połączenie
                if (output != null && output.equals("bye")) {
                    out.close();
                    in.close();
                    server.removeThread(this);
                }
                // jeśli wiadomość ChangText, przekaż wiadomość do wszystkich klientów
                else if (output != null && output.startsWith("change")) {
                    server.returnMessageToEveryOtherClient(output, this);
                }
                // w przeciwnym przypadku tylko do klienta wysyłającego
                if (output != null) {
                    out.println(output);
                }
            }
        } finally {
            out.close();
            in.close();
        }
    }

    /**
     * Obsługa wejścia klientów.
     * Składnia:
     * Message          :== Edit | Open | New | Look| Bye | Name
     * Edit             :== change DocumentName Username Version (Remove|Insert)
     * Remove           :== remove Position Position
     * Insert           :== insert Chars Position
     * Open             :== open DocumentName
     * New              :== new DocumentName
     * Look             :== look
     * Bye              :== "bye"
     * Name             :== name Username
     * Username         :== Chars
     * Chars            :== .+
     * Position         :== Int
     * DocumentName     :== Chars
     * Chars            :== \\d\\w
     * Version          :== [0-9]+
     * Int              :== [0-9]
     *
     * Wykonanie zmian w documenMap serwera, jeśli możliwe do wykonania.
     * Zwrot odpowiedniej wiadomości do klienta.
     *
     * @param input String będący wiadomością od klienta
     * @return String będący wiadomością do klienta
     * @throws RuntimeException
     */

    public String handleRequest(String input) {
        if (!alive) {
            throw new RuntimeException("Klient nieaktywny.");
        }
        String returnMessage = "";
        input = input.trim();
        String[] tokens = input.split(" ");
        if (DEBUG) {
            System.out.println("Wiadomość od klienta " + input);
        }
        if (!input.matches(regex)) {
            // niepoprawne wejście
            // pusta documentName
            if (tokens.length == 1 && tokens[0].equals("new")) {
                return error5;
            } else {
                return error6;
            }
        } else {
            if (tokens[0].equals("bye")) {
                // wiadomość "bye"
                alive = false;
                returnMessage = "bye";

            } else if (tokens[0].equals("new")) {
                // wiadomość "new", tworzenie nowego dokumentu, jeśli nazwa jest poprawna, w przeciwnym wypadku błąd
                String documentName = tokens[1];
                if (DEBUG) {
                    System.out.println("Tworzenie nowego dokumentu.");
                }
                if (server.getDocumentMap().containsKey(documentName)) {
                    returnMessage = error1;
                } else {
                    server.addNewDocument(documentName);
                    returnMessage = "new " + documentName;
                }
            }else if(tokens[0].equals("name")){
                if (DEBUG){System.out.println(tokens[1]);}
                if(server.nameIsAvailable(tokens[1])){
                    this.username = tokens[1];
                    server.addUsername(this, tokens[1]);
                    returnMessage = "name "+tokens[1];
                }
                else{
                    returnMessage = error7;
                }

            } else if (tokens[0].equals("look")) {
                // wiadomość "look",
                // jeśli na serwerze nie ma żadnych dokumentów - błąd, w przeciwnym przypadku zwrócenie Stringu z nazwami
                String result = "alldocs";
                if (server.documentMapisEmpty()) {
                    returnMessage = error3;
                } else {
                    result = result + server.getAllDocuments();
                    returnMessage = result;
                }

            } else if (tokens[0].equals("open")) {
                // wiadomość "open", otwieranie dokumentu, jeśli taki istnieje
                String documentName = tokens[1];
                if (!server.getDocumentMap().containsKey(documentName) || !server.getDocumentVersionMap().containsKey(documentName)) {
                    returnMessage = error2;
                } else {
                    int version = server.getVersion(documentName);
                    String documentText = Encoding.encode(server.getDocumentText(documentName));
                    returnMessage = "open " + documentName + " " + version + " " + documentText;
                }

            } else if (tokens[0].equals("change")) {
                // wiadomość "change", zmiana treści dokumentu, jeśli możliwe
                int version = Integer.parseInt(tokens[3]);
                int offset, changeLength;
                Edit edit;
                String documentName = tokens[1];
                String editType = tokens[4];
                String username = tokens[2];
                if (!server.getDocumentMap().containsKey(documentName) || !server.getDocumentVersionMap().containsKey(documentName)) {
                    // jeśli taki dokument nie istnieje
                    returnMessage = error2;
                } else {
                    Object lock = new Object();
                    // SEKCJA KRYTYCZNA
                    // Tylko jeden wątek może przebywać w tej części, ponieważ zmieniany jest numer wersji i może dojść do wyścigu.
                    synchronized (lock) {
                        if (server.getVersion(documentName) != version) {
                            // wersja klienta jest przestarzała
                            // uaktualnienie względem poprzednich zmian, żeby zmiana była możliwa
                            if(editType.equals("insert")){
                                offset = Integer.parseInt(tokens[6]);
                            } else {
                                offset = Integer.parseInt(tokens[5]);}
                                String updates = server.manageEdit(documentName,version, offset);
                                String[] updatedTokens = updates.split(" ");
                                version = Integer.parseInt(updatedTokens[1]);
                                offset = Integer.parseInt(updatedTokens[2]);
                        }
                        // serwer może wprowadzić zmiany i zwrócić wiadomość
                        int length = server.getDocumentLength(documentName);
                        if (editType.equals("remove")) {
                            offset = Integer.parseInt(tokens[5]);
                            int endPosition = Integer.parseInt(tokens[6]);
                            // zmiana treści dokumentu
                            server.delete(documentName, offset, endPosition);
                            changeLength = offset - endPosition; // negative
                            edit = new Edit(documentName, Type.REMOVE, "", version, offset, changeLength);
                            server.logEdit(edit);
                            // server updates version number:
                            server.updateVersion(documentName, version + 1);
                            // zakodowania wiadomosci
                            returnMessage = createMessage(documentName, username, version + 1, offset, changeLength, Encoding.encode(server.getDocumentText(documentName)));
                        } else if (editType.equals("insert")) {
                            Type type = Type.INSERT;
                            offset = Integer.parseInt(tokens[6]);
                            String text = Encoding.decode(tokens[5]);
                            if (offset > length) {
                                returnMessage = error4;
                            } else {
                                // zmiana treści dokumentu
                                server.insert(documentName, offset, text);
                                changeLength = text.length();
                                edit = new Edit(documentName, type, text, version, offset, changeLength);
                                server.logEdit(edit);
                                // uaktualnienie wersji dokumentu
                                server.updateVersion(documentName, version + 1);
                                returnMessage = createMessage(documentName, username, version + 1, offset, changeLength, Encoding.encode(server.getDocumentText(documentName)));
                            }
                        }
                    }
                }
            }
        }
        return returnMessage;
    }

    /**
     * Generowanie zwracanej wiadomości z podanych argumentów zgodnie ze składnią.
     * @param documentName  nazwa dokumentu
     * @param version       wersja dokumentu
     * @param offset        pozycja początku zmiany
     * @param changeLength  długość zmiany
     * @param documentText  treść dokumentu
     * @return
     */

    private String createMessage(String documentName, String username, int version, int offset, int changeLength, String documentText) {
        String message = "change " + documentName + " " +username+" "+ version + " " + offset + " " + changeLength + " " + documentText;
        return message;
    }

    /**
     * Zwrócenie gniazda klienta.
     * @return gniazdo klienta
     */

    public Socket getSocket() {
        return socket;
    }

    /**
     * Zwrócenie nazwy klienta dla tego wątku.
     * @return userName
     */

    public String getUsername() {
        return username;
    }

    /**
     * Zwraca prywatne pole alive.
     * @return alive - reprezentuje stan klienta
     */

    public boolean getAlive() {
        return alive;
    }
}
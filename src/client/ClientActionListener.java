package client;

import gui.MainWindow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import debug.Debug;

/**
 * Klasa, która słucha wiadomości serwera i je obsługuje.
 */

public class ClientActionListener {

    private static boolean DEBUG = Debug.DEBUG;
    private Client client;
    private Socket socket;
    private BufferedReader in;
    private final String regex = "(Error: .+)|"
            + "(alldocs [\\w|\\d]+)|(new [\\w|\\d]+)|(open [\\w|\\d]+\\s(\\d+)\\s?(.+)?)|"
            + "(change [\\w|\\d]+\\s[\\w|\\d]+\\s(\\d+)\\s(\\d+)\\s(-?\\d+)\\s?(.+)?)|(name [\\d\\w]+)";
    private final int groupChangeVersion = 8;
    private final int groupChangePosition = 9;
    private final int groupChangeLength = 10;
    private final int groupChangeText = 11;
    private final int groupOpenVersion = 5;
    private final int groupOpenText = 6;
    private MainWindow main;

    /**
     * Tworzenie nowego ClientActionListener.
     *
     * @param client klient
     * @param socket gniazdo
     */

    public ClientActionListener(Client client, Socket socket) {
        this.client = client;
        this.socket = socket;
        this.main = client.getMainWindow();
    }

    /**
     * Nasłuchuje wiadomości od serwera i je obsługuje.
     *
     * @throws IOException
     */

    public void run() throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        try {
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                handleMessageFromServer(line);
            }
        }
        finally {
            in.close();
        }
    }


    /**
     * Obsługuje wiadomości od serwera. Uaktualnia GUI, nazwę dokumentu, treść dokumentu...
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
     * @param input wiadomość od serwera
     * @throws IOException gdy połączenie ma błąd lub kończy się niespodziewanie
     */

    public void handleMessageFromServer(String input) {
        input = input.trim();
        if(DEBUG){ System.out.println("Wiadomość od serwera " + input);}
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (!matcher.find()) {
            // błędny format wiadomości
            main.openErrorView("Błędny format.");
        }
        String[] tokens = input.split(" ");

        // wiadomość "error"
        if (tokens[0].equals("Error:")) {
            main.openErrorView(input);
        }

        // wiadomosć "alldocs"
        else if (tokens[0].equals("alldocs")) {
            ArrayList<String> names = new ArrayList<String>();
            for (int i = 1; i < tokens.length; i++) {
                names.add(tokens[i]);
            }
            main.displayOpenDocuments(names);

        }
        else if (tokens[0].equals("name")){
            client.setUsername(tokens[1]);
        }

        // wiadomość "new"
        // tworzenie dokumentu
        else if (tokens[0].equals("new")) {
            main.switchToDocumentView(tokens[1], "");
            client.updateDocumentName(tokens[1]);
            // ustawienie wersji na 1
            client.updateVersion(1);
        }

        // wiadomość "open"
        // otwarcie dokumentu
        else if (tokens[0].equals("open")) {
            client.updateDocumentName(tokens[1]);
            // uaktualnienie wersji
            client.updateVersion(Integer.parseInt(matcher.group(groupOpenVersion)));
            String documentText = matcher.group(groupOpenText);
            client.updateText(documentText);
            if (DEBUG){System.out.println("Treść dokumentu: " + documentText);}
            main.switchToDocumentView(tokens[1], documentText);
        }

        // wiadomość "change"
        // dokonanie zmian w dokumencie
        else if (tokens[0].equals("change")) {
            // first, need to check the documents are the same
            if(DEBUG){System.out.println("Uaktualnianie dokumentu.");}
            int version = Integer.parseInt(matcher.group(groupChangeVersion));
            if (client.getDocumentName()!=null) {
                if(client.getDocumentName().equals(tokens[1]) ){
                    // dokument zmieniony
                    String username = tokens[2];
                    String documentText = matcher.group(groupChangeText);
                    if(DEBUG){System.out.println(documentText);}
                    int editPosition = Integer.parseInt(matcher.group(groupChangePosition));
                    int editLength = Integer.parseInt(matcher.group(groupChangeLength));
                    if(DEBUG){System.out.println(documentText);}
                    main.updateDocument(documentText, editPosition, editLength, username, version);
                    client.updateText(documentText);
                    client.updateVersion(version);
                }
            }
        }
    }
}
package server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import debug.Debug;

/**
 * Klasa ServerMain rozpoczyna pracę serwera, który słucha określonego portu.
 * Domyślnie jest to port o numerze 50000.
 */

public class ServerMain {
    private static final boolean DEBUG = Debug.DEBUG;
    private static final int defaultPort = 50000;
    public static int port;

    /**
     * Rozpoczęcie pracy Serwera Współdzielonego Edytora Tekstowego.
     * Punkt wejściowy do rozpoczęcia pracy serwera na określonym porcie.
     * Jeśli numer portu nie jest określony lub podany został w błędny sposób, uaktywniany jest port o numerze 6969.
     *
     * Użycie: ServerMain -p <NUMER_PORTU>
     * <NUMER_PORTU> := wybrany numer portu dla serwera
     *
     * @param args
     */

    public static void main(String[] args) {
            port = defaultPort;
        try {
            runServer(port);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Rozpoczyna pracę serwera na określonym porcie.
     * Mapy są na początku puste, jako że żaden klient jeszcze nie rozpoczął pracy na serwerze.
     *
     * @param port Numer portu, który nasłuchuje serwer - z przedziału od 0 do 65535.
     */

    public static void runServer(int port) throws IOException {
        if (DEBUG) {
            System.out.println("Jestem w runServer().");
        }
        Map<String, StringBuffer> map = new HashMap<String, StringBuffer>();    // mapa treści
        Map<String, Integer> versions = new HashMap<String, Integer>();         // mapa wersje
        Server server = new Server(port, map, versions);
        server.serve();
    }
}
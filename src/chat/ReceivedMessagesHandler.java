package chat;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Klasa obsługująca wiadomości przychodzące do użytkowników.
 */

class ReceivedMessagesHandler implements Runnable {

    // wejście serwera
    private InputStream server;

    /**
     * Konstruktor
     * @param server gniazdo
     */

    public ReceivedMessagesHandler(InputStream server) {
        this.server = server;
    }

    /**
     * Rozpoczyna obsługę wiadomości.
     * Interpretuje przychodzące wiadomości od serwera.
     */

    public void run() {
        // otrzymanie wiadomości i wyświetlenie
        Scanner s = new Scanner(server);
        String tmp = "";
        while (s.hasNextLine()) {
            tmp = s.nextLine();
            if (tmp.charAt(0) == '[') {
                tmp = tmp.substring(1, tmp.length()-1);
                System.out.println("\nLISTA UŻYTKOWNIKÓW: " + new ArrayList<String>(Arrays.asList(tmp.split(","))) + "\n"
                );
            }else{ try {
                    System.out.println("\n" + getTagValue(tmp));
                    // System.out.println(tmp);
                } catch(Exception ignore){}
            }
        }
        s.close();
    }

    /**
     * Przetwarzanie/interpretacja wiadomości.
     */

    public static String getTagValue(String xml){
        return  xml.split(">")[2].split("<")[0] + xml.split("<span>")[1].split("</span>")[0];
    }
}
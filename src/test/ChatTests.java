package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

//Aby testy chatu przeprowadzić w 100% trzeba otworzyć chat przez klienta na uruchomionym serwerze edytora tekstowego!
public class ChatTests {
    public static Socket testSocket;
    public static BufferedReader in;
    public static PrintWriter out;

    public void connectUserTest() {
        try {
            testSocket = new Socket("127.0.0.1", 50050);
            in = new BufferedReader(new InputStreamReader(testSocket.getInputStream()));
            out = new PrintWriter(testSocket.getOutputStream(), true);
            out.println("Test");
            System.out.println("Jeżeli widzisz tę wiadomość, to znaczy, że dołączenie użytkownika powiodło się!");
        } catch(Exception e) {
            System.out.println("Dołączanie nie powiodło się!");
        }
    }

    public void sendMessageTest() {
        String nick;

        try {
            out.println("To jest przykładowa wiadomość chatu! :D");
            System.out.println("Jeżeli chcesz przetestować funkcjonalność wiadomości prywatnej, wprowadź swój nick na chacie, jeżeli nie, wciśnij enter:");
            Scanner scn = new Scanner(System.in);
            nick = scn.nextLine();
            if(!(nick.isEmpty())){
                out.println("@" + nick + " Cześć!");
                System.out.println("Wysłano wiadomość prywatną.");
            }
        } catch(Exception e) {
            System.out.println("Wysyłanie wiadomości nie powiodło się!");
        }
    }
}

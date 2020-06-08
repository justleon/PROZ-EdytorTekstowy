package test;

//import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

//Do przeprowadzenia testów niezbędne jest właczenie serwera!
public class ServerTests {
    public static Socket testSocket;
    public static BufferedReader in;
    public static PrintWriter out;

    public void createThreadTest() {
        try {
            //tworzymy nowe połączenie
            testSocket = new Socket("127.0.0.1", 50000);
            in = new BufferedReader(new InputStreamReader(testSocket.getInputStream()));
            out = new PrintWriter(testSocket.getOutputStream(), true);
            System.out.println("Udało się utworzyć gniazdo: " + testSocket.isConnected() + " -> obiekt OurThreadClass utworzony");
        } catch(Exception e) {
            System.out.println("Coś poszło nie tak...");
        }
    }

    public void createNameTest() {
        try {
            //dodajemy nazwę użytkownika powiązaną z danym połączeniem
            out.println("name Test");
            String inLine = in.readLine();
            System.out.println(inLine);
        } catch (Exception e) {
            System.out.println("Takie imię już istnieje lub nie udało się go dodać!");
        }
    }

    public void createDocumentTest() {
        String inLine;

        try {
            //czy sa jakies dokumenty?
            out.println("look");
            inLine = in.readLine();
            System.out.println(inLine);
            //tworzymy nowy
            out.println("new dokument");
            inLine = in.readLine();
            System.out.println(inLine);
            //czy teraz jakies sa?
            out.println("look");
            inLine = in.readLine();
            System.out.println(inLine);
        } catch (Exception e) {
            System.out.println("Nie udało sie utworzyć nowego dokumentu!");
        }
    }

    public void openDocumentTest() {
        String inLine;

        try {
            //popelnilismy literowke i probujemy otworzyc dokument
            out.println("open dokuemnt");
            inLine = in.readLine();
            System.out.println(inLine);
            //otwarcie pliku
            out.println("open dokument");
            inLine = in.readLine();
            System.out.println(inLine);
        } catch (Exception e) {
            System.out.println("Testy otwierania nie powiodły się!");
        }

    }

    public void editDocumentTest() {
        String inLine;

        try{
            out.println("change dokument Test 1 insert To+jest+testowa+wiadomość+//// 0");
            inLine = in.readLine();
            System.out.println(inLine);
            out.println("change dokument Test 2 remove 25 30");
            inLine = in.readLine();
            System.out.println(inLine);
        } catch (Exception e) {
            System.out.println("Testy edycji pliku nie powiodły się!");
        }
    }

    public void endTests() {
        String inLine;

        try {
            out.println("bye");
            inLine = in.readLine();
            System.out.println(inLine);
        } catch( Exception e) {
            System.out.println("Nie udało sie zakończyć pracy prawidłowo!");
        }
    }
}

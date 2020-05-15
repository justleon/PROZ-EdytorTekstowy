package chat;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Klasa klienta dla funkcjonalności chatu.
 * Zlicza wszystkie instancje.
 */

class User {
    private static int nbUser = 0;
    private int userId;
    private PrintStream streamOut;
    private InputStream streamIn;
    private String nickname;
    private Socket server;
    private String color;

    /**
     * Konstruktor
     *
     * @param server  serwer
     * @param name nazwa użytkownika
     * @throws IOException
     */

    public User(Socket server, String name) throws IOException {
        this.streamOut = new PrintStream(server.getOutputStream());
        this.streamIn = server.getInputStream();
        this.server = server;
        this.nickname = name;
        this.userId = nbUser;
        this.color = ColorInt.getColor(this.userId);
        nbUser += 1;
    }

    /**
     * Zmiana koloru użytkownika.
     *
     * @param hexColor kolor w postaci 16-kowej
     */

    public void changeColor(String hexColor){
        // sprawdź poprawność
        Pattern colorPattern = Pattern.compile("#([0-9a-f]{3}|[0-9a-f]{6}|[0-9a-f]{8})");
        Matcher m = colorPattern.matcher(hexColor);
        if (m.matches()){
            Color c = Color.decode(hexColor);
            // jeśli kolor jest za jasny, to nie zmieniaj
            double luma = 0.2126 * c.getRed() + 0.7152 * c.getGreen() + 0.0722 * c.getBlue(); // per ITU-R BT.709
            if (luma > 160) {
                this.getOutStream().println("<b>Kolor za jasny.</b>");
                return;
            }
            this.color = hexColor;
            this.getOutStream().println("<b>Kolor zmieniony.</b> " + this.toString());
            return;
        }
        this.getOutStream().println("<b>Nie można zmienić koloru.</b>");
    }


    /**
     * Zwraca OutStream.
     *
     * @return streamOut
     */
    public PrintStream getOutStream(){
        return this.streamOut;
    }

    /**
     * Zwraca InputStream
     *
     * @return streamIn
     */

    public InputStream getInputStream(){ //client
        return this.streamIn;
    }

    /**
     * Zwraca nazwę użytkownika.
     *
     * @return nickname
     */

    public String getNickname(){
        return this.nickname;
    }

    /**
     * Wypisuje nazwę użytkownika w jego kolorze.
     *
     * @return użytkownika wypisanego w jego kolorze.
     */

    public String toString(){

        return "<u><span style='color:"+ this.color +"'>" + this.getNickname() + "</span></u>";
    }
}
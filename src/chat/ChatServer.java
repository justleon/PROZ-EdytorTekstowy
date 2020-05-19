package chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Klasa ChatServer rozpoczyna pracę serwera chatu, który słucha określonego portu.
 * Domyślnie jest to port 50050.
 */

public class ChatServer {
    private List<chat.User> clients;
    private int port;
    private ServerSocket server;

    public static void main(String[] args) throws IOException {
        new ChatServer(50050).run();
    }

    /**
     * Konstruktor
     *
     * @param port port
     */

    public ChatServer(int port) {
        this.port = port;
        this.clients = new ArrayList<User>();
    }

    /**
     * Rozpoczyna pracę serwera i obsługę nowych użytkowników.
     * Każdy użytkownik obsługiwany przez osobny wątek.
     *
     * @throws IOException
     */

    public void run() throws IOException {
        server = new ServerSocket(port) {
            protected void finalize() throws IOException {
                this.close();
            }
        };

        System.out.println("Port " + port + " jest otwarty.");

        while (true) {
            // akceptacja nowych użytkowików
            Socket client = server.accept();

            // ustalenie nazwy użytkownika
            String nickname = (new Scanner ( client.getInputStream() )).nextLine();
            nickname = nickname.replace(",", ""); //  ',' use for serialisation
            nickname = nickname.replace(" ", "_");
            System.out.println("Nowy klient: \"" + nickname + "\"\n\t     Host:" + client.getInetAddress().getHostAddress());

            // utworzenie użytkownika
            User newUser = new User(client, nickname);

            // dodanie użytkownika do listy odbiorców
            this.clients.add(newUser);

            // wiadomość powitania
            newUser.getOutStream().println("<b>Witaj</b> " + newUser.toString() + "!");

            // tworzenie nowego wątku dla użytkownika w celu obsługi przychodzących wiadomości
            new Thread(new UserHandler(this, newUser)).start();
        }
    }

    /**
     *  Usuwa użytkownika z listy odbiorców.
     *
     * @param user użytkownik
     */

    public void removeUser(User user){
        this.clients.remove(user);
    }

    /**
     * Rozesłanie wiadomości do wszystkich użytkowników.
     *
     * @param msg wiadomość
     * @param userSender nadawca
     */

    public void broadcastMessages(String msg, User userSender) {
        for (User client : this.clients) {
            client.getOutStream().println(
                    userSender.toString() + "<span>: " + msg+"</span>");
        }
    }

    /**
     * Wysłanie listy aktywnych użytkowników do wszystkich klientów.
     */

    public void broadcastAllUsers(){
        for (User client : this.clients) {
            client.getOutStream().println(this.clients);
        }
    }

    /**
     * Wysłanie wiadomości prywatnej.
     *
     * @param msg wiadomość
     * @param userSender nadawca
     * @param user odbiorca
     */

    public void sendMessageToUser(String msg, User userSender, String user){
        boolean find = false;
        for (User client : this.clients) {
            if (client.getNickname().equals(user) && client != userSender) {
                find = true;
                userSender.getOutStream().println(userSender.toString() + " -> " + client.toString() +": " + msg);
                client.getOutStream().println("(<b>Prywatnie</b>)" + userSender.toString() + "<span>: " + msg+"</span>");
            }
        }
        if (!find) {
            userSender.getOutStream().println(userSender.toString() + " -> (<b>nikt</b>): " + msg);
        }
    }
}

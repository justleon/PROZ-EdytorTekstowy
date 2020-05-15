package chat;

import java.util.Scanner;

/**
 *  Klasa obsługująca wątki użytkowników.
 *  Kontroluje zmiany w buforze i wysyła do wszystkich wiadomość o aktualizacji.
 */

class UserHandler implements Runnable {

    private ChatServer chatServer;
    private User user;

    /**
     * Kontruktor
     *
     * @param chatServer serwer
     * @param user użytkownik
     */
    public UserHandler(ChatServer chatServer, User user) {
        this.chatServer = chatServer;
        this.user = user;
        this.chatServer.broadcastAllUsers();
    }

    /**
     * Rozpoczęcie obsługi.
     */

    public void run() {
        String message;

        // kiedy pojawi się nowa wiadomość, wyślij do wszystkich
        Scanner sc = new Scanner(this.user.getInputStream());
        while (sc.hasNextLine()) {
            message = sc.nextLine();

            // emotki
            message = message.replace(":)", "<img src='http://4.bp.blogspot.com/-ZgtYQpXq0Yo/UZEDl_PJLhI/AAAAAAAADnk/2pgkDG-nlGs/s1600/facebook-smiley-face-for-comments.png'>");
            message = message.replace(":D", "<img src='http://2.bp.blogspot.com/-OsnLCK0vg6Y/UZD8pZha0NI/AAAAAAAADnY/sViYKsYof-w/s1600/big-smile-emoticon-for-facebook.png'>");
            message = message.replace(":d", "<img src='http://2.bp.blogspot.com/-OsnLCK0vg6Y/UZD8pZha0NI/AAAAAAAADnY/sViYKsYof-w/s1600/big-smile-emoticon-for-facebook.png'>");
            message = message.replace(":(", "<img src='http://2.bp.blogspot.com/-rnfZUujszZI/UZEFYJ269-I/AAAAAAAADnw/BbB-v_QWo1w/s1600/facebook-frown-emoticon.png'>");
            message = message.replace("-_-", "<img src='http://3.bp.blogspot.com/-wn2wPLAukW8/U1vy7Ol5aEI/AAAAAAAAGq0/f7C6-otIDY0/s1600/squinting-emoticon.png'>");
            message = message.replace(";)", "<img src='http://1.bp.blogspot.com/-lX5leyrnSb4/Tv5TjIVEKfI/AAAAAAAAAi0/GR6QxObL5kM/s400/wink%2Bemoticon.png'>");
            message = message.replace(":P", "<img src='http://4.bp.blogspot.com/-bTF2qiAqvi0/UZCuIO7xbOI/AAAAAAAADnI/GVx0hhhmM40/s1600/facebook-tongue-out-emoticon.png'>");
            message = message.replace(":p", "<img src='http://4.bp.blogspot.com/-bTF2qiAqvi0/UZCuIO7xbOI/AAAAAAAADnI/GVx0hhhmM40/s1600/facebook-tongue-out-emoticon.png'>");
            message = message.replace(":o", "<img src='http://1.bp.blogspot.com/-MB8OSM9zcmM/TvitChHcRRI/AAAAAAAAAiE/kdA6RbnbzFU/s400/surprised%2Bemoticon.png'>");
            message = message.replace(":O", "<img src='http://1.bp.blogspot.com/-MB8OSM9zcmM/TvitChHcRRI/AAAAAAAAAiE/kdA6RbnbzFU/s400/surprised%2Bemoticon.png'>");

            // zarządzanie wiadomościami prywatnymi
            if (message.charAt(0) == '@'){
                if(message.contains(" ")){
                    System.out.println("Wiadomość prywatna: " + message);
                    int firstSpace = message.indexOf(" ");
                    String userPrivate= message.substring(1, firstSpace);
                    chatServer.sendMessageToUser(message.substring(firstSpace+1, message.length()), user, userPrivate
                    );
                }

                // instrukcja zmiany koloru
            }else if (message.charAt(0) == '#'){
                user.changeColor(message);
                // aktualizaja kolorów wszystkich użytkowników
                this.chatServer.broadcastAllUsers();
            }else{
                // aktualizacja listy użytkowników
                chatServer.broadcastMessages(message, user);
            }
        }

        // koniec wątku
        chatServer.removeUser(user);
        this.chatServer.broadcastAllUsers();
        sc.close();
    }
}
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Chat {
    //ramka chatu
    JFrame chatFrame = new JFrame("Chat");

    //ramka powitania
    JFrame loginFrame;

    //bufor wiadomości
    JTextField messageBuffer;

    //okno wiadomości
    JTextArea chatBox;

    //pole tekstowe wyboru nazwy użytkownika
    JTextField usernameChooser;

    //nazwa uzytkownika
    String username;

    //ramka powitania
    public void LoginWindow() {

        //ramka chatu - niewidoczna
        chatFrame.setVisible(false);

        loginFrame = new JFrame("Chat");
        usernameChooser = new JTextField();

        JLabel chooseUsernameLabel = new JLabel("Wprowadź swoje imię (min. 3 znaki): ");

        //przycisk wejscia do chatu
        JButton enterChat = new JButton("Rozpocznij Chatowanie");

        //pole na imie
        JPanel loginPanel = new JPanel(new GridBagLayout());

        //dla chooseUsernameLabel
        GridBagConstraints loginRight = new GridBagConstraints();
        loginRight.gridx = 0;
        loginRight.gridy = 1;
        loginRight.fill = GridBagConstraints.HORIZONTAL;
        loginRight.gridwidth = GridBagConstraints.REMAINDER;

        //dla usernameChooser
        GridBagConstraints loginLeft = new GridBagConstraints();
        loginLeft.gridx = 0;
        loginLeft.gridy = 0;

        //dodanie elementow do panelu
        loginPanel.add(chooseUsernameLabel, loginLeft);
        loginPanel.add(usernameChooser, loginRight);

        //dodanie panelu i przycisku do ramki
        loginFrame.add(BorderLayout.CENTER, loginPanel);
        loginFrame.add(BorderLayout.SOUTH, enterChat);
        loginFrame.setVisible(true);
        loginFrame.setSize(250, 100);

        //listener przycisku
        enterChat.addActionListener(new enterServerButtonListener());
    }

    public void ChatWindow() {

        JButton sendMessage;

        //ramka chatu - widoczna
        chatFrame.setVisible(true);

        //panel bufora
        JPanel bufforPanel = new JPanel();
        chatFrame.add(BorderLayout.SOUTH, bufforPanel);
        bufforPanel.setLayout(new GridBagLayout());

        //bufor wiadomosci
        messageBuffer = new JTextField();

        //przycisk wyslania
        sendMessage = new JButton("Wyslij Wiadomość");

        //pole wiadomosci
        chatBox = new JTextArea();
        chatBox.setEditable(false);
        chatFrame.add(new JScrollPane(chatBox), BorderLayout.CENTER);
        chatBox.setLineWrap(true);
        chatBox.setFont(new Font("Serif", Font.PLAIN, 15));

        //dla bufora wiadomosci
        GridBagConstraints left = new GridBagConstraints();
        left.anchor = GridBagConstraints.WEST;
        left.fill = GridBagConstraints.HORIZONTAL;
        left.gridwidth = GridBagConstraints.REMAINDER;

        //dla przycisku wyslania
        GridBagConstraints right = new GridBagConstraints();
        right.anchor = GridBagConstraints.EAST;
        right.weightx = 2.0;

        //dodanie bufora wiadomosci i przycisk wyslania
        bufforPanel.add(messageBuffer, left);
        bufforPanel.add(sendMessage, right);

        //listener przycisku wyslania
        sendMessage.addActionListener(new sendMessageButtonListener());

        chatFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        chatFrame.setSize(450, 300);
    }

    class sendMessageButtonListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {

            if (messageBuffer.getText().length() < 1) {
                //nic nie rob

            } else if (messageBuffer.getText().equals(".reset")) {
                chatBox.setText("Okno czatu zostało zresetowane\n");
                messageBuffer.setText("");
                //wyczysc okno chatu

            } else {
                chatBox.append("<" + username + ">:  " + messageBuffer.getText() + "\n");
                messageBuffer.setText("");
                //wyslij wiadomosc
            }
        }
    }

    class enterServerButtonListener implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            username = usernameChooser.getText();
            if (username.length() < 3) {
                System.out.println("Wprowadzony ciąg znaków nie spełnia wymagań minimalnej długości imienia");
                //bledne imie

            } else {
                loginFrame.setVisible(false);
                ChatWindow();
                //przejdz do okna chatu
            }
        }
    }
}

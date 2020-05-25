package chat;

import debug.Debug;
import gui.DocumentView;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import javax.swing.*;
import javax.swing.text.html.*;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Klasa głównego widoku chatu.
 */

public class ChatView extends Thread{

    final JTextPane jtextFilDiscu = new JTextPane();
    final JTextPane jtextListUsers = new JTextPane();
    final JTextField jtextInputChat = new JTextField();
    private String oldMsg = "";
    private Thread read;
    private String serverName;
    private int PORT;
    private String name;
    BufferedReader input;
    PrintWriter output;
    Socket server;

    private JFrame jfr;
    private JLabel jlName;
    private JTextField jtfPort;
    private JLabel jlPort;
    private JLabel jlAddr;
    private JButton jcbtn;
    private JButton jsbtn;
    private JScrollPane jtextInputChatSP;
    private JButton jsbtndeco;

    /**
     * Konstruktor
     */

    public ChatView(String username, String host) {

        this.serverName = host;
        this.name = username;
        this.PORT = 50050;

        String fontfamily = "Arial, sans-serif";
        Font font = new Font(fontfamily, Font.PLAIN, 15);

        jfr = new JFrame("Chat Współdzielonego Dokumentu Tekstowego");
        jfr.getContentPane().setLayout(null);
        jfr.setSize(700, 500);
        jfr.setResizable(false);
        // użytkownik nie może zamknąć okna zanim się połączy
        // dopiero po połączeniu można zamknąć okno chatu
        jfr.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // okno wiadomości
        jtextFilDiscu.setBounds(25, 25, 490, 320);
        jtextFilDiscu.setFont(font);
        jtextFilDiscu.setMargin(new Insets(6, 6, 6, 6));
        jtextFilDiscu.setEditable(false);
        JScrollPane jtextFilDiscuSP = new JScrollPane(jtextFilDiscu);
        jtextFilDiscuSP.setBounds(25, 25, 490, 320);

        jtextFilDiscu.setContentType("text/html");
        jtextFilDiscu.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);

        // lista użytkowników
        jtextListUsers.setBounds(520, 25, 156, 320);
        jtextListUsers.setEditable(true);
        jtextListUsers.setFont(font);
        jtextListUsers.setMargin(new Insets(6, 6, 6, 6));
        jtextListUsers.setEditable(false);
        JScrollPane jsplistuser = new JScrollPane(jtextListUsers);
        jsplistuser.setBounds(520, 25, 156, 320);

        jtextListUsers.setContentType("text/html");
        jtextListUsers.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);

        // bufor wiadomości
        jtextInputChat.setBounds(0, 350, 400, 50);
        jtextInputChat.setFont(font);
        jtextInputChat.setMargin(new Insets(6, 6, 6, 6));
        jtextInputChatSP = new JScrollPane(jtextInputChat);
        jtextInputChatSP.setBounds(25, 350, 650, 50);

        // przycisk "Wyślij"
        jsbtn = new JButton("Wyślij");
        jsbtn.setFont(font);
        jsbtn.setBounds(575, 410, 100, 35);

        // przycisk "Rozłącz"
        jsbtndeco = new JButton("Rozłącz");
        jsbtndeco.setFont(font);
        jsbtndeco.setBounds(25, 410, 130, 35);

        jtextInputChat.addKeyListener(new KeyAdapter() {
            // wysyłanie po wciśnięciu Enter
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }

                // pobranie ostatniej wiadomości po wciśnięciu strzałki w górę
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    String currentMessage = jtextInputChat.getText().trim();
                    jtextInputChat.setText(oldMsg);
                    oldMsg = currentMessage;
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    String currentMessage = jtextInputChat.getText().trim();
                    jtextInputChat.setText(oldMsg);
                    oldMsg = currentMessage;
                }
            }
        });

        // wciśnięcie przycisku "Wyślij"
        jsbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                sendMessage();
            }
        });

        // widok połączenia
        jlName = new JLabel("Imię: " + this.name);
        jlPort = new JLabel("Port: ");
        jtfPort = new JTextField(Integer.toString(this.PORT));
        jlAddr = new JLabel("Host: " + this.serverName);
        jcbtn = new JButton("Połącz");

        // pozycje modułów
        jcbtn.setFont(font);
        jlAddr.setBounds(25, 380, 135, 40);
        jlName.setBounds(375, 380, 135, 40);
        jlPort.setBounds(200, 380, 40, 40);
        jtfPort.setBounds(240, 380, 75, 40);
        jcbtn.setBounds(575, 380, 100, 40);

        // kolor
        jtextFilDiscu.setBackground(Color.LIGHT_GRAY);
        jtextListUsers.setBackground(Color.LIGHT_GRAY);

        // dodanie elementów
        jfr.add(jcbtn);
        jfr.add(jtextFilDiscuSP);
        jfr.add(jsplistuser);
        jfr.add(jlName);
        jfr.add(jlPort);
        jfr.add(jtfPort);
        jfr.add(jlAddr);
        jfr.setVisible(true);


        // informacja o chacie
        appendToPane(jtextFilDiscu, "<h2>Możliwe komendy:</h2>"
                +"<b>@nazwaużytkownika</b>, aby wysłać prywatną wiadomości do użytkownika 'nazwaużytkownika'" + "<br>"
                +"<b>:)</b> emotikony" + "<br>"
                +"<b>strzałka w górę</b>, aby pobrać ostatnio wpisaną wiadomość" + "<br>");

        // w chwili połączenia
        jcbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    //name = jlName.getText();
                    String port = jtfPort.getText();
                    //serverName = jlAddr.getText();
                    //PORT = Integer.parseInt(port);

                    appendToPane(jtextFilDiscu, "<span>Łączenie z " + serverName + " na porcie " + PORT + "...</span>");
                    server = new Socket(serverName, PORT);

                    appendToPane(jtextFilDiscu, "<span>Połączony z " +
                            server.getRemoteSocketAddress()+"</span>");

                    input = new BufferedReader(new InputStreamReader(server.getInputStream()));
                    output = new PrintWriter(server.getOutputStream(), true);

                    // prześlij nazwę użytkownika na serwer
                    output.println(name);

                    // stwórz nowy wątek czytający
                    read = new Read();
                    read.start();

                    //zmiana widoku
                    jfr.remove(jlName);
                    jfr.remove(jlPort);
                    jfr.remove(jtfPort);
                    jfr.remove(jlAddr);
                    jfr.remove(jcbtn);
                    jfr.add(jsbtn);
                    jfr.add(jtextInputChatSP);
                    jfr.add(jsbtndeco);
                    jfr.revalidate();
                    jfr.repaint();
                    jtextFilDiscu.setBackground(Color.WHITE);
                    jtextListUsers.setBackground(Color.WHITE);
                } catch (Exception ex) {
                    appendToPane(jtextFilDiscu, "<span>Nie można połączyć z serwerem.</span>");
                    JOptionPane.showMessageDialog(jfr, ex.getMessage());
                }
            }

        });

        // podczas rozłączenia
        jsbtndeco.addActionListener(new ActionListener()  {
            public void actionPerformed(ActionEvent ae) {
                //zmiana widoku
                disconnect();
            }
        });

        // podczas zamknięcia okna chatu wykonuje się najpierw rozłączenie
        jfr.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    disconnect();
                } catch (NullPointerException ex) {
                    if(Debug.DEBUG){ System.out.println("Chat zamknięty przed nawiązaniem połączenia.");}
                }
                DocumentView.clientChatIsOpen = false;
                System.out.println("Chat zamknięty.");
                    e.getWindow().dispose();
                }
        });

    }

    /**
     * Funkcja służąca do rozłączania chatu.
     */

    public void disconnect() throws NullPointerException{
        jfr.add(jlName);
        jfr.add(jtfPort);
        jfr.add(jlPort);
        jfr.add(jlAddr);
        jfr.add(jcbtn);
        jfr.remove(jsbtn);
        jfr.remove(jtextInputChatSP);
        jfr.remove(jsbtndeco);
        jfr.revalidate();
        jfr.repaint();
        read.interrupt();
        jtextListUsers.setText(null);
        jtextFilDiscu.setBackground(Color.LIGHT_GRAY);
        jtextListUsers.setBackground(Color.LIGHT_GRAY);
        appendToPane(jtextFilDiscu, "<span>Połączenie zamknięte.</span>");
        output.close();
    }


    /**
     * Wysyłanie wiadomości.
     */
    public void sendMessage() {
        try {
            String message = jtextInputChat.getText().trim();
            if (message.equals("")) {
                return;
            }
            this.oldMsg = message;
            output.println(message);
            jtextInputChat.requestFocus();
            jtextInputChat.setText(null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            System.exit(0);
        }
    }

    /**
     * Klasa odpowiadająca za czytanie nowych wiadomości.
     */

    class Read extends Thread {
        public void run() {
            String message;
            while(!Thread.currentThread().isInterrupted()){
                try {
                    message = input.readLine();
                    if(message != null){
                        if (message.charAt(0) == '[') {
                            message = message.substring(1, message.length()-1);
                            ArrayList<String> ListUser = new ArrayList<String>(Arrays.asList(message.split(", ")));
                            jtextListUsers.setText(null);
                            for (String user : ListUser) {
                                appendToPane(jtextListUsers, "@" + user);
                            }
                        }else{
                            appendToPane(jtextFilDiscu, message);
                        }
                    }
                }
                catch (IOException ex) {
                    System.err.println("Nie można odebrać wiadomości.");
                }
            }
        }
    }

    /**
     * Przetwarza tekst na porządany format.
     *
     * @param tp JTextPane
     * @param msg wiadomość
     */

    private void appendToPane(JTextPane tp, String msg){
        HTMLDocument doc = (HTMLDocument)tp.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit)tp.getEditorKit();
        try {
            editorKit.insertHTML(doc, doc.getLength(), msg, 0, 0, null);
            tp.setCaretPosition(doc.getLength());
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}

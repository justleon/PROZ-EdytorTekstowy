package gui;

import chat.ChatServer;
import chat.ChatView;
import handlers.Encoding;
import handlers.MessageSwingWorker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;

import client.Client;
import debug.Debug;

/**
 * Klasa okna edycji dokumentu.
 */

public class DocumentView extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final boolean DEBUG = Debug.DEBUG;
    private JFrame frame;
    private JMenuBar menu;
    private JMenu file, edit, chat;
    private JMenuItem newfile, open, exit, copy, cut, paste, connect, disconnect;
    private JLabel documentNameLabel;
    private String documentName, documentText;
    private JTextArea area;
    private JScrollPane scrollpane;
    private DefaultCaret caret;
    private TextDocumentListener documentListener;
    private final Client client;
    private final String username;
    private int currentVersion;
    private boolean sent = false; // do zarządzania kursorem

    /**
     * Tworzenie nowego DocumentView.
     */

    public DocumentView(MainWindow frame) {
        this.frame = frame;
        this.client = null;
        this.username = "";
        documentNameLabel = new JLabel("Edycja dokumentu: ");
        createLayout();
    }

    /**
     * Tworzenie nowego DocumentView.
     *
     * @param frame główny kontener
     * @param documentName nazwa dokumentu
     * @param text treść dokumentu
     */

    public DocumentView(MainWindow frame, String documentName, String text) {
        if (DEBUG) {
            System.out.println("Tworzenie widoku dokumentu.");
        }
        this.frame = frame;
        this.client = frame.getClient();
        this.documentName = documentName;
        this.username = frame.getUsername();
        documentText = Encoding.decode(text);
        documentNameLabel = new JLabel("<html><B>"+documentName+"</B></html>");
        createLayout();
    }

    /**
     * Elementy, layout, listenerzy.
     */

    private void createLayout() {

        menu = new JMenuBar();
        file = new JMenu("Plik");
        edit = new JMenu("Edycja");
        chat = new JMenu("Chat");
        menu.add(file);
        menu.add(edit);
        menu.add(chat);

        newfile = new JMenuItem("Nowy");
        newfile.addActionListener(new NewFileListener());
        file.add(newfile);

        copy = new JMenuItem("Kopiuj");
        copy.addActionListener(new CopyListener());
        edit.add(copy);

        cut = new JMenuItem("Wytnij");
        cut.addActionListener(new CutListener());
        edit.add(cut);

        paste = new JMenuItem("Wklej");
        paste.addActionListener(new PasteListener());
        edit.add(paste);

        open = new JMenuItem("Otwórz");
        open.addActionListener(new OpenFileListener());
        file.add(open);

        exit = new JMenuItem("Wyjdź");
        exit.addActionListener(new ExitFileListener());
        file.add(exit);

        connect = new JMenuItem("Chatuj");
        connect.addActionListener(new ConnectListener());
        chat.add(connect);
        frame.setJMenuBar(menu);

        caret = new DefaultCaret();
        area = new JTextArea(25, 65);
        area.setLineWrap(true);
        area.setText(documentText);
        area.setWrapStyleWord(true);


        area.setCaret(caret);
        documentListener = new TextDocumentListener();
        area.getDocument().addDocumentListener(documentListener);

        scrollpane = new JScrollPane(area);
        scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup().addComponent(documentNameLabel).addComponent(scrollpane));
        layout.setVerticalGroup(layout.createSequentialGroup().addComponent(documentNameLabel).addComponent(scrollpane));
    }


    /**
     * Klasa DocumentListener słuchacza dla dokumentu w GUI.
     */

    private class TextDocumentListener implements DocumentListener {

        /**
         * Wysyłanie wiadomości "edit", typ zmiany: insert.
         */

        public void insertUpdate(DocumentEvent e) {
            synchronized (area) {
                int changeLength = e.getLength();
                int offset = e.getOffset();
                int insert = caret.getDot();
                String message;
                try {
                    String addedText = area.getDocument().getText(offset, changeLength);
                    String encodedText = Encoding.encode(addedText);
                    currentVersion=client.getVersion();
                    message = "change " + documentName + " "+username+" "+ currentVersion+ " insert " + encodedText + " " + insert;
                    if(DEBUG){
                        System.out.println(message);
                    }
                    sent = true;
                    MessageSwingWorker worker = new MessageSwingWorker(client,
                            message, sent);
                    worker.execute();
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
            }
        }

        /**
         * Wysyłanie wiadomości "edit", typ zmiany: remove
         */

        public void removeUpdate(DocumentEvent e) {
            synchronized (area) {
                int changeLength = e.getLength();
                currentVersion=client.getVersion();
                int offset = e.getOffset();
                int endPosition = offset + changeLength;
                String message = "change " + documentName +" "+username+" " +currentVersion+" remove " + offset + " " + endPosition;
                if(DEBUG){
                    System.out.println(message);
                }
                sent = true;
                MessageSwingWorker worker = new MessageSwingWorker(client, message, sent);
                client.updateVersion(currentVersion+1);
                worker.execute();
            }
        }

        public void changedUpdate(DocumentEvent e) {
            // pusty składnik - nie uruchamiaj
        }
    }



    /**
     * Zarządanie offsetem na podstawie pozycji aktualnej kursora, pozycji zmiany i jej długości.
     *
     * @param currentPos aktualna pozycja
     * @param pivotPosition pozycja zmiany
     * @param amount długość zmiany
     */

    private void manageCursor(int currentPos, int pivotPosition, int amount) {
        if(DEBUG){
            System.out.println("Aktualna pozycja: "+caret.getDot());
            System.out.println("Pozycja zmiany: "+pivotPosition);
            System.out.println("Długość zmiany: "+amount);
        }

        if (currentPos >= pivotPosition) {
            if (currentPos <= pivotPosition + Math.abs(amount)) {
                caret.setDot(pivotPosition);
            } else {
                caret.setDot(amount+currentPos);
            }
        }
        else{
            caret.setDot(currentPos);
        }
        if(DEBUG){
            System.out.println("caret moved to: "+caret.getDot());
        }
    }

    /**
     * Dekodowanie i aktualizacja dokumentu na podstawie wiadomości z serwera.
     * Zarządzanie pozycją offestu.
     *
     * @param updatedText zakodowana wiadomość
     * @param editPosition offset z serwera
     * @param editLength długość zmiany z serwera
     * @param version wersja zmiany
     */

    public void updateDocument(String updatedText, int editPosition, int editLength, String username, int version) {
        documentText = Encoding.decode(updatedText);
        int pos = caret.getDot();
        synchronized (area) {
            if(this.username!=null && !this.username.equals(username)){
                area.getDocument().removeDocumentListener(documentListener);
                area.setText(documentText);
                area.getDocument().addDocumentListener(documentListener);
                manageCursor(pos, editPosition, editLength);
            }
            else if(this.username!=null && this.username.equals(username)) {
                // sprawdzanie zgodności wersji
                if(currentVersion<version-1){
                    area.getDocument().removeDocumentListener(documentListener);
                    area.setText(documentText);
                    area.getDocument().addDocumentListener(documentListener);
                    caret.setDot(editPosition+editLength);
                }
            }
        }
    }

    /**
     * Słuchacz przycisku "Nowy" z JMenu.
     */

    private class NewFileListener implements ActionListener {

        /**
         * Wysłanie wiadomości "new" do serwera, gdy klient utworzy dokument.
         */

        public void actionPerformed(ActionEvent e) {
            String newDocumentName = JOptionPane.showInputDialog("Wprowadź nazwę dokumentu:", "");
            // Jeśli nie zostało wciśnięte "anuluj", to wiadomość "new" musi być wysłana do serwera.
            if (newDocumentName !=null){
                String message = "new " + newDocumentName;
                MessageSwingWorker worker = new MessageSwingWorker(client, message, true);
                worker.execute();
            }
        }
    }

    /**
     * Słuchacz przycisku "Otwórz" z JMenu.
     */

    private class OpenFileListener implements ActionListener {

        /**
         * Wysłanie wiadomości "look" do serwera i w odpowiedzi wyświetlenie listy dokumentów z serwera do otwarcia.
         */

        public void actionPerformed(ActionEvent e) {
            // wiadomość do klienta, lista documentNames
            client.sendMessageToServer("look");
        }
    }


    /**
     * Słuchacz przycisku "Wyjdź" z JMenu.
     */

    private class ExitFileListener implements ActionListener {

        /**
         * Wyświetlenie JOptionPane z pytaniem o potwierdzenie wyjścia.
         * W przypadku potwierdzenia GUI się zamyka i klient zostaje rozłączony od serwera.
         */

        public void actionPerformed(ActionEvent e) {
            int n = JOptionPane.showConfirmDialog(null, "Zakończyć pracę Współdzielonego Dokumentu Tekstowego?", "Exit", JOptionPane.YES_NO_OPTION);
            if (n == 0) {
                if(!client.getSocket().isClosed()) {
                    client.sendMessageToServer("bye");
                }
                System.exit(0);
            }
        }
    }

    /**
     * Słuchacz przycisku "Kopiuj" z JMenu.
     */

    private class CopyListener implements ActionListener {

        /**
         * Kopiuje wybrany tekst.
         */

        public void actionPerformed(ActionEvent e) {
            area.copy();
        }
    }

    /**
     * Słuchacz przycisku "Wklej" z JMenu.
     */

    private class PasteListener implements ActionListener {

        /**
         * Wkleja wybrany tekst.
         */

        public void actionPerformed(ActionEvent e) {
            area.paste();
        }
    }

    /**
     * Słuchacz przycisku "Wytnij" z JMenu.
     */

    private class CutListener implements ActionListener {

        /**
         * Wycina wybrany tekst.
         */

        public void actionPerformed(ActionEvent e) {
            area.cut();
        }
    }

    /**
     * Słuchacz przycisku "Chatuj" z JMenu.
     */

    private class ConnectListener implements ActionListener {

        /**
         * Otwiera okno chatu.
         */

        public void actionPerformed( ActionEvent e) {
            ChatView clientChat = new ChatView(username, client.getHost());
        }
    }
}
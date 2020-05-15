package gui;

import handlers.WelcomeViewThread;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import client.Client;


/**
 * WelcomeView witający klientów po połączeniu z serwerem.
 */

public class WelcomeView extends JPanel implements ActionListener {
    private final MainWindow frame;
    private JLabel welcomeLabel;
    private JLabel createNewLabel;
    private JTextField documentName;
    private JButton createNewButton, openDocumentButton;
    private Client client;

    /**
     * Tworzenie WelcomeView.
     *
     * @param frame podstawowy kontener
     * @param client klient
     */

    public WelcomeView(MainWindow frame, Client client) {
        this.frame = frame;
        this.client = client;
        welcomeLabel = new JLabel("Rozpocznij pracę ze Współdzielonym Edytorem Tekstowym!");
        System.out.println("Tworzenie WelcomeView.");
        createNewLabel = new JLabel("Wprowadź nazwę dokumentu:");
        documentName = new JTextField();
        documentName.addActionListener(this);
        createNewButton = new JButton("Stwórz nowy dokument");
        createNewButton.addActionListener(this);

        openDocumentButton = new JButton("Otwórz dokument z serwera");
        openDocumentButton.addActionListener(this);

        frame.setSize(600,200);

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup().addComponent(welcomeLabel).addComponent(createNewLabel).addComponent(documentName, 100, 150, Short.MAX_VALUE).addGroup(layout.createSequentialGroup().addComponent(createNewButton).addComponent(openDocumentButton)));
        layout.setVerticalGroup(layout.createSequentialGroup().addComponent(welcomeLabel).addComponent(createNewLabel).addComponent(documentName, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE).addGroup(layout.createParallelGroup().addComponent(createNewButton).addComponent(openDocumentButton)));
    }

    /**
     * Listener dla pola documentName.
     * Sprawdza poprawność wprowadzonej nazwy.
     * Tworzy nowy wątek WelcomeView, który wysyła wiadomość "new" do serwera.
     */

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == createNewButton || e.getSource() == documentName) {
            String newDocumentName = documentName.getText().trim();
            if (newDocumentName.matches("[\\w\\d]+")) {
                WelcomeViewThread thread = new WelcomeViewThread(client, "new "	+ newDocumentName);
                thread.start();
            } else {
                JOptionPane.showMessageDialog(null, "Nazwa dokumentu nie może być pusta i może się składać jedynie z liter i cyfr.", "Niepoprawna nazwa dokumentu!", JOptionPane.ERROR_MESSAGE);
            }
        }
        else if (e.getSource() == openDocumentButton){
            client.sendMessageToServer("look");
        }
    }

    /**
     * Zwraca klienta.
     *
     * @return klient
     */

    public Client getClient() {
        return client;
    }
}
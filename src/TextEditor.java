import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class TextEditor extends JFrame implements ActionListener {
    // tutaj wpisujemy tekst
    JTextArea text;

    // ramka
    JFrame frame;

    public TextEditor() {
        frame = new JFrame("Dokument tekstowy");


        text = new JTextArea();

        //menubar
        JMenuBar menubar = new JMenuBar();

        //menu glowne i jego zawartość wraz z action listenerami
        JMenu menu = new JMenu("Plik");
        JMenuItem item1 = new JMenuItem("Nowy");
        JMenuItem item2 = new JMenuItem("Otwórz");
        JMenuItem item3 = new JMenuItem("Zapisz");
        JMenuItem item4 = new JMenuItem("Drukuj");

        item1.addActionListener(this);
        item2.addActionListener(this);
        item3.addActionListener(this);
        item4.addActionListener(this);

        menu.add(item1);
        menu.add(item2);
        menu.add(item3);
        menu.add(item4);

        //pod-menu i jego zawartosc z action listenerami
        JMenu menu2 = new JMenu("Edytuj");
        JMenuItem item5 = new JMenuItem("Wytnij");
        JMenuItem item6 = new JMenuItem("Kopiuj");
        JMenuItem item7 = new JMenuItem("Wklej");

        item5.addActionListener(this);
        item6.addActionListener(this);
        item7.addActionListener(this);

        menu2.add(item5);
        menu2.add(item6);
        menu2.add(item7);

        //chat z listenerem
        JMenuItem chatButton = new JMenuItem("Chat");
        chatButton.addActionListener(this);

        //wyjscie z programu
        JMenuItem exit_item = new JMenuItem("Zamknij");
        exit_item.addActionListener(this);

        //dodanie wszystkiego do MenuBar
        menubar.add(menu);
        menubar.add(menu2);
        menubar.add(chatButton);
        menubar.add(exit_item);

        frame.setJMenuBar(menubar);
        frame.add(text);
        frame.setSize(1000, 600);
        frame.show();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();

        if (s.equals("Nowy")) {
            text.setText("");
        }

        else if (s.equals("Otwórz")) {
            //utworzenie obiektu JFileChooser
            JFileChooser file_chooser = new JFileChooser("File: ");

            int r = file_chooser.showOpenDialog(null);

            if (r == JFileChooser.APPROVE_OPTION) {
                File file = new File(file_chooser.getSelectedFile().getAbsolutePath());

                try {
                    String s1 = "", file_text = "";

                    //czytanie z pliku
                    FileReader fr = new FileReader(file);

                    BufferedReader br = new BufferedReader(fr);

                    file_text = br.readLine();

                    //czytanie calego pliku
                    while ((s1 = br.readLine()) != null)
                        file_text += "\n" + s1;

                    text.setText(file_text);
                }
                catch (Exception event) {
                    JOptionPane.showMessageDialog(frame, event.getMessage());
                }

            }
        }

        else if (s.equals("Zapisz")) {
            //utworzenie obiektu JFileChooser
            JFileChooser file_chooser = new JFileChooser("File: ");

            int r = file_chooser.showOpenDialog(null);

            if (r == JFileChooser.APPROVE_OPTION) {
                File file = new File(file_chooser.getSelectedFile().getAbsolutePath());

                try {
                    //utworzenie nowego FileWritera
                    FileWriter fw = new FileWriter(file, false);

                    BufferedWriter bw = new BufferedWriter(fw);

                    bw.write(text.getText());

                    bw.flush();
                    bw.close();
                }
                catch (Exception event) {
                    JOptionPane.showMessageDialog(frame, event.getMessage());
                }
            }
        }

        else if (s.equals("Drukuj")) {
            try {
                text.print();
            }
            catch (Exception event) {
                JOptionPane.showMessageDialog(frame, event.getMessage());
            }
        }

        else if (s.equals("Wytnij")) {
            text.cut();
        }

        else if (s.equals("Kopiuj")) {
            text.copy();
        }

        else if (s.equals("Wklej")) {
            text.paste();
        }

        else if (s.equals("Chat")) {
            Chat chat = new Chat();
            chat.LoginWindow();
        }

        else if (s.equals("Zamknij")) {
            frame.dispose();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextEditor editor = new TextEditor();
    }
}

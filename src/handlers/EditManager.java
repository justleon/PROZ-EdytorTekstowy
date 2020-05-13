package handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import debug.Debug;

/**
 * Klasa zarządzająca kolejką zmian w dokumencie na serwerze.
 */

public class EditManager {
    private final Map<String, List<Edit>> editLog;
    private static final boolean DEBUG = Debug.DEBUG;

    /**
     * Tworzenie nowego EditManager dla serwera z nową mapą documentNames do listy Edits.
     * @param documentName
     */

    public EditManager(){
        editLog = Collections.synchronizedMap(new HashMap<String, List<Edit>>());
    }

    /**
     * Tworzenie nowego logu dla dokumentu.
     * Wywołanie podczas tworzenia dokumentu.
     * @param documentName nazwa dokumentu
     */

    public synchronized void createNewlog(String documentName){
        editLog.put(documentName, new ArrayList<Edit>());
    }

    /**
     * Dodaje zmianę do listy dla dokumentu.
     * Nazwa dokumentu pobierana ze zmiany.
     * @param edit zmiana
     */

    public synchronized void logEdit(Edit edit){
        String documentName = edit.getDocumentName();
        editLog.get(documentName).add(edit);
        if (DEBUG){System.out.println(edit.toString());}
    }

    /**
     * Przechodzi przez versionEditLog i próbuje dodać zmianę w poprawnej kolejności.
     * Wywołanie, gdy klient uaktualnia wersję dokumentu.
     * Przechodzi przez całą historię zmian, których wersja jest równa lub większa od danej i odnajduje poprawną kolejność.
     *
     * @param documentName nazwa dokumentu
     * @param version wersja, na której dokonana jest zmiana
     * @param offset pozycja startowa
     * @return poprawny offset
     */

    public synchronized String manageEdit(String documentName, int version, int offset) {
        List<Edit> list = editLog.get(documentName);
        int updatedOffset = offset;
        for (Edit edit : list) {
            if (edit.getVersion() >= version) {
                updatedOffset = manageOffset(updatedOffset, edit.getOffset(), edit.getLength());
                version = edit.getVersion();
            }
        }
        if (DEBUG){System.out.println("Nowy offset: "+offset);}
        if (DEBUG){System.out.println("Nowa wersja: "+version);}
        String result = documentName+" "+(version+1)+" "+offset;
        return result;
    }


    /**
     * Przyjmuje bieżący offset i porównuje go z otherOffset - offset ostatnio wykonanej zmiany oraz jej długość i poprawia offset, jeśli to konieczne.
     *
     * @param currentOffset   bieżąca pozycja offsetu
     * @param otherOffset     pozycja po zmianie
     * @param length          długość ostatnio wykonanej zmiany
     * @return poprawny offset
     */

    private int manageOffset(int currentOffset, int otherOffset, int length) {
        if (currentOffset < otherOffset) {
            return currentOffset;
        } else if (currentOffset < otherOffset + length && currentOffset >= otherOffset) {
            return otherOffset;
        } else {
            return currentOffset + length;
        }
    }
}
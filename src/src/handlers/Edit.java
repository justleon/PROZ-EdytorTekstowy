package handlers;

/**
 * Klasa reprezentująca zmianę w dokumencie.
 * Przechowuje:
 * typ zmiany,
 * treść, jeśli typ to Insert,
 * długość zmiany,
 * offset,
 * wersję dokumentu, na której wykonana była zmiana.
 */

public class Edit {

    // typy zmian
    public static enum Type {INSERT, REMOVE}

    private final String documentName;
    private final Type type;
    private final String text;
    private final int length;
    private final int offset;
    private final int version;

    // typ i treść różne od null

    /**
     * Tworzenie nowej zmiany.
     *
     * @param documentName
     * @param editType
     * @param text
     * @param version
     * @param offset
     * @param length
     */

    public Edit(String documentName, Type editType, String text, int version, int offset, int length) {
        this.documentName = documentName;
        this.type = editType;
        this.text = text;
        this.offset = offset;
        this.length = length;
        this.version = version;
        checkRep();
    }

    /**
     * Sprawdzanie niezmienności reprezentacji.
     */

    private void checkRep() {
        assert documentName != null;
        assert type != null;
    }

    /**
     * Zwracanie typu zmiany.
     * @return typ zmiany
     */

    public Type getType() {
        return type;
    }

    /**
     * Zwracanie treści zmiany.
     * @return treść zmiany
     */

    public String getText() {
        return text;
    }

    /**
     * Zwracanie offsetu.
     * @return offset zmiany
     */

    public int getOffset() {
        return offset;
    }

    /**
     * Zwracanie długości zmiany.
     * @return długość zmiany
     */

    public int getLength() {
        return length;
    }

    /**
     * Zwracanie wersji zmiany
     * @return wersja zmiany
     */
    public int getVersion() {
        return version;
    }

    /**
     * Zwracanie nazwy dokumentu.
     * @return nazwa dokumentu
     */

    public String getDocumentName() {
        return documentName;
    }

    /**
     * Tworzenie reprezentacji zmiany w postaci Stringu.
     * @return reprezentacja zmiany
     */

    public String toString() {
        return "Edit: " + documentName + " type: " + type + " v: " + version + " offset: " + offset + " length: " + length + " text: " + text;
    }
}
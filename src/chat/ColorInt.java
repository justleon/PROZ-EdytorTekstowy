package chat;

/**
 * Klasa odpowiadająca za kolory użytkowników.
 */

class ColorInt {
    public static String[] mColors = {
            "#3079ab", // granatowy
            "#e15258", // czerwony
            "#f9845b", // pomarańczowy
            "#7d669e", // fioletowy
            "#53bbb4", // niebieski
            "#51b46d", // zielony
            "#e0ab18", // musztardowy
            "#f092b0", // różowy
            "#e8d174", // żółty
            "#e39e54", // pomarańczowy
            "#d64d4d", // czerwony
            "#4d7358", // zielony
    };

    /**
     * Nadaje kolor użytkownikowi.
     *
     * @param i id użytkowika
     * @return kolor
     */
    public static String getColor(int i) {
        return mColors[i % mColors.length];
    }
}

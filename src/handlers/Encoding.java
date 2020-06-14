package handlers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Klasa zawierająca metody do kodowania i odkodowywania wiadomości.
 */

public class Encoding {

    /**
     * Koduje treść, korzystając z URLEncoder zgodnie z systemem kodowania UTF-8
     *
     * @param text treść do zakodowania
     * @return zakodowana treść
     */

    public static String encode(String text) {
        String result = "";
        if (text == null) {
            return result;
        }
        try {
            result = URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Dekoduje treść, korzystając z URLEncoder zgodnie z systemem kodowania UTF-8
     *
     * @param text treść do odkodowania
     */

    public static String decode(String text) {
        String result = "";
        if (text == null) {
            return result;
        }
        try {
            result = URLDecoder.decode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
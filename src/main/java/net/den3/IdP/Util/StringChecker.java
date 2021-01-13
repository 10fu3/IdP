package net.den3.IdP.Util;

import java.net.MalformedURLException;
import java.net.URL;

public class StringChecker {
    public static boolean isMailAddress(String mail){
        String pattern = "^[a-zA-Z0-9!#$%&'_`/=~*+\\-?^{|}]+(\\.[a-zA-Z0-9!#$%&'_`/=~*+\\-?^{|}]+)*+(.*)@[a-zA-Z0-9][a-zA-Z0-9\\-]*(\\.[a-zA-Z0-9\\-]+)+$";
        return mail.matches(pattern);
    }

    public static boolean isURL(String url){
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static boolean containsNotAllowCharacter(String words){
        return (words.contains("{") || words.contains("}") || words.contains(",") || words.contains("\""));
    }

//    public static boolean hasNull(Object... param){
//        for (Object o : param) {
//            if (o == null) {
//                return true;
//            }
//        }
//        return false;
//    }
}

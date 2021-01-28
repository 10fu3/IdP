package net.den3.IdP.Util;

import java.util.UUID;

public class LongUUID {
    public static String generate(){
        StringBuilder sb = new StringBuilder(UUID.randomUUID().toString());
        for (int i = 0; i < 3; i++) {
            sb.append("-");
            sb.append(UUID.randomUUID().toString());
        }
        return sb.toString();
    }
}

package net.den3.IdP.Util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Optional;

public class ByteArrayUtil {
    public static Optional<byte[]> convertInputStream(InputStream is){

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[65535];

        try{
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
        }catch (Exception e){
            return Optional.empty();
        }

        return Optional.of(buffer.toByteArray());
    }

    public static byte[] encode(byte[] src, String key) {
        byte[] byteKeyArray = new byte[0];
        byte[] byteEncArray = new byte[src.length];

        // キーの文字列を変換する文字列をカバーするまで繰り返す
        while(byteKeyArray.length < src.length) {
            byteKeyArray = (new String(byteKeyArray) + key).getBytes();
        }

        // 変換
        for (int i = 0; i < src.length; i++) {
            byteEncArray[i] = (byte)(src[i]^byteKeyArray[i]);
        }

        return byteEncArray;
    }

    private static byte[] decode(byte[] src, String key) {
        return encode(src, key);
    }
}

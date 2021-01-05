package net.den3.IdP.Security;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Optional;

/**
 * 何かしらの暗号化をするクラス
 * https://www.casleyconsulting.co.jp/blog/engineer/153/
 * Java SE 8限定 安全なパスワードを生成する方法
 * @author casleyconsulting.inc
 */
public class HashGenerator {
    /** パスワードを安全にするためのアルゴリズム */
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    /** ストレッチング回数 */
    private static final int ITERATION_COUNT = 10000;
    /** 生成される鍵の長さ */
    private static final int KEY_LENGTH = 256;

    /**
     *　平文のパスワードとソルトから安全なパスワードを生成
     *
     * @param password 平文のパスワード
     * @param salt ソルト
     * @return 安全なパスワード
     */
    public static String getSafetyPassword(String password, String salt) {

        char[] passCharAry = password.toCharArray();
        byte[] hashedSalt = getHashedSalt(salt);

        PBEKeySpec keySpec = new PBEKeySpec(passCharAry, hashedSalt, ITERATION_COUNT, KEY_LENGTH);

        SecretKeyFactory skf;
        try {
            skf = SecretKeyFactory.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        SecretKey secretKey;
        try {
            secretKey = skf.generateSecret(keySpec);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
        byte[] passByteAry = secretKey.getEncoded();

        // 生成されたバイト配列を16進数の文字列に変換
        StringBuilder sb = new StringBuilder(64);
        for (byte b : passByteAry) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    /**
     * ソルトをハッシュ化して返却
     * ※ハッシュアルゴリズムはSHA-256を使用
     *
     * @param salt ソルト
     * @return ハッシュ化されたバイト配列のソルト
     */
    private static byte[] getHashedSalt(String salt) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        messageDigest.update(salt.getBytes());
        return messageDigest.digest();
    }

    public static Optional<String> generateBase64SHA_256(String text){
        try{
            return Optional.of(new String(Base64.getEncoder().encode(MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8)))));
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
            return Optional.empty();
        }
    }
}

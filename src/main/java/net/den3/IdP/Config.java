package net.den3.IdP;

import net.den3.IdP.Util.Errorable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Config {
    private final static Config config = new Config();

    private final Map<String,String> store = new HashMap<>();

    /**
     * 起動するたびに異なるサーバーに振られるID
     */
    private String ServerID = "";


    public static Config get(){
        return config;
    }

    public Config(){
        store.put("Port",Optional.ofNullable(System.getenv("PORT")).orElse("80"));
        store.put("DatabaseURL", Optional.ofNullable(System.getenv("DB_URL")).orElse("jdbc:mariadb://localhost:3306/den3_account"));
        store.put("RedisURL",Optional.ofNullable(System.getenv("REDIS_URL")).orElse("localhost"));
        store.put("DBAccountName" , System.getenv("DBACCOUNT") != null ? System.getenv("DBACCOUNT") : "user");
        store.put("DBAccountPassword" , System.getenv("DBPASSWORD") != null ? System.getenv("DBPASSWORD") : "password");
        store.put("MailAddress", System.getenv("MAIL_ADDRESS") != null ? System.getenv("MAIL_ADDRESS") : "");
        store.put("MailPassword" , System.getenv("MAIL_PASS") != null ? System.getenv("MAIL_PASS") : "");
        store.put("EntryMailTitle",System.getenv("MAIL_TITLE") != null ? System.getenv("MAIL_TITLE") : "[電子計算機研究会] 仮登録申請の確認メール");
        store.put("EntryMailBody",System.getenv("MAIL_BODY") != null ? System.getenv("MAIL_BODY") : "仮登録ありがとうございます.<br>本登録をするには本メール到着後1日以内に次のURLにアクセスしてください <br>");
        store.put("EntryMailAddressRegex", System.getenv("MAIL_ADDRESS_REGEX") != null ? System.getenv("MAIL_ADDRESS_REGEX") : ".+");
        store.put("URL" , System.getenv("SELF_URL") != null ? System.getenv("SELF_URL") : "");
        store.put("LoginURL" , System.getenv("LOGIN_URL") != null ? System.getenv("LOGIN_URL") : "");
        store.put("UploaderToken",System.getenv("UPLOADER_SECRET") != null ? System.getenv("UPLOADER_SECRET") :"");
        store.put("MinimumPassword", new Errorable<String,String>().of(System.getenv("MINIMUM_PASS"),"8",(v)->String.valueOf(Integer.valueOf(v))));
        store.put("IDTokenValidTime", new Errorable<String,String>().of(System.getenv("IDTOKEN_VALID_TIME_MINUTE"),"5",(v)->String.valueOf(Long.valueOf(v))));
        ServerID = UUID.randomUUID().toString();
    }

    public int getPort(){
        return Integer.parseInt(store.get("Port"));
    }

    public String getDBAccountName() {
        return store.get("DBAccountName");
    }

    public String getDBAccountPassword() {
        return store.get("DBAccountPassword");
    }

    public String getDBURL(){
        return store.get("DatabaseURL");
    }

    public String getRedisURL(){
        return store.get("RedisURL");
    }

    public String getEntryMailAddress() {
        return store.get("MailAddress");
    }

    public String getEntryMailPassword() {
        return store.get("MailPassword");
    }

    public String getEntryMailTitle() {
        return store.get("EntryMailTitle");
    }

    public String getEntryMailBody() {
        return store.get("EntryMailBody");
    }

    public String getEntryMailRegex(){
        return  store.get("EntryMailAddressRegex");
    }

    public String getSelfURL() {
        return store.get("URL");
    }

    public String getLoginURL() {
        return store.get("LoginURL");
    }

    public String getUploaderToken(){
        return store.get("UploaderToken");
    }

    public Integer getMinimumPassword(){
        return Integer.valueOf(store.get("MinimumPassword"));
    }

    public Long getIDTokenValidMinutes(){
        return Long.valueOf(store.get("IDTokenValidTime"));
    }
}

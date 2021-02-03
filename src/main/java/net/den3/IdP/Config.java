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
        store.put("DatabaseURL", Optional.ofNullable(System.getenv("D3A_DB_URL")).orElse("jdbc:mariadb://localhost:3306/den3_account"));
        store.put("RedisURL",Optional.ofNullable(System.getenv("D3A_REDIS_URL")).orElse("localhost"));
        store.put("DBAccountName" , System.getenv("D3A_DBACCOUNT") != null ? System.getenv("D3A_DBACCOUNT") : "user");
        store.put("DBAccountPassword" , System.getenv("D3A_DBPASSWORD") != null ? System.getenv("D3A_DBPASSWORD") : "password");
        store.put("MailAddress", System.getenv("D3A_MAIL_ADDRESS") != null ? System.getenv("D3A_MAIL_ADDRESS") : "");
        store.put("MailPassword" , System.getenv("D3A_MAIL_PASS") != null ? System.getenv("D3A_MAIL_PASS") : "");
        store.put("URL" , System.getenv("D3A_SELF_URL") != null ? System.getenv("D3A_SELF_URL") : "");
        store.put("LoginURL" , System.getenv("D3A_LOGIN_URL") != null ? System.getenv("D3A_LOGIN_URL") : "");
        store.put("UploaderToken",System.getenv("D3A_UPLOADER_SECRET") != null ? System.getenv("D3A_UPLOADER_SECRET") :"");
        store.put("MinimumPassword", new Errorable<String,String>().of(System.getenv("D3A_MINIMUM_PASS"),"8",(v)->String.valueOf(Integer.valueOf(v))));
        ServerID = UUID.randomUUID().toString();
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

}

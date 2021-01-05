package net.den3.IdP;

import java.util.HashMap;
import java.util.Map;
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

        store.put("DockerMODE",
                (System.getenv("D3A_ISDOCKER") != null) && System.getenv("D3A_ISDOCKER").equalsIgnoreCase("DOCKER") ? "true" : "false");
        store.put("DBAccountName" , System.getenv("D3A_DBACCOUNT") != null ? System.getenv("D3A_DBACCOUNT") : "user");
        store.put("DBAccountPassword" , System.getenv("D3A_DBPASSWORD") != null ? System.getenv("D3A_DBPASSWORD") : "password");
        store.put("mail_address", System.getenv("D3A_MAIL_ADDRESS") != null ? System.getenv("D3A_MAIL_ADDRESS") : "");
        store.put("mail_pass" , System.getenv("D3A_MAIL_PASS") != null ? System.getenv("D3A_MAIL_PASS") : "");
        store.put("url" , System.getenv("D3A_SELF_URL") != null ? System.getenv("D3A_SELF_URL") : "");
        store.put("loginURL" , System.getenv("D3A_LOGIN_URL") != null ? System.getenv("D3A_LOGIN_URL") : "");
        store.put("jwt_secret" , System.getenv("D3A_JWT_SECRET") != null ? System.getenv("D3A_JWT_SECRET") : UUID.randomUUID().toString());
        ServerID = UUID.randomUUID().toString();
    }

    public String getDBAccountName() {
        return store.get("DBAccountName");
    }

    public String getDBAccountPassword() {
        return store.get("DBAccountPassword");
    }

    public String getDBURL(){

        return "true".equalsIgnoreCase(store.get("DockerMODE")) ? "jdbc:mariadb://db:3306/den3_account" : "jdbc:mariadb://localhost:3306/den3_account";
    }

    public String getRedisURL(){
        return "true".equalsIgnoreCase(store.get("DockerMODE")) ? "redis" : "localhost";
    }

    public String getEntryMailAddress() {
        return store.get("mail_address");
    }

    public Config setEntryMailAddress(String entryMailAddress) {
        store.put("mail_address" , entryMailAddress);
        return this;
    }

    public String getEntryMailPassword() {
        return store.get("mail_pass");
    }

    public Config setEntryMailPassword(String entryMailPassword) {
        store.put("mail_pass",entryMailPassword);
        return this;
    }

    public String getSelfURL() {
        return store.get("url");
    }

    public Config setSelfURL(String selfURL) {
        store.put("url",selfURL);
        return this;
    }

    public String getLoginURL() {
        return store.get("loginURL");
    }

    public Config setLoginURL(String loginURL) {
        store.put("loginURL",loginURL);
        return this;
    }

    public String getJwtSecret(){
        return store.get("jwt_secret");
    }

    public Config setJwtSecret(String pass){
        store.put("jwt_secret", pass);
        return this;
    }

    public String getServerID(){
        return ServerID;
    }
}

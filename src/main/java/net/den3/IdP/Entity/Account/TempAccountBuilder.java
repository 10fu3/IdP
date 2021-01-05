package net.den3.IdP.Entity.Account;

import net.den3.IdP.Security.HashGenerator;

public class TempAccountBuilder {

    private final TemporaryAccountEntity tae = new TemporaryAccountEntity();

    /**
     * 仮アカウントエンティティにメールアドレスとハッシュ化されたパスワードを代入する
     * @param key エントリーキー
     * @return ビルダー
     */
    public TempAccountBuilder setKey(String key) {
        tae.key = key;
        return this;
    }

    /**
     * 仮アカウントエンティティにメールアドレスを割り当てる
     * @param mail メールアドレス
     * @return ビルダー
     */
    public TempAccountBuilder setMail(String mail){
        tae.mail = mail;
        return this;
    }

    /**
     * 内部用 DBに格納されたハッシュ化済みパスワードを直接エンティティに割り当てる
     * @param pass パスワード
     * @return ビルダー
     */
    public TempAccountBuilder setPass(String pass){
        tae.passwordHash = pass;
        return this;
    }

    /**
     * 仮アカウントエンティティにパスワードを代入する
     * @param pass パスワード
     * @param salt パスワード生成用のソルト
     * @return ビルダー
     */
    public TempAccountBuilder setSecurePass(String pass,String salt){
        tae.passwordHash = HashGenerator.getSafetyPassword(pass,salt);
        return this;
    }

    /**
     * 仮アカウントエンティティにメールアドレスとハッシュ化されたパスワードを代入する
     * @param unixTime 仮登録時の時間(UNIX時間)
     * @return ビルダー
     */
    public TempAccountBuilder setRegisteredDate(Long unixTime){
        tae.registeredDate = unixTime;
        return this;
    }

    public TempAccountBuilder setNick(String nick){
        tae.nick = nick;
        return this;
    }

    public static TempAccountBuilder New(){
        return new TempAccountBuilder();
    }

    public ITempAccount build(){
        return tae;
    }

}

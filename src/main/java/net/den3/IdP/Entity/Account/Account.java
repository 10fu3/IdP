package net.den3.IdP.Entity.Account;


/**
 * データベースに存在するアカウントを表すクラス
 * NULLを返すメソッドは存在しない
 */

class Account implements IAccount{

    String uuid = "";
    Long lastLogin = 0L;
    String mail = "";
    String passwordHash = "";
    String iconURL = "https://i.imgur.com/R6tktJ6.jpg";
    String nickName = "";
    AccountAttribute attribute;

    /**
     * アカウントのメールアドレスを返す
     * @return メールアドレス
     */
    @Override
    public String getMail() {
        return this.mail;
    }

    /**
     * アカウントのパスワードハッシュを返す
     * @return ハッシュ化されたパスワード
     */
    @Override
    public String getPasswordHash() {
        return this.passwordHash;
    }

    /**
     * アカウントのニックネームを返す
     * @return ニックネーム
     */
    @Override
    public String getNickName() {
        return this.nickName;
    }

    /**
     * アカウントのアイコンを返す 指定がない場合は初期のアイコンURLがセットされている
     * @return アイコンのURL
     */
    @Override
    public String getIconURL() {
        return this.iconURL;
    }

    /**
     * 最終ログイン時刻を返す (形式: YYYY/MM/DD HH:MM:SS)
     * @return 最終ログイン時刻
     */
    @Override
    public Long getLastLoginTime() {
        return this.lastLogin;
    }

    /**
     * 内部IDを返す このIDで外部サービスは個人を識別する
     * @return UUID
     */
    @Override
    public String getUUID() {
        return this.uuid;
    }

    /**
     * アカウントの属性を取得する
     *
     * @return 属性
     */
    @Override
    public AccountAttribute getAttribute() {
        return this.attribute;
    }
}

package net.den3.IdP.Entity.Account;

public interface IAccount {
    /**
     * 固有のIDを返す
     * @return UUID
     */
    String getUUID();

    /**
     * メールアドレスを返す
     * @return メールアドレス
     */
    String getMail();

    /**
     * ハッシュ化されたパスワードを返す
     * @return パスワード
     */
    String getPasswordHash();

    /**
     * ニックネームを返す
     * @return ニックネーム
     */
    String getNickName();

    /**
     * アイコンのURLを返す
     * @return アイコンのURL
     */
    String getIconURL();

    /**
     * 最後にログインした時刻を返す
     * @return 最終ログイン時刻
     */
    Long getLastLoginTime();

    /**
     * アカウントの属性を取得する
     * @return 属性
     */
    AccountAttribute getAttribute();

}

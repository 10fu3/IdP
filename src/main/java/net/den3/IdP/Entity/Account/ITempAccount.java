package net.den3.IdP.Entity.Account;

public interface ITempAccount{

    /**
     * 仮登録時刻を返す
     * @return 仮登録時の時間(UNIX時間)
     */
    Long getRegisteredDate();

    /**
     * アカウントのメールアドレスを返す
     * @return メールアドレス
     */
    String getMail();

    /**
     * アカウントのパスワードハッシュを返す
     * @return ハッシュ化されたパスワード
     */
    String getPassHash();

    /**
     * 有効化に必要なキーを返す
     * @return 有効化キー
     */
    String getKey();

    /**
     * ニックネームを返す
     * @return ニックネーム
     */
    String getNick();
}

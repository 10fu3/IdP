package net.den3.IdP.Entity.Account;

class TemporaryAccountEntity implements ITempAccount {

    String nick = "";
    String mail = "";
    String passwordHash = "";
    Long registeredDate = 0L;
    String key = "";

    /**
     * 仮登録時刻を返す
     * @return 仮登録時の時間(UNIX時間)
     */
    @Override
    public Long getRegisteredDate() {
        return this.registeredDate;
    }

    /**
     * 有効化に必要なキーを返す
     * @return 有効化キー
     */
    @Override
    public String getKey() {
        return this.key;
    }

    /**
     * アカウントのメールアドレスを返す
     * @return メールアドレス
     */
    @Override
    public String getMail() {
        return mail;
    }

    /**
     * アカウントのパスワードハッシュを返す
     * @return ハッシュ化されたパスワード
     */
    @Override
    public String getPassHash() {
        return passwordHash;
    }

    /**
     * ニックネームを返す
     * @return ニックネーム
     */
    @Override
    public String getNick() {
        return nick;
    }
}

package net.den3.IdP.Entity.Account;


import net.den3.IdP.Security.HashGenerator;

public class AccountBuilder {

    private Account account = new Account();

    public static AccountBuilder Edit(IAccount account){
        AccountBuilder builder = AccountBuilder.New();
        builder.account = (Account)account;
        return builder;
    }

    public static AccountBuilder New(){
        return new AccountBuilder();
    }

    public IAccount build(){
        return account;
    }

    /**
     * アカウントエンティティにUUIDを設定する
     * @param UUID UUID
     * @return アカウントエンティティ
     */
    public AccountBuilder setUUID(String UUID) {
        account.uuid = UUID;
        return this;
    }

    /**
     * アカウントエンティティに最終ログイン時刻を設定する
     * @param lastLogin 最終ログイン時刻
     * @return アカウントエンティティ
     */
    public AccountBuilder setLastLogin(String lastLogin) {
        account.lastLogin = Long.decode(lastLogin);
        return this;
    }

    /**
     * アカウントエンティティに最終ログイン時刻を設定する
     * @param lastLogin 最終ログイン時刻
     * @return アカウントエンティティ
     */
    public AccountBuilder setLastLogin(Long lastLogin) {
        account.lastLogin = lastLogin;
        return this;
    }

    /**
     * アカウントエンティティにメールアドレスを設定する
     * @param mail メールアドレス
     * @return アカウントエンティティ
     */
    public AccountBuilder setMail(String mail) {
        account.mail = mail;
        return this;
    }

    /**
     * アカウントエンティティにパスワードハッシュ(文字列)を設定する
     * @param pass パスワードハッシュ(文字列)
     * @return アカウントエンティティ
     */
    public AccountBuilder setPasswordHash(String pass) {
        account.passwordHash = pass;
        return this;
    }

    /**
     * アカウントエンティティにパスワードを割り当てる 実際にはパスワードハッシュを生成して割り当てる
     * @param pass パスワード(生)
     * @return アカウントエンティティ
     */
    public AccountBuilder setSecurePass(String pass, String salt) {
        account.passwordHash = HashGenerator.getSafetyPassword(pass,salt);
        return this;
    }

    /**
     * アカウントエンティティにアイコンの保存先URLを設定する
     * @param iconURL アイコンの保存先URL
     * @return アカウントエンティティ
     */
    public AccountBuilder setIconURL(String iconURL) {
        account.iconURL = iconURL;
        return this;
    }

    /**
     * アカウントエンティティにニックネームを設定する
     * @param nickName ニックネーム
     * @return アカウントエンティティ
     */
    public AccountBuilder setNickName(String nickName) {
        account.nickName = nickName;
        return this;
    }

    /**
     * アカウントエンティティに属性を設定する
     * @param attribute 属性エンティティ
     * @return ビルダー
     */
    public AccountBuilder setAttribute(AccountAttribute attribute){
        account.attribute = attribute;
        return this;
    }

    /**
     * アカウントエンティティに空の属性を設定する
     * @return ビルダー
     */
    public AccountBuilder setAttribute(String uuid){
        account.attribute = new AccountAttribute(uuid,false,false);
        return this;
    }
}

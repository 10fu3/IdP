package net.den3.IdP.Entity.Account;

public interface IPPID {
    /**
     * PPIDを取得する
     * @return PPID
     */
    String getID();

    /**
     * アカウントUUIDを取得する
     * @return アカウントUUID
     */
    String getAccountID();

    /**
     * サービスIDを取得する
     * @return サービスID
     */
    String getServiceID();
}

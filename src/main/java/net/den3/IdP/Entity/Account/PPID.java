package net.den3.IdP.Entity.Account;

/**
 * サービスごとにことなるアカウントUUIDを発行するためのエンティティ
 */
class PPID implements IPPID{
    String ID = "";
    String accountID = "";
    String serviceID = "";

    /**
     * PPIDを取得する
     *
     * @return PPID
     */
    @Override
    public String getID() {
        return this.ID;
    }

    /**
     * アカウントUUIDを取得する
     *
     * @return アカウントUUID
     */
    @Override
    public String getAccountID() {
        return this.accountID;
    }

    /**
     * サービスIDを取得する
     *
     * @return サービスID
     */
    @Override
    public String getServiceID() {
        return this.serviceID;
    }
}

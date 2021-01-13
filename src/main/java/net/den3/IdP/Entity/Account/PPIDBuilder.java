package net.den3.IdP.Entity.Account;

/**
 * 仮名(PPID)IDエンティティを組み立てる
 */
public class PPIDBuilder {
    private PPID ppid = new PPID();

    /**
     * 仮名IDをセットする
     * @param ID 仮名ID
     * @return
     */
    public PPIDBuilder setID(String ID) {
        this.ppid.ID = ID;
        return this;
    }

    /**
     * アカウントIDをセットする
     * @param accountID アカウントID
     * @return ビルダー
     */
    public PPIDBuilder setAccountID(String accountID) {
        this.ppid.accountID = accountID;
        return this;
    }

    /**
     * サービスIDをセットする
     * @param serviceID サービスID
     * @return ビルダー
     */
    public PPIDBuilder setServiceID(String serviceID) {
        this.ppid.serviceID = serviceID;
        return this;
    }

    /**
     * PPIDエンティティを組み立てる
     * @return PPIDエンティティ
     */
    public IPPID build(){
        return this.ppid;
    }
}

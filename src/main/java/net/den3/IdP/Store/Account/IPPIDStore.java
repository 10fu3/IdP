package net.den3.IdP.Store.Account;

import net.den3.IdP.Entity.Account.IPPID;
import net.den3.IdP.Store.InjectionStore;

import java.util.List;
import java.util.Optional;

/**
 * サービスごとにことなるアカウントUUIDを発行するためのストア
 */
public interface IPPIDStore {

    static IPPIDStore getInstance(){
        return (IPPIDStore) InjectionStore.get().get("ppid").orElseThrow(NullPointerException::new);
    }

    /**
     * PPIDエンティティを返す
     * @param ppid 仮名ID
     * @return PPIDエンティティ
     */
    Optional<IPPID> getPPID(String ppid);

    /**
     * アカウントが持つ仮名ID(PPID)を返す
     * @param accountUUID アカウントUUID
     * @return [仮名ID]
     */
    List<IPPID> getPPIDs(String accountUUID);

    /**
     * 仮名IDとアカウントUUIDを紐付けて保存する
     * @param ppid 仮名ID
     */
    void addPPID(IPPID ppid);

    /**
     * 仮名IDがストアに保存されているかどうか
     * @param ppid 仮名ID
     * @return true -> 含まれている false->含まれていない
     */
    boolean containsPPID(String ppid);

    /**
     * 仮名IDをサービスと内部IDで完全一致検索
     *
     * @param uuid 内部ID
     * @param client_ID サービスID
     * @return ppid
     */
    Optional<IPPID> getPPID(String uuid,String client_ID);

    /**
     * 仮名IDをストアから削除する
     * @param ppid 仮名ID
     */
    void removePPID(String ppid);

    /**
     * 発行サービスIDと合致するPPIDをすべて削除する
     * @param serviceID サービスID
     */
    void removePPIDbyServiceID(String serviceID);

    /**
     * アカウントUUIDと合致するPPIDをさべて削除する
     * @param accountID
     */
    void removePPIDbyAccountID(String accountID);

}

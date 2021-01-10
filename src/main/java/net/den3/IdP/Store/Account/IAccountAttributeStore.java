package net.den3.IdP.Store.Account;

import net.den3.IdP.Entity.Account.AccountAttribute;
import net.den3.IdP.Entity.Account.IAccount;
import net.den3.IdP.Store.InjectionStore;

import java.util.List;
import java.util.Optional;

public interface IAccountAttributeStore {
    /**
     * シングルトンオブジェクトを取得する
     * @return アカウントストア
     */
    static IAccountAttributeStore getInstance() {
        return (IAccountAttributeStore) InjectionStore.get().get("account_attribute").orElseThrow(NullPointerException::new);
    }

    /**
     * アカウントUUIDに紐付いているアカウント属性を取得する
     * @param uuid アカウントUUID
     * @return 属性
     */
    Optional<AccountAttribute> getAttribute(String uuid);

    /**
     * アカウントUUIDに紐付いているアカウント属性を削除
     * @param uuid アカウントUUID
     */
    void deleteAttribute(String uuid);

    /**
     * アカウントエンティティに紐付いてる属性を更新する
     * @param account アカウントエンティティ
     */
    void updateAttribute(IAccount account);

    /**
     * アカウントのUUIDとアカウントの属性をセットでDBに保存する
     * @param uuid アカウントのUUID
     * @param attribute 保存する属性
     */
    void addAttribute(String uuid,AccountAttribute attribute);

    /**
     * 凍結されたアカウントをすべて取得する
     * @return [凍結されたアカウントのUUID]
     */
    List<String> getFrozenAccount();

    /**
     * 管理者権限を持つアカウントをすべて取得する
     * @return [管理者権限を持つアカウントのUUID]
     */
    List<String> getAdminAccount();
}

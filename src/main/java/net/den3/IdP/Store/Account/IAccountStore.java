package net.den3.IdP.Store.Account;

import net.den3.IdP.Entity.Account.AccountAttribute;
import net.den3.IdP.Entity.Account.IAccount;
import net.den3.IdP.Entity.Account.ITempAccount;
import net.den3.IdP.Store.InjectionStore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface IAccountStore {
    /**
     * シングルトンオブジェクトを取得する
     * @return アカウントストア
     */
    static IAccountStore getInstance() {
        return (IAccountStore) InjectionStore.get().get("account").orElseThrow(NullPointerException::new);
    }

    /**
     * 指定されたUUIDを持つアカウントがアカウントストアに登録されているかどうか
     * @param uuid 調べる対象のUUID
     * @return true->存在する false->存在しない
     */
    boolean containsAccountInSQLByUUID(String uuid);

    /**
     * 指定されたメールアドレスを持つアカウントがアカウントストアに登録されているかどうか
     * @param mail 調べる対象のメールアドレス
     * @return true->存在する false->存在しない
     */
    boolean containsAccountInSQLByMail(String mail);

    /**
     * アカウントの情報を更新する
     *
     * @param account 更新するエンティティ
     * @return true → 成功 false → 失敗
     */
    boolean updateAccountInSQL(IAccount account);

    /**
     * アカウントをDBに登録する
     *
     * @param account アカウントエンティティ
     * @return true → 成功 false → 失敗
     */
    boolean addAccountInSQL(IAccount account);

    /**
     * アカウントをDBに登録する
     *
     * @param tempAccount 仮アカウントエンティティ
     * @return true → 成功 false → 失敗
     */
    boolean addAccountInSQL(ITempAccount tempAccount, ITempAccountStore tempAccountStore);
    /**
     * アカウントをDBから削除する
     *
     * @param deleteAccount 削除対象のアカウントエンティティのUUID
     * @return true → 削除成功 false → 失敗
     */
    boolean deleteAccountInSQL(String deleteAccount);
    /**
     * データベースに登録されたアカウントをすべて取得する
     * @return アカウントエンティティのリスト
     */
    Optional<List<IAccount>> getAccountsAll();
    /**
     * データベースの中にあるアカウントをメールアドレスで検索して取得する
     * @param mail メールアドレス
     * @return アカウントエンティティ
     */
    Optional<IAccount> getAccountByMail(String mail);
    /**
     * データベースの中にあるアカウントをuuidで検索して取得する
     * @param id アカウント固有のUUID
     * @return アカウントエンティティ
     */
    Optional<IAccount> getAccountByUUID(String id);
    /**
     * 発行したSQLに合致するアカウントを取得する
     * @param query Connectionを引数に持ち戻り値がPreparedStatement>のラムダ式/クロージャ
     * @return SQLの条件に合致したアカウントのリスト
     */
    Optional<List<IAccount>> getAccountBySQL(Function<Connection, Optional<PreparedStatement>> query);

    /**
     * 凍結されたアカウントをすべて取得する
     * @return [凍結されたアカウント]
     */
    List<IAccount> getFrozenAccount();

    /**
     * 管理者権限を持つアカウントをすべて取得する
     * @return [管理者権限を持つアカウント]
     */
    List<IAccount> getAdminAccount();
}

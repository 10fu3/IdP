package net.den3.IdP.Store.Auth;

import net.den3.IdP.Store.InjectionStore;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ILoginTokenStore {

    static ILoginTokenStore getInstance(){
        return (ILoginTokenStore) InjectionStore.get().get("login_token").orElseThrow(NullPointerException::new);
    }

    /**
     * 登録されたトークンから紐づけられたアカウントUUIDを取得する
     * @param token トークン
     * @return Optional[紐づけられたアカウントUUID]
     */
    Optional<String> getAccountUUID(String token);

    /**
     * トークンの存在を確認する
     * @param token トークン
     * @return true->存在する false->存在しない
     */
    boolean containsToken(String token);

    /**
     * トークンを登録する
     * @param token トークン
     * @param uuid アカウントに紐付けされたUUID
     */
    void putToken(String token, String uuid);

    /**
     * トークンを削除する
     * @param uuid アカウントに紐付けされたUUID
     * @return true->成功 false->失敗
     */
    boolean deleteTokenByAccount(String uuid);

    /**
     * トークンを削除する
     * @param token ログイントークン
     * @return true->成功 false->失敗
     */
    boolean deleteToken(String token);
}

package net.den3.IdP.Store.Auth;

import net.den3.IdP.Entity.Auth.IAccessToken;
import net.den3.IdP.Store.InjectionStore;

import java.util.Optional;

public interface IAccessTokenStore {

    static IAccessTokenStore getInstance() {
        return (IAccessTokenStore) InjectionStore.get().get("access_token").orElseThrow(NullPointerException::new);
    }

    /**
     * アクセストークンエンティティをUUIDから取得する
     * @param id 内部ID
     * @return アクセストークンエンティティ
     */
    Optional<IAccessToken> getTokenByID(String id);

    /**
     * アクセストークンエンティティをリフレッシュトークンから取得する
     * @param refreshToken リフレッシュトークン
     * @return アクセストークンエンティティ
     */
    Optional<IAccessToken> getTokenByRefresh(String refreshToken);

    /**
     * アクセストークンエンティティをトークンから取得する
     * @param token トークン
     * @return アクセストークンエンティティ
     */
    Optional<IAccessToken> getTokenByToken(String token);

    /**
     * 特定の内部IDを持つアクセストークンエンティティをIdPから削除する
     * @param id 内部ID
     */
    void deleteTokenByID(String id);

    /**
     * 保存されているアクセストークンを更新する
     * @param token 更新後のアクセストークンエンティティ
     */
    void updateToken(IAccessToken token);

    /**
     * アクセストークンを保存する
     * @param token アクセストークンエンティティ
     * @return 成功->true 失敗->false
     */
    boolean addToken(IAccessToken token);
}

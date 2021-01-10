package net.den3.IdP.Store.Auth;


import net.den3.IdP.Entity.Auth.IAuthFlow;
import net.den3.IdP.Store.InjectionStore;

import java.util.Optional;

public interface IAuthFlowStore {

    static IAuthFlowStore getInstance() {
        return (IAuthFlowStore)InjectionStore.get().get("auth_flow").orElseThrow(NullPointerException::new);
    }

    /**
     * 認可フロー固有のIDから認可フローエンティティを取得する
     * @param id 認可フロー固有のID
     * @return 認可フローエンティティ
     */
    Optional<IAuthFlow> getAuthFlowByID(String id);

    /**
     * アクセストークンから認可フローエンティティを取得する
     * @param token アクセストークン
     * @return 認可フローエンティティ
     */
    Optional<IAuthFlow> getAuthFlowByAccessToken(String token);

    /**
     * リフレッシュトークンから認可フローエンティティを取得する
     * @param token リフレッシュトークン
     * @return 認可フローエンティティ
     */
    Optional<IAuthFlow> getAuthFlowByRefreshToken(String token);

    /**
     * 認可コードから認可フローエンティティを取得する
     * @param token 認可コード
     * @return 認可フローエンティティ
     */
    Optional<IAuthFlow> getAuthFlowByAuthorizationCode(String token);

    /**
     * DBを更新する
     * @param flow 認可フローエンティティ
     */
    void updateAuthFlow(IAuthFlow flow);

    /**
     * DBから削除する
     * @param id 認可フローエンティティ固有ID
     */
    void deleteAuthFlow(String id);

    /**
     * DBから削除する
     * @param id アカウントUUID
     */
    void deleteAuthFlowByAccountUUID(String id);

    /**
     * DBに追加する
     * @param flow 認可フローエンティティ
     */
    void addAuthFlow(IAuthFlow flow);

}

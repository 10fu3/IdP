package net.den3.IdP.Entity.Auth;

import java.util.Optional;

public interface IAccessToken {

    Long _30DAY = 60L*60L*24L*30L;
    Long _90DAY = 60L*60L*24L*90L;

    /**
     * 内部IDを取得する
     * @return 内部ID
     */
    String getUUID();

    /**
     * 認可したアカウントを取得する
     * @return 認可したアカウント
     */
    String getAccountID();

    /**
     * 認可されたサービスのクライアントIDを取得する
     * @return クライアントID
     */
    String getClientID();

    /**
     * アクセストークンを取得する
     * @return リフレッシュトークン
     */
    String getAccessToken();

    /**
     * リフレッシュトークンを取得する
     * @return リフレッシュトークン
     */
    String getRefreshToken();

    /**
     * スコープ(権限)を取得する
     * @return スコープ
     */
    String getScope();

    /**
     * nonce値を取得する
     * @return nonce
     */
    Optional<String> getNonce();


    /**
     * アクセストークンを更新する
     * @return 更新後のアクセストークン
     */
    String updateAccessToken();

    /**
     * リフレッシュトークンを更新する
     * @return 更新後のリフレッシュトークン
     */
    String updateRefreshToken();

    /**
     * アクセストークンの生存時間を取得する
     * @return アクセストークンの生存時間
     */
    Long getLifeTimeAccessToken();

    /**
     * リフレッシュトークンの生存時間を取得する
     * @return リフレッシュトークンの生存時間
     */
    Long getLifeTimeRefreshToken();

}

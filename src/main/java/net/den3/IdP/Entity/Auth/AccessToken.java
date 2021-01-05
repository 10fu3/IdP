package net.den3.IdP.Entity.Auth;

import java.util.Optional;
import java.util.UUID;

class AccessToken implements IAccessToken{
    String uuid = UUID.randomUUID().toString();
    String clientID = "";
    String accountID = "";
    String scope = "";
    Optional<String> nonce = Optional.empty();
    String accessToken = UUID.randomUUID().toString();
    String refreshToken = UUID.randomUUID().toString();
    Long lifeTimeAccessToken = (System.currentTimeMillis()/1000L) + _30DAY;
    Long lifeTimeRefreshToken = (System.currentTimeMillis()/1000L) + _90DAY;

    @Override
    public String getUUID() {
        return this.uuid;
    }

    /**
     * 認可したアカウントを取得する
     *
     * @return 認可したアカウント
     */
    @Override
    public String getAccountID() {
        return this.accountID;
    }

    /**
     * 認可されたサービスのクライアントIDを取得する
     *
     * @return クライアントID
     */
    @Override
    public String getClientID() {
        return this.clientID;
    }

    /**
     * アクセストークンを取得する
     *
     * @return リフレッシュトークン
     */
    @Override
    public String getAccessToken() {
        return this.refreshToken;
    }

    /**
     * リフレッシュトークンを取得する
     *
     * @return リフレッシュトークン
     */
    @Override
    public String getRefreshToken() {
        return this.refreshToken;
    }

    /**
     * スコープ(権限)を取得する
     *
     * @return スコープ
     */
    @Override
    public String getScope() {
        return this.scope;
    }

    /**
     * nonce値を取得する
     *
     * @return nonce
     */
    @Override
    public Optional<String> getNonce() {
        return this.nonce;
    }

    /**
     * アクセストークンを更新する
     *
     * @return 更新後のアクセストークン
     */
    @Override
    public String updateAccessToken() {
        this.accessToken = UUID.randomUUID().toString();
        return this.accessToken;
    }

    /**
     * リフレッシュトークンを更新する
     *
     * @return 更新後のリフレッシュトークン
     */
    @Override
    public String updateRefreshToken() {
        return this.refreshToken;
    }

    /**
     * アクセストークンの生存時間を取得する
     *
     * @return アクセストークンの生存時間
     */
    @Override
    public Long getLifeTimeAccessToken() {
        return this.lifeTimeAccessToken;
    }

    /**
     * リフレッシュトークンの生存時間を取得する
     *
     * @return リフレッシュトークンの生存時間
     */
    @Override
    public Long getLifeTimeRefreshToken() {
        return this.lifeTimeRefreshToken;
    }
}

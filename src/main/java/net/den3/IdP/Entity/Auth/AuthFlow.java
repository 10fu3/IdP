package net.den3.IdP.Entity.Auth;

import java.util.Optional;
import java.util.UUID;

/**
 * 認可フローを実装するクラス
 */
class AuthFlow implements IAuthFlow {

    //認可コードの生存時間 10分
    final static Long lifeTime = 60L*10L;

    Long lifetime = 0L;
    String clientID = "";
    String accountID = "";
    String authorizationCode = UUID.randomUUID().toString();
    String uuid = UUID.randomUUID().toString();
    String accessToken = "";
    Optional<String> codeChallenge = Optional.empty();
    Optional<CodeChallengeMethod> codeChallengeMethod = Optional.empty();

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
     * 認可コードを取得する
     *
     * @return 認可コード
     */
    @Override
    public String getAuthorizationCode() {
        return this.authorizationCode;
    }


    /**
     * フロー固有コード
     *
     * @return フロー固有コード
     */
    @Override
    public String getUUID() {
        return this.uuid;
    }

    /**
     * 認可コードから得られるアクセストークンを取得するためのアクセストークンエンティティの内部ID
     *
     * @return アクセストークンエンティティの内部ID
     */
    @Override
    public String getAccessToken() {
        return this.accessToken;
    }


    /**
     * PKCE用 コードチャレンジの方法を返す s256,plane,other
     *
     * @return s256, plane, other
     */
    @Override
    public Optional<CodeChallengeMethod> getCodeChallengeMethod() {
        return this.codeChallengeMethod;
    }

    /**
     * PKCE用 コードチャレンジ
     *
     * @return コードチャレンジ
     */
    @Override
    public Optional<String> getCodeChallenge() {
        return this.codeChallenge;
    }

    /**
     * 有効な時間
     *
     * @return 有効時間
     */
    @Override
    public Long getLifeTime() {
        return this.lifetime;
    }

    /**
     * UNIX時間での有効期限を設定する
     * @param lifeTime 有効期限
     */
    @Override
    public void setLifeTIme(Long lifeTime) {
        this.lifetime = lifeTime;
    }
}

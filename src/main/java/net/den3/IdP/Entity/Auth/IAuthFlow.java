package net.den3.IdP.Entity.Auth;

import java.util.Optional;

/**
 * 認可フローを定義するクラス
 */
public interface IAuthFlow {

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
     * 認可コードを取得する
     * @return 認可コード
     */
    String getAuthorizationCode();

    /**
     * フロー固有コード
     * @return フロー固有コード
     */
    String getUUID();

    /**
     * 認可コードから得られるアクセストークンを取得するためのアクセストークンエンティティの内部ID
     * @return アクセストークンエンティティの内部ID
     */
    String getAccessTokenUUID();

    /**
     * PKCE用 コードチャレンジの方法を返す s256,plane,other
     * @return s256,plane,other
     */
    Optional<CodeChallengeMethod> getCodeChallengeMethod();

    /**
     * PKCE用 コードチャレンジ
     * @return コードチャレンジ
     */
    Optional<String> getCodeChallenge();

    /**
     * 有効な時間
     * @return 有効時間
     */
    Long getLifeTime();

    /**
     * UNIX時間での有効期限を設定する
     * @param lifetime 有効期限
     */
    void setLifeTIme(Long lifetime);


}

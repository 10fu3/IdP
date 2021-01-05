package net.den3.IdP.Entity.Auth;

import java.util.Optional;

public class AccessTokenBuilder {
    private final AccessToken token = new AccessToken();

    public static AccessTokenBuilder New(){
        return new AccessTokenBuilder();
    }

    public IAccessToken build(){
        return token;
    }

    public AccessTokenBuilder setUUID(String uuid){
        this.token.uuid = uuid;
        return this;
    }

    public AccessTokenBuilder setClientID(String clientID) {
        this.token.clientID = clientID;
        return this;
    }

    public AccessTokenBuilder setAccountID(String accountID) {
        this.token.accountID = accountID;
        return this;
    }

    public AccessTokenBuilder setScope(String scope) {
        this.token.scope = scope;
        return this;
    }

    public AccessTokenBuilder setAccessToken(String accessToken) {
        this.token.accessToken = accessToken;
        return this;
    }

    public AccessTokenBuilder setRefreshToken(String refreshToken) {
        this.token.refreshToken = refreshToken;
        return this;
    }

    public AccessTokenBuilder setLifeTimeAccessToken(Long lifeTimeAccessTokenBuilder) {
        this.token.lifeTimeAccessToken = lifeTimeAccessTokenBuilder;
        return this;
    }

    public AccessTokenBuilder setLifeTimeRefreshToken(Long lifeTimeRefreshToken) {
        this.token.lifeTimeRefreshToken = lifeTimeRefreshToken;
        return this;
    }

    public AccessTokenBuilder setNonce(Optional<String> nonce){
        this.token.nonce = nonce;
        return this;
    }
}

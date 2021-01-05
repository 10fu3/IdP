package net.den3.IdP.Entity.Auth;

import java.util.Optional;

public class AuthFlowBuilder {

    private final AuthFlow af = new AuthFlow();
    
    public static AuthFlowBuilder create(){
        return new AuthFlowBuilder();
    }
    
    public IAuthFlow build(){
        return af;
    }

    public AuthFlowBuilder setClientID(String clientID) {
        if(clientID == null){
            throw new NullPointerException();
        }
        this.af.clientID = clientID;
        return this;
    }

    public AuthFlowBuilder setAccountID(String accountID) {
        if(accountID == null){
            throw new NullPointerException();
        }
        this.af.accountID = accountID;
        return this;
    }

    public AuthFlowBuilder setAuthorizationCode(String authorizationCode) {
        if(authorizationCode == null){
            throw new NullPointerException();
        }
        this.af.authorizationCode = authorizationCode;
        return this;
    }

    public AuthFlowBuilder setUUID(String flowCode) {
        if(flowCode == null){
            throw new NullPointerException();
        }
        this.af.uuid = flowCode;
        return this;
    }

    public AuthFlowBuilder setAccessTokenUUID(String uuid){
        if(uuid == null){
            throw new NullPointerException();
        }
        this.af.accessTokenUUID = uuid;
        return this;
    }

    public AuthFlowBuilder setCodeChallenge(String codeChallenge){
        if(codeChallenge == null){
            throw new NullPointerException();
        }
        this.af.codeChallenge = Optional.of(codeChallenge);
        return this;
    }

    public AuthFlowBuilder setCodeChallengeMethod(CodeChallengeMethod method){
        if(method == null){
            throw new NullPointerException();
        }
        this.af.codeChallengeMethod = Optional.of(method);
        return this;
    }

    public AuthFlowBuilder setLifeTime(Long time){
        this.af.lifetime = time;
        return this;
    }

    public AuthFlowBuilder setLifeTimeNow(){
        this.af.lifetime = (System.currentTimeMillis()/1000L) +AuthFlow.lifeTime;
        return this;
    }

}


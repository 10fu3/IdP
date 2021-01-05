package net.den3.IdP.Entity.Auth;

import java.util.Optional;

public class AuthorizeParam{
    String query = "";
    boolean ValidParam;
    String responseType = "";
    String clientID = "";
    String redirect_uri = "";
    String state = "";
    String scope = "";
    Optional<String> nonce = Optional.empty();
    Optional<String> codeChallengeMethod = Optional.empty();
    Optional<String> codeChallenge = Optional.empty();

    public String getQuery() {
        return query;
    }

    public boolean isValidParam() {
        return ValidParam;
    }

    public String getResponseType() {
        return responseType;
    }

    public String getClientID() {
        return clientID;
    }

    public String getRedirect_uri() {
        return redirect_uri;
    }

    public String getState() {
        return state;
    }

    public String getScope() {
        return scope;
    }

    public Optional<String> getNonce() {
        return nonce;
    }

    public Optional<String> getCodeChallengeMethod() {
        return codeChallengeMethod;
    }

    public Optional<String> getCodeChallenge() {
        return codeChallenge;
    }

    public AuthorizeParam(io.javalin.http.Context ctx){

        if(!(ctx.queryParamMap().containsKey("response_type") &&
                ctx.queryParamMap().containsKey("client_id") &&
                ctx.queryParamMap().containsKey("redirect_uri") &&
                ctx.queryParamMap().containsKey("state") &&
                ctx.queryParamMap().containsKey("scope"))){
            ValidParam = false;
            return;
        }

        ValidParam = true;
        query = ctx.queryString();
        responseType = ctx.queryParam("response_type");
        clientID = ctx.queryParam("client_id");
        redirect_uri = ctx.queryParam("redirect_uri");
        state = ctx.queryParam("state");
        scope = ctx.queryParam("scope");
        if(ctx.queryParamMap().containsKey("nonce")){
            nonce = Optional.ofNullable(ctx.queryParam("nonce"));
        }
        if(ctx.queryParamMap().containsKey("code_challenge_method")&&ctx.queryParamMap().containsKey("code_challenge")){
            String method = ctx.queryParam("code_challenge_method");
            String code = ctx.queryParam("code_challenge");
            if(!("S256".equalsIgnoreCase(method) || "plane".equalsIgnoreCase(method))){
                ValidParam = false;
            }
            codeChallengeMethod = Optional.ofNullable(ctx.queryParam("code_challenge_method"));
            codeChallenge = Optional.ofNullable(ctx.queryParam("code_challenge"));
        }
    }
}
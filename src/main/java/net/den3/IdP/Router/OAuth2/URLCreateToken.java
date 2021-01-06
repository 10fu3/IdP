package net.den3.IdP.Router.OAuth2;

import com.auth0.jwt.JWT;
import net.den3.IdP.Config;
import net.den3.IdP.Entity.Account.IAccount;
import net.den3.IdP.Entity.Auth.CodeChallengeMethod;
import net.den3.IdP.Entity.Auth.IAccessToken;
import net.den3.IdP.Entity.Auth.IAuthFlow;
import net.den3.IdP.Entity.Service.IService;
import net.den3.IdP.Security.HashGenerator;
import net.den3.IdP.Security.JWTTokenCreator;
import net.den3.IdP.Store.Account.IAccountStore;
import net.den3.IdP.Store.Auth.IAccessTokenStore;
import net.den3.IdP.Store.Auth.IAuthFlowStore;
import net.den3.IdP.Store.Service.IServiceStore;
import net.den3.IdP.Util.MapBuilder;
import net.den3.IdP.Util.StatusCode;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class URLCreateToken {
    public static void mainFlow(io.javalin.http.Context ctx){
        Map<String, List<String>> param = ctx.formParamMap();
        if(param.containsKey("code")
                && param.containsKey("redirect_uri")
                && param.containsKey("client_id")
                && param.containsKey("client_secret")){

            Optional<IService> opService = IServiceStore.getInstance().getService(ctx.formParam("client_id"));
            Optional<IAuthFlow> opAuthFlow = IAuthFlowStore.getInstance().getAuthFlowByAuthorizationCode(ctx.formParam("code"));

            if(!(opService.isPresent() && opAuthFlow.isPresent())){
                ctx.status(StatusCode.NotFound.code()).result("params_not_found");
                return;
            }
            Optional<IAccessToken> token = IAccessTokenStore.getInstance().getTokenByToken(opAuthFlow.get().getAccessToken());

            if(!token.isPresent()){
                ctx.status(StatusCode.NotFound.code()).result("access_token");
                return;
            }

            //確実に存在する
            IService service = opService.get();
            IAuthFlow auth = opAuthFlow.get();

            Optional<IAccount> opAccount = IAccountStore.getInstance().getAccountByUUID(auth.getAccountID());

            //そもそもアカウントがないのでアクセストークンと認可コードは削除する
            if(!opAccount.isPresent()){
                ctx.status(StatusCode.NotFound.code());
                //削除
                IAuthFlowStore.getInstance().deleteAuthFlow(auth.getUUID());
                IAccessTokenStore.getInstance().deleteTokenByID(token.get().getUUID());
                return;
            }

            IAccount account = opAccount.get();

            //リダイレクトURLが一致しない
            if(!service.getRedirectURL().equalsIgnoreCase(ctx.formParam("redirect_uri"))){
                ctx.status(StatusCode.BadRequest.code()).result("redirect_uri");
                return;
            }

            //シークレットキーが一致しない
            if(!service.getSecretID().equalsIgnoreCase(ctx.formParam("client_secret"))){
                ctx.status(StatusCode.BadRequest.code()).result("client_secret");
                return;
            }

            //10分超えなので認可コードだけ削除する
            if(auth.getLifeTime() < (System.currentTimeMillis()/1000L)){
                ctx.status(StatusCode.BadRequest.code()).result("timeout");
                //削除
                IAuthFlowStore.getInstance().deleteAuthFlow(auth.getUUID());
            }

            CodeChallengeMethod challengeMethod = auth.getCodeChallengeMethod().orElse(CodeChallengeMethod.NONE);


            //認可リクエスト時にコードチャレンジを設定した場合
            if((challengeMethod == CodeChallengeMethod.S256
                    || challengeMethod == CodeChallengeMethod.PLANE)){
                if(!param.containsKey("code_verifier")){
                    ctx.status(StatusCode.BadRequest.code()).result("code_challenge");
                    return;
                }

                if(challengeMethod == CodeChallengeMethod.S256){
                    String serverCodeVerifier = HashGenerator.generateBase64SHA_256(auth.getCodeChallenge().orElse("")).orElse("");
                    if(!Optional.ofNullable(ctx.formParam("code_verifier")).orElse("").equalsIgnoreCase(serverCodeVerifier)){
                        ctx.status(StatusCode.BadRequest.code()).result("code_challenge");
                        return;
                    }
                }
                if(challengeMethod == CodeChallengeMethod.PLANE){
                    if(!Optional.ofNullable(ctx.formParam("code_verifier")).orElse("").equalsIgnoreCase(auth.getCodeChallenge().orElse(""))){
                        ctx.status(StatusCode.BadRequest.code()).result("code_challenge");
                        return;
                    }
                }
            }

            //認可コードとアクセストークンを引き換えする
            MapBuilder builder = MapBuilder
                    .New()
                    .put("access_token",token.get().getAccessToken())
                    .put("expires_in",token.get().getLifeTimeAccessToken()-(System.currentTimeMillis()/1000L))
                    .put("refresh_token", token.get().getRefreshToken())
                    .put("scope",token.get().getScope())
                    .put("token_type","Bearer");

            //もしopenidだったらid_tokenも返す
            if(token.get().getScope().contains("openid")){
                builder.put("id_token",
                        JWTTokenCreator
                                .signHMAC256(
                                        JWTTokenCreator.addAuthenticateClaims(
                                                JWT.create(),
                                                service,
                                                account,
                                                token.get().getNonce(),
                                                Config.get().getSelfURL()),
                                        service.getSecretID()
                                )
                );
            }
            ctx.json(builder.build());

            //削除
            IAuthFlowStore.getInstance().deleteAuthFlow(auth.getUUID());
        }else{
            ctx.status(StatusCode.BadRequest.code());
        }
    }
}

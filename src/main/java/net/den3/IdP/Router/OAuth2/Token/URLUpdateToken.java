package net.den3.IdP.Router.OAuth2.Token;

import com.auth0.jwt.JWT;
import net.den3.IdP.Config;
import net.den3.IdP.Entity.Account.IAccount;
import net.den3.IdP.Entity.Account.IPPID;
import net.den3.IdP.Entity.Auth.AccessTokenBuilder;
import net.den3.IdP.Entity.Auth.IAccessToken;
import net.den3.IdP.Entity.Service.IService;
import net.den3.IdP.Entity.Service.ServicePermission;
import net.den3.IdP.Security.JWTTokenCreator;
import net.den3.IdP.Store.Account.IAccountStore;
import net.den3.IdP.Store.Account.IPPIDStore;
import net.den3.IdP.Store.Auth.IAccessTokenStore;
import net.den3.IdP.Store.Auth.IAuthFlowStore;
import net.den3.IdP.Store.Service.IServiceStore;
import net.den3.IdP.Util.MapBuilder;
import net.den3.IdP.Util.StatusCode;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * リフレッシュトークンを用いてアクセストークンをアップデートする
 */
public class URLUpdateToken {
    public static void mainFlow(io.javalin.http.Context ctx){

        Map<String, List<String>> param = ctx.formParamMap();

        //必要なパラメーター
        if(param.containsKey("refresh_token") &&
                param.containsKey("client_id") &&
                param.containsKey("client_secret")){

            String refreshToken = Optional.ofNullable(ctx.formParam("refresh_token")).orElse("");
            String clientID = Optional.ofNullable(ctx.formParam("client_id")).orElse("");
            String clientSecret = Optional.ofNullable(ctx.formParam("client_secret")).orElse("");

            Optional<IAccessToken> opAccessToken = IAccessTokenStore.getInstance().getTokenByRefresh(refreshToken);
            Optional<IService> opService = IServiceStore.getInstance().getService(clientID);

            //アクセストークンがない
            if(!opAccessToken.isPresent()){
                ctx.status(StatusCode.NotFound.code());
                return;
            }

            //サービスが存在しない
            if(!opService.isPresent()){
                ctx.status(StatusCode.BadRequest.code());
                return;
            }

            IAccessToken token = opAccessToken.get();
            IService service = opService.get();

            //クライアントIDが認可コード取得時と違う
            if(!token.getClientID().equalsIgnoreCase(clientID)){
                ctx.status(StatusCode.NotFound.code());
                return;
            }

            //クライアントシークレットが一致しない
            if(!service.getSecretID().equalsIgnoreCase(clientSecret)){
                ctx.status(StatusCode.BadRequest.code());
                return;
            }

            //リフレッシュトークンが有効期限切れ
            if(token.getLifeTimeRefreshToken() < (System.currentTimeMillis()/1000L)){
                ctx.status(StatusCode.BadRequest.code())
                .json(MapBuilder.New().put("error","invalid_grant").put("error_description","invalid_grant"));
                return;
            }

            //サービス別に割り振られたアカウントのIDから本来のアカウントUUIDを探す
            Optional<IPPID> ppid = IPPIDStore
                                    .getInstance()
                                    .getPPID(token.getAccountID());
            if(!ppid.isPresent()){
                ctx.status(StatusCode.NotFound.code()).result("ppid");
                return;
            }
            Optional<IAccount> opAccount = IAccountStore.getInstance().getAccountByUUID(ppid.get().getAccountID());

            //そもそもアカウントがないのでアクセストークンを削除する
            if(!opAccount.isPresent()){
                ctx.status(StatusCode.NotFound.code());
                //削除
                IAccessTokenStore.getInstance().deleteTokenByID(token.getUUID());
                return;
            }

            IAccount account = opAccount.get();

            IAccessToken newToken = AccessTokenBuilder
                    .New()
                    .setUUID(token.getUUID())
                    .setAccountID(token.getAccountID())
                    .setClientID(token.getClientID())
                    .setAccessToken(UUID.randomUUID().toString())
                    .setNonce(token.getNonce())
                    .setScope(token.getScope())
                    .setLifeTimeRefreshToken(token.getLifeTimeRefreshToken())
                    .build();

            IAccessTokenStore.getInstance().updateToken(newToken);

            MapBuilder result = MapBuilder
                    .New()
                    .put("token_type", "Bearer")
                    .put("scope", newToken.getScope())
                    .put("access_token", newToken.getAccessToken())
                    .put("refresh_token", newToken.getRefreshToken())
                    .put("expires_in", IAccessToken._30DAY);

            List<ServicePermission> perms = ServicePermission.convertFromScope(token.getScope());

            if(ServicePermission.convertFromScope(token.getScope()).contains(ServicePermission.READ_UUID)){
                result.put("id_token",
                    JWTTokenCreator
                    .signHMAC256(
                        JWTTokenCreator
                        .addAuthenticateClaims(
                            JWT.create(),
                            service,
                            ppid.get(),
                            account,
                            perms,
                            Optional.empty(),
                            Config.get().getSelfURL(),
                            Config.get().getIDTokenValidMinutes()
                        ),
                        service.getSecretID()
                    )
                );
            }

            ctx.status(StatusCode.OK.code())
               .json(result);

        }else{
            ctx.status(StatusCode.BadRequest.code());
        }
    }
}

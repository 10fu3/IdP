package net.den3.IdP.Router.OAuth2.Token;

import net.den3.IdP.Entity.Account.IAccount;
import net.den3.IdP.Entity.Auth.IAccessToken;
import net.den3.IdP.Entity.Service.IService;
import net.den3.IdP.Store.Account.IAccountStore;
import net.den3.IdP.Store.Auth.IAccessTokenStore;
import net.den3.IdP.Store.Auth.IAuthFlowStore;
import net.den3.IdP.Store.Auth.ILoginTokenStore;
import net.den3.IdP.Store.Service.IServiceStore;
import net.den3.IdP.Util.MapBuilder;
import net.den3.IdP.Util.ParseJSON;
import net.den3.IdP.Util.StatusCode;

import java.util.Map;
import java.util.Optional;

/**
 * アクセストークンを取り消す
 */
public class URLRevokeToken {
    public static void mainFlow(io.javalin.http.Context ctx){

        Optional<String> accountUUID = ILoginTokenStore.getInstance().getAccountUUID(Optional.ofNullable(ctx.header("authorization")).orElse(""));
        if(accountUUID.isPresent()){
            String uuid = accountUUID.get();
            Optional<Map<String, String>> json = ParseJSON.convertToStringMap(ctx.body());
            Optional<IAccount> account = IAccountStore.getInstance().getAccountByUUID(uuid);
            if(!account.isPresent() || !json.isPresent()){
                ctx.status(StatusCode.BadRequest.code());
                return;
            }
            if(!json.get().containsKey("id")) {
                ctx.status(StatusCode.BadRequest.code()).json(MapBuilder.New().put("error", "invalid id").build());
                return;
            }
            IAuthFlowStore.getInstance().deleteAuthFlowByAccountUUID(uuid);
            IAccessTokenStore.getInstance().deleteTokenByAccountUUID(uuid);
            return;
        }

        Optional<String> clientId = Optional.ofNullable(ctx.formParam("client_id"));
        Optional<String> secret = Optional.ofNullable(ctx.formParam("client_secret"));
        Optional<String> accessToken = Optional.ofNullable(ctx.formParam("access_token"));

        if((!clientId.isPresent()) || (!secret.isPresent()) || (!accessToken.isPresent())){
            ctx.status(StatusCode.BadRequest.code()).result("bad parameter");
            return;
        }

        Optional<IService> service = IServiceStore.getInstance().getService(clientId.orElse(""));

        if(!(service.isPresent()) || (!service.get().getSecretID().equalsIgnoreCase(secret.orElse("")))){
            ctx.status(StatusCode.NotFound.code()).result("bad parameter");
            return;
        }

        Optional<IAccessToken> token = IAccessTokenStore.getInstance().getTokenByToken(accessToken.orElse(""));

        if(!token.isPresent()){
            ctx.status(StatusCode.NotFound.code()).result("bad token");
            return;
        }

        if(!token.get().getClientID().equalsIgnoreCase(service.get().getServiceID())){
            ctx.status(StatusCode.NotFound.code()).result("bad token");
            return;
        }

        //削除
        IAccessTokenStore.getInstance().deleteTokenByID(token.get().getUUID());
        IAuthFlowStore.getInstance().getAuthFlowByAccessToken(token.get().getAccessToken());

    }
}

package net.den3.IdP.Router.OAuth2.Token;

import net.den3.IdP.Entity.Auth.IAccessToken;
import net.den3.IdP.Entity.Service.IService;
import net.den3.IdP.Store.Auth.IAccessTokenStore;
import net.den3.IdP.Store.Auth.IAuthFlowStore;
import net.den3.IdP.Store.Service.IServiceStore;
import net.den3.IdP.Util.StatusCode;

import java.util.Optional;

public class URLRevokeToken {
    public static void mainFlow(io.javalin.http.Context ctx){

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

        //削除
        IAccessTokenStore.getInstance().deleteTokenByID(token.get().getUUID());
        IAuthFlowStore.getInstance().getAuthFlowByAccessToken(token.get().getAccessToken());

    }
}

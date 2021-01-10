package net.den3.IdP.Router.Account.API;

import net.den3.IdP.Store.Auth.ILoginTokenStore;
import net.den3.IdP.Util.StatusCode;

import java.util.Optional;

public class URLLogout {
    public static void mainFlow(io.javalin.http.Context ctx){

        Optional<String> accountUUID = ILoginTokenStore.getInstance().getAccountUUID(Optional.ofNullable(ctx.header("authorization")).orElse(""));
        if(accountUUID.isPresent()){
            String token = ctx.header("authorization");
            if (ILoginTokenStore.getInstance().deleteToken(token)) {
                ctx.status(StatusCode.OK.code()).header("authorization","");
            } else {
                ctx.status(StatusCode.NotFound.code());
            }
        }else{
            ctx.status(StatusCode.NotFound.code());
        }
    }
}

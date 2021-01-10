package net.den3.IdP.Router.Account.API;

import net.den3.IdP.Entity.Account.IAccount;
import net.den3.IdP.Store.Account.IAccountAttributeStore;
import net.den3.IdP.Store.Account.IAccountStore;
import net.den3.IdP.Store.Auth.ILoginTokenStore;
import net.den3.IdP.Util.StatusCode;

import java.util.Optional;

public class URLDeleteAccount {
    public static void mainFlow(io.javalin.http.Context ctx){
        String authorization = Optional.ofNullable(ctx.header("authorization")).orElse("");
        Optional<String> uuid = ILoginTokenStore.getInstance().getAccountUUID(authorization);
        if(!uuid.isPresent()){
            ctx.status(StatusCode.Unauthorized.code());
            return;
        }
        Optional<IAccount> account = IAccountStore.getInstance().getAccountByUUID(uuid.orElse(""));
        if(!account.isPresent()){
            ctx.status(StatusCode.Unauthorized.code());
            return;
        }

        if(account.get().getAttribute().isAdmin()){
            if(ctx.formParam("uuid") != null){
                IAccountAttributeStore.getInstance().deleteAttribute(ctx.formParam("uuid"));
                boolean result = IAccountStore.getInstance().deleteAccountInSQL(ctx.formParam("uuid"));
                ctx.status((result ? StatusCode.OK : StatusCode.NotFound).code());
                return;
            }
        }

        IAccountAttributeStore.getInstance().deleteAttribute(uuid.orElse(""));
        boolean result = IAccountStore.getInstance().deleteAccountInSQL(uuid.orElse(""));
        ctx.status((result ? StatusCode.OK : StatusCode.NotFound).code());
    }
}

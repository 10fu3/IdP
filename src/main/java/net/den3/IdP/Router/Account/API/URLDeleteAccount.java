package net.den3.IdP.Router.Account.API;

import net.den3.IdP.Entity.Account.IAccount;
import net.den3.IdP.Store.Account.IAccountAttributeStore;
import net.den3.IdP.Store.Account.IAccountStore;
import net.den3.IdP.Store.Auth.IAccessTokenStore;
import net.den3.IdP.Store.Auth.IAuthFlowStore;
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
                boolean result = IAccountStore.getInstance().delete(ctx.formParam("uuid"));
                ctx.status((result ? StatusCode.OK : StatusCode.NotFound).code());
                return;
            }
        }
        //ログインセッションを削除
        ILoginTokenStore.getInstance().deleteTokenByAccount(uuid.orElse(""));
        //認可フローを削除
        IAuthFlowStore.getInstance().deleteAuthFlowByAccountUUID(uuid.orElse(""));
        //アクセストークンを削除
        IAccessTokenStore.getInstance().deleteTokenByAccountUUID(uuid.orElse(""));
        //属性を削除
        IAccountAttributeStore.getInstance().deleteAttribute(uuid.orElse(""));
        //アカウントを削除
        boolean result = IAccountStore.getInstance().delete(uuid.orElse(""));
        ctx.status((result ? StatusCode.OK : StatusCode.NotFound).code());
    }
}

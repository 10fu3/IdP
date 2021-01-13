package net.den3.IdP.Router.Uploader;

import net.den3.IdP.Entity.Account.IAccount;
import net.den3.IdP.Store.Account.IAccountStore;
import net.den3.IdP.Store.Auth.ILoginTokenStore;
import net.den3.IdP.Store.Upload.IUploaderStore;
import net.den3.IdP.Util.StatusCode;

import java.util.ArrayList;
import java.util.Optional;

public class URLGetUploadFiles {
    public static void mainFlow(io.javalin.http.Context ctx){
        String authorization = Optional.ofNullable(ctx.header("authorization")).orElse("");
        Optional<String> uuid = ILoginTokenStore.getInstance().getAccountUUID(authorization);
        if(!uuid.isPresent()){
            ctx.status(StatusCode.Unauthorized.code());
            return;
        }
        Optional<IAccount> account = IAccountStore.getInstance().getAccountByUUID(uuid.orElse(""));
        if(!account.isPresent()){
            ctx.status(StatusCode.NotFound.code());
            return;
        }

        if(!account.get().getAttribute().isAdmin()){
            ctx.status(StatusCode.Forbidden.code());
            return;
        }

        ctx.json(IUploaderStore.getInstance().getUploadEntity().orElse(new ArrayList<>()));
    }
}

package net.den3.IdP.Router.Account.API;

import net.den3.IdP.Entity.Account.IAccount;
import net.den3.IdP.Entity.Auth.IAccessToken;
import net.den3.IdP.Store.Account.IAccountStore;
import net.den3.IdP.Store.Auth.IAccessTokenStore;
import net.den3.IdP.Store.Auth.ILoginTokenStore;
import net.den3.IdP.Util.MapBuilder;
import net.den3.IdP.Util.StatusCode;

import java.util.Optional;

public class URLGetProfile {

    public static void mainFlow(io.javalin.http.Context ctx){
        String authorization = Optional.ofNullable(ctx.header("authorization")).orElse("");
        if(Optional.ofNullable(ctx.header("authorization")).orElse("").contains("Bearer")){
            String bearer = authorization.replaceFirst("Bearer ","");
            Optional<IAccessToken> accessToken = IAccessTokenStore.getInstance().getTokenByToken(bearer);
            if(!accessToken.isPresent()){
                ctx.status(StatusCode.NotFound.code());
                return;
            }
            Optional<IAccount> opAccount = IAccountStore.getInstance().getAccountByUUID(accessToken.get().getAccountID());
            if(!opAccount.isPresent()){
                //存在しないアカウントのアクセストークンは削除する
                IAccessTokenStore.getInstance().deleteTokenByID(accessToken.get().getUUID());
                ctx.status(StatusCode.NotFound.code());
                return;
            }

            ctx.json(MapBuilder
                    .New()
                    .put("user_id",opAccount.get().getUUID())
                    .put("nick",opAccount.get().getNickName())
                    .put("icon",opAccount.get().getIconURL())
                    .build());

        }else{
            Optional<String> accountUUID = ILoginTokenStore.getInstance().getAccountUUID(authorization);
            if (accountUUID.isPresent()) {
                Optional<IAccount> account = IAccountStore.getInstance().getAccountByUUID(accountUUID.orElse(""));
                if(account.isPresent()){
                    ctx.status(StatusCode.OK.code())
                            .json(MapBuilder
                                    .New()
                                    .put("nick",account.get().getNickName())
                                    .put("icon",account.get().getIconURL())
                                    .build()
                            );
                    return;
                }
            }else{
                ctx.status(StatusCode.Unauthorized.code());
                return;
            }
            ctx.status(StatusCode.NotFound.code());
        }
    }
}

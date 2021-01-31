package net.den3.IdP.Router.Account.API;

import net.den3.IdP.Entity.Account.AccountBuilder;
import net.den3.IdP.Entity.Auth.LoginResult;
import net.den3.IdP.Logic.LoginAccount;
import net.den3.IdP.Store.Account.IAccountStore;
import net.den3.IdP.Store.Auth.ILoginTokenStore;
import net.den3.IdP.Util.MapBuilder;
import net.den3.IdP.Util.ParseJSON;
import net.den3.IdP.Util.StatusCode;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;


public class URLLogin {
    private static Boolean containsNeedKey(Map<String, String> json){
        return json.containsKey("mail") ||  json.containsKey("pass");
    }

    /**
     * ログイン時のメイン処理 トークンを返す
     * @param ctx HTTPリクエスト/レスポンス
     */
    public static void mainFlow(io.javalin.http.Context ctx){
        Optional<Map<String, String>> wrapJson = ParseJSON.convertToStringMap(ctx.body());

        if(!wrapJson.isPresent() || !containsNeedKey(wrapJson.get())){
            ctx.status(StatusCode.BadRequest.code());
            return;
        }

        LoginResult loginResult = LoginAccount.containsAccount(wrapJson.get());
        String token = UUID.randomUUID().toString();
        if(loginResult == LoginResult.SUCCESS && loginResult.account.isPresent()){
           ILoginTokenStore.getInstance().putToken(token,loginResult.account.get().getUUID());

           //ログイン時刻を更新する
           IAccountStore.getInstance().update(AccountBuilder.Edit(loginResult.account.get()).setLastLogin(System.currentTimeMillis()/1000L).build());

           ctx.status(StatusCode.OK.code()).json(
                   MapBuilder
                   .New()
                   .put("authorization",token)
                   .build()
           );
        }else{
            ctx.status(StatusCode.BadRequest.code()).json(
                    MapBuilder
                    .New()
                    .put("message",loginResult.getMessage())
                    .build()
            );
        }
    }
}

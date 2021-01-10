package net.den3.IdP.Router.Account.API;

import net.den3.IdP.Entity.Account.AccountAttribute;
import net.den3.IdP.Entity.Account.AccountBuilder;
import net.den3.IdP.Entity.Account.IAccount;
import net.den3.IdP.Logic.Entry.CheckAccountResult;
import net.den3.IdP.Logic.Entry.EntryAccount;
import net.den3.IdP.Store.Account.IAccountAttributeStore;
import net.den3.IdP.Store.Account.IAccountStore;
import net.den3.IdP.Store.Auth.ILoginTokenStore;
import net.den3.IdP.Util.ContentsType;
import net.den3.IdP.Util.ParseJSON;
import net.den3.IdP.Util.StatusCode;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class URLEntryAccount {
    /**
     * HTTPリクエストに仮登録に必要なパラメーターの有無を返す
     * @param json JSONオブジェクト
     * @return 仮登録に必要なパラメーターがある→true ない→false
     */
    private static Boolean containsNeedKey(Map<String,String> json){
        return json.containsKey("mail") ||  json.containsKey("pass") || json.containsKey("nick");
    }

    /**
     * HTTPリクエストを受け取って仮登録をする
     * @param ctx io.javalin.http.Context
     */
    public static void mainFlow(io.javalin.http.Context ctx){
        ctx.res.setContentType(ContentsType.JSON.get());
        Optional<Map<String,String>> optionalReqJSON = ParseJSON.convertToStringMap(ctx.body());


        String authorization = Optional.ofNullable(ctx.header("authorization")).orElse("");
        Optional<String> uuid = ILoginTokenStore.getInstance().getAccountUUID(authorization);
        Optional<IAccount> account = IAccountStore.getInstance().getAccountByUUID(uuid.orElse(""));

        //管理者権限持ちアカウントが新規アカウントを強制作成する
        if(optionalReqJSON.isPresent() && account.isPresent() && account.get().getAttribute().isAdmin()){
            //JSONからメール/パスワード/ニックネームを拾う
            String createUUID = UUID.randomUUID().toString();
            String mail = optionalReqJSON.get().get("mail");
            String pass = optionalReqJSON.get().get("pass");
            String nickname = optionalReqJSON.get().get("nick");
            boolean admin = optionalReqJSON.get().get("admin").equalsIgnoreCase("true");
            boolean frozen = optionalReqJSON.get().get("frozen").equalsIgnoreCase("true");

            if(CheckAccountResult.SUCCESS == EntryAccount.checkAccount(mail,pass,nickname)){
                IAccount createAccount = AccountBuilder
                                         .New()
                                         .setUUID(createUUID)
                                         .setMail(mail)
                                         .setNickName(nickname)
                                         .setSecurePass(pass,createUUID)
                                         .setAttribute(new AccountAttribute(createUUID,admin,frozen))
                                         .build();
                ctx.status((IAccountStore.getInstance().addAccountInSQL(createAccount) ? StatusCode.OK : StatusCode.BadRequest).code());
            }else{
                ctx.status(StatusCode.BadRequest.code());
            }
        }


        //JSONじゃないない何かを送りつけられた場合/そもそもリクエストにmail/passパラメータが含まれてない可能性を排除する
        if(!optionalReqJSON.isPresent() || !containsNeedKey(optionalReqJSON.get())){
            ctx.status(StatusCode.BadRequest.code());
            return;
        }
        //登録処理の結果を返す
        ctx.json(EntryAccount.mainFlow(optionalReqJSON.get()));
    }
}

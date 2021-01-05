package net.den3.IdP.Router.Account;

import net.den3.IdP.Config;
import net.den3.IdP.Entity.Account.ITempAccount;
import net.den3.IdP.Store.Account.IAccountStore;
import net.den3.IdP.Store.Account.ITempAccountStore;
import net.den3.IdP.Util.ContentsType;

import java.time.Instant;
import java.util.Optional;

public class URLConfirmedEntry {

    private static final Long ONE_DAY = 86400L;

    /**
     * アカウント有効化リンクを踏むとアクセスされるメソッド
     * @param ctx io.javalin.http.Context
     */
    public static void mainFlow(io.javalin.http.Context ctx){
        String key = ctx.pathParam("key");
        //有効化キーを持つアカウントの情報が仮登録アカウントストアに存在しない場合
        if(!ITempAccountStore.getInstance().containsAccountByKey(key)){
            ctx.redirect("/account/register/invalid");
            return;
        }
        //有効化キーを持つアカウントの情報が仮登録アカウントストアに存在しない場合その2
        Optional<ITempAccount> tempAccount = ITempAccountStore.getInstance().getAccountByKey(key);
        if(!tempAccount.isPresent()){
            ctx.redirect("/account/register/invalid");
            return;
        }
        //もし有効期限が切れていたら
        if(tempAccount.get().getRegisteredDate()+ONE_DAY < Instant.now().getEpochSecond()){
            //削除する
            ITempAccountStore.getInstance().removeAccountInTemporaryDB(tempAccount.get().getKey());
            ctx.redirect("/account/register/invalid");
            return;
        }

        //DBへの追加がうまくいき,仮登録アカウントストアからも削除が成功するとtrueを返す
        if(IAccountStore.getInstance().addAccountInSQL(tempAccount.get(),ITempAccountStore.getInstance())
                && ITempAccountStore.getInstance().removeAccountInTemporaryDB(tempAccount.get().getKey())){
            ctx.redirect(Config.get().getLoginURL());
        }else{
            //失敗したときはfalseを返す
            ctx.redirect("/account/register/invalid");
        }

    }

    public static void invalid(io.javalin.http.Context ctx){
        ctx.res.setContentType(ContentsType.HTML.get());
        ctx.result("<h1>エラー</h1><br>登録申請は無効化されたか、エラーが発生しています. 管理者までお問い合わせください");
    }
}

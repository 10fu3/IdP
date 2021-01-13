package net.den3.IdP.Router.Account.API;

import net.den3.IdP.Config;
import net.den3.IdP.Entity.Account.AccountAttribute;
import net.den3.IdP.Entity.Account.AccountBuilder;
import net.den3.IdP.Entity.Account.IAccount;
import net.den3.IdP.Entity.Mail.MailEntity;
import net.den3.IdP.Logic.Entry.CheckAccountResult;
import net.den3.IdP.Logic.Entry.EntryAccount;
import net.den3.IdP.Logic.Entry.MailSendService;
import net.den3.IdP.Store.Account.IAccountStore;
import net.den3.IdP.Store.Auth.ILoginTokenStore;
import net.den3.IdP.Util.MapBuilder;
import net.den3.IdP.Util.ParseJSON;
import net.den3.IdP.Util.StatusCode;

import java.util.*;

public class URLUpdateProfile {
    public static void mainFlow(io.javalin.http.Context ctx){
        String authorization = Optional.ofNullable(ctx.header("authorization")).orElse("");
        Optional<String> accountUUID = ILoginTokenStore.getInstance().getAccountUUID(authorization);
        if(!accountUUID.isPresent()){
            //未認証
            ctx.status(StatusCode.Unauthorized.code());
            return;
        }
        Optional<IAccount> opAccount = IAccountStore.getInstance().getAccountByUUID(accountUUID.get());
        if(!opAccount.isPresent()){
            //アカウントがない
            ctx.status(StatusCode.NotFound.code());
            return;
        }
        boolean admin = opAccount.get().getAttribute().isAdmin();
        //変更対象のアカウント
        IAccount account = opAccount.get();

        Optional<Map<String, String>> opReq = ParseJSON.convertToStringMap(ctx.body());
        if(!opReq.isPresent()){
            //JSON形式のリクエストではない
            ctx.status(StatusCode.BadRequest.code());
            return;
        }

        CheckAccountResult filter = CheckAccountResult.SUCCESS;
        //管理者権限持ちが他のアカウントを操作する場合
        if(admin && opReq.get().containsKey("uuid")){
            Optional<IAccount> target = IAccountStore.getInstance().getAccountByUUID(opReq.get().get("uuid"));
            if(!target.isPresent()){
                //変更対象のアカウントがない
                ctx.status(StatusCode.NotFound.code());
                return;
            }
            account = target.get();
        }

        AccountBuilder builder = AccountBuilder.Edit(account);

        for (String k : opReq.get().keySet()) {
            switch (k) {
                case "mail":
                    if (filter == CheckAccountResult.SUCCESS) {
                        filter = EntryAccount.checkMail(opReq.get().get(k));
                        if(filter == CheckAccountResult.SUCCESS){
                            builder.setMail(opReq.get().get(k));
                        }
                    }
                    break;
                case "new-pass":
                    if (filter == CheckAccountResult.SUCCESS) {
                        filter = EntryAccount.checkPass(opReq.get().get(k));
                        if(filter == CheckAccountResult.SUCCESS){
                            builder.setSecurePass(opReq.get().get(k),account.getUUID());
                        }
                    }
                    break;
                case "nick":
                    if (filter == CheckAccountResult.SUCCESS) {
                        filter = EntryAccount.checkNickName(opReq.get().get(k));
                        if(filter == CheckAccountResult.SUCCESS){
                            builder.setNickName(opReq.get().get(k));
                        }
                    }
                    break;
                case "frozen":
                    if (filter == CheckAccountResult.SUCCESS && !admin) {
                        filter = CheckAccountResult.ERROR_PERMISSION;
                    }else{
                        AccountAttribute aa = account.getAttribute();
                        builder.setAttribute(new AccountAttribute(aa.isAdmin(),"true".equalsIgnoreCase(opReq.get().get(k))));
                    }
                    break;
                case "admin":
                    if (filter == CheckAccountResult.SUCCESS && !admin) {
                        filter = CheckAccountResult.ERROR_PERMISSION;
                    }else{
                        AccountAttribute aa = account.getAttribute();
                        builder.setAttribute(new AccountAttribute("true".equalsIgnoreCase(opReq.get().get(k)),aa.isFrozen()));
                    }
                    break;
            }
        }

        //メールアドレスを変えてない あるいはメールアドレス以外を変えた
        if(filter == CheckAccountResult.SUCCESS){
            //情報を更新
            IAccountStore.getInstance().updateAccountInSQL(builder.build());

            //管理者権限による操作の場合,通知メールをださない
            if(!opAccount.get().getUUID().equalsIgnoreCase(account.getUUID())){
                return;
            }
            //送信元の名前
            String fromName = "電子計算機研究会 アカウント情報変更通知";
            //メール送信オブジェクト
            MailSendService mailService = new MailSendService(Config.get().getEntryMailAddress(),Config.get().getEntryMailPassword(),fromName);
            //非同期でメールは送られる
            mailService.send(
                    new MailEntity()
                            .setTo(oldMail)
                            .setTitle("[電子計算機研究会] アカウント情報変更の確認メール")
                            .setBody("あなたのアカウント情報が変更されました.<br>心当たりがなければ,管理者までお問い合わせください<br>変更処理日時: "
                                    +LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 hh時 mm分"))),
                    ()->{
                        //成功したとき
                    },
                    ()->{
                        //失敗したとき
                        System.out.println("メール送信失敗");
                    }
            );

        }else{
            ctx.status(StatusCode.BadRequest.code()).json(MapBuilder.New().put("error",filter.getString()).build());
        }

    }
}

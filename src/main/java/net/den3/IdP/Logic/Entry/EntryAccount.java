package net.den3.IdP.Logic.Entry;

import net.den3.IdP.Config;
import net.den3.IdP.Entity.Account.ITempAccount;
import net.den3.IdP.Entity.Account.TempAccountBuilder;
import net.den3.IdP.Entity.Mail.MailEntity;
import net.den3.IdP.Store.Account.IAccountStore;
import net.den3.IdP.Store.Account.ITempAccountStore;
import net.den3.IdP.Util.MapBuilder;
import net.den3.IdP.Util.StringChecker;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 登録申請処理クラス
 */
public class EntryAccount {

    /**
     * 仮登録申請されたアカウントの情報をチェックを依頼し,可能なら仮登録処理まで行う
     * @param reqJSON 仮登録申請時に送られてくるJSON
     * @return クライアントに返されるJSON statusが成功/失敗を表し messageがエラーの原因を返す
     */
    public static Map<String,Object> mainFlow(Map<String,String> reqJSON){

        Config config = Config.get();

        //JSONからメール/パスワード/ニックネームを拾う
        String mail =reqJSON.get("mail");
        String pass = reqJSON.get("pass");
        String nickname = reqJSON.get("nick");

        //送信元の名前
        String fromName = "電子計算機研究会 仮登録案内";

        //メール送信オブジェクト
        MailSendService mailService = new MailSendService(config.getEntryMailAddress(),config.getEntryMailPassword(),fromName);

        //基準に満たない/ルール違反をしているメールアドレス/パスワードか調べる
        CheckAccountResult checkAccountResult = EntryAccount.checkAccount(mail, pass,nickname);

        //チェックにひっかかるアカウント情報ならばここで弾く
        if(checkAccountResult != CheckAccountResult.SUCCESS){
            //ここに到達するときは登録処理に失敗している
            return MapBuilder.New()
                    .put("status","ERROR")
                    .put("message",checkAccountResult.getString())
                    .build();
        }

        //すでに仮登録されていたら上書きする
        Optional<ITempAccount> sameAccount = ITempAccountStore.getInstance().getAccountByMail(mail);
        //ここでDBから削除する
        sameAccount.ifPresent(account -> ITempAccountStore.getInstance().removeAccountInTemporaryDB(account.getKey()));

        //<-- ここまでで基準に満たないアカウント登録はすべて却下されている -->
        //管理に使う一時的なキーを発行
        //UUIDを発行する
        String queueID = UUID.randomUUID().toString();

        String uuid = UUID.randomUUID().toString();
        //ここに仮登録処理を書く 発行時刻を1970年から秒単位で記述
        ITempAccount tempAccount = TempAccountBuilder
                                    .New()
                                    .setAccountUUID(uuid)
                                    .setKey(queueID)
                                    .setMail(mail)
                                    .setSecurePass(pass,uuid)
                                    .setRegisteredDate(Instant.now().getEpochSecond())
                                    .setNick(nickname)
                                    .build();


//        ITempAccount tempAccount = TemporaryAccountEntity.create(mail,pass,String.valueOf(TimeUnit.SECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS)),queueID);
//        tempAccount.setNickName(nickname);
        //仮登録テーブルに登録する
        if(!ITempAccountStore.getInstance().addAccountInTemporaryDB(tempAccount)){
            return MapBuilder.New()
                    .put("status","ERROR")
                    .put("message","Internal Error")
                    .build();
        }

        //非同期でメールは送られる
        mailService.send(
                new MailEntity()
                .setTo(mail)
                .setTitle(Config.get().getEntryMailTitle())
                .setBody(config.getEntryMailBody()+
                        config.getSelfURL()+"/account/register/goal/"+queueID),
                ()->{
                    //成功したとき (特に何もしない)
                    System.out.println("メール送信済み");
                },
                ()->{
                    //失敗したとき
                    System.out.println("メール送信失敗");
                    ITempAccountStore.getInstance().removeAccountInTemporaryDB(queueID);
                }
        );
        return MapBuilder.New().put("status","success").build();
    }

    public static CheckAccountResult checkNickName(String nickname){
        if(StringChecker.containsNotAllowCharacter(nickname)){
            return CheckAccountResult.ERROR_NOT_ALLOW_CHAR;
        }
        return CheckAccountResult.SUCCESS;
    }

    public static CheckAccountResult checkMail(String mail){
        if(!StringChecker.isMailAddress(mail)){
            //Invalid e-address
            return CheckAccountResult.ERROR_MAIL;
        }
        if(StringChecker.containsNotAllowCharacter(mail)){
            return CheckAccountResult.ERROR_NOT_ALLOW_CHAR;
        }
        if(IAccountStore.getInstance().containsByMail(mail)){
            //Already registered e-address
            return CheckAccountResult.ERROR_SAME;
        }
        if(!mail.matches(Config.get().getEntryMailRegex())){
            return CheckAccountResult.ERROR_NOT_MATCH_DOMAIN;
        }
        //SUCCESS
        return CheckAccountResult.SUCCESS;
    }

    public static CheckAccountResult checkPass(String pass){
        if(pass.length() < Config.get().getMinimumPassword()){
            return CheckAccountResult.ERROR_PASSWORD_LENGTH;
        }
        if(StringChecker.containsNotAllowCharacter(pass)){
            return CheckAccountResult.ERROR_NOT_ALLOW_CHAR;
        }
        return CheckAccountResult.SUCCESS;
    }

    /**
     * 登録申請されたアカウントの情報が正しくかつ,すでに入力されたものではないかを調べる
     * @param mail 登録申請用メールアドレス
     * @param pass 登録申請用パスワード
     * @return CheckAccountResult列挙体
     */
    public static CheckAccountResult checkAccount (String mail, String pass,String nickname){

        CheckAccountResult checkMail = checkMail(mail);
        CheckAccountResult checkPass = checkPass(pass);
        CheckAccountResult checkNick = checkNickName(nickname);

        if(checkMail !=  CheckAccountResult.SUCCESS){
            return checkMail;
        }
        if(checkPass != CheckAccountResult.SUCCESS){
            return checkPass;
        }
        return checkNick;

        //SUCCESS
    }


}

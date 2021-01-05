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
     * @param store 仮アカウントストア
     * @param accountStore アカウントストア
     * @param config サーバー設定情報
     * @return クライアントに返されるJSON statusが成功/失敗を表し messageがエラーの原因を返す
     */
    public static Map<String,Object> mainFlow(Map<String,String> reqJSON,ITempAccountStore store,IAccountStore accountStore,Config config){
        //JSONからメール/パスワード/ニックネームを拾う
        String mail =reqJSON.get("mail");
        String pass = reqJSON.get("pass");
        String nickname = reqJSON.get("nick");

        //送信元の名前
        String fromName = "電子計算機研究会 仮登録案内";

        //メール送信オブジェクト
        MailSendService mailService = new MailSendService(config.getEntryMailAddress(),config.getEntryMailPassword(),fromName);

        System.out.println(config.getEntryMailAddress()+" "+config.getEntryMailPassword());

        //基準に満たない/ルール違反をしているメールアドレス/パスワードか調べる
        CheckAccountResult checkAccountResult = EntryAccount.checkAccount(accountStore,mail, pass,nickname);

        //チェックにひっかかるアカウント情報ならばここで弾く
        if(checkAccountResult != CheckAccountResult.SUCCESS){
            //ここに到達するときは登録処理に失敗している
            return MapBuilder.New()
                    .put("status","ERROR")
                    .put("message",checkAccountResult.getString())
                    .build();
        }

        //すでに仮登録されていたら上書きする
        Optional<ITempAccount> sameAccount = store.getAccountByMail(mail);
        //ここでDBから削除する
        sameAccount.ifPresent(account -> store.removeAccountInTemporaryDB(account.getKey()));

        //<-- ここまでで基準に満たないアカウント登録はすべて却下されている -->
        //管理に使う一時的なキーを発行
        //UUIDを発行する
        String queueID = UUID.randomUUID().toString();
        //ここに仮登録処理を書く 発行時刻を1970年から秒単位で記述
        ITempAccount tempAccount = TempAccountBuilder
                                    .New()
                                    .setKey(queueID)
                                    .setMail(mail)
                                    .setSecurePass(pass,mail)
                                    .setRegisteredDate(Instant.now().getEpochSecond())
                                    .setNick(nickname)
                                    .build();


//        ITempAccount tempAccount = TemporaryAccountEntity.create(mail,pass,String.valueOf(TimeUnit.SECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS)),queueID);
//        tempAccount.setNickName(nickname);
        //仮登録テーブルに登録する
        if(!store.addAccountInTemporaryDB(tempAccount)){
            return MapBuilder.New()
                    .put("status","ERROR")
                    .put("message","Internal Error")
                    .build();
        }

        //非同期でメールは送られる
        mailService.send(
                new MailEntity()
                .setTo(mail)
                .setTitle("[電子計算機研究会] 仮登録申請の確認メール")
                .setBody("仮登録ありがとうございます.<br>本登録をするには本メール到着後1日以内に次のURLにアクセスしてください <br>"+
                        config.getSelfURL()+"/account/register/goal/"+queueID),
                ()->{
                    //成功したとき (特に何もしない)
                    System.out.println("メール送信済み");
                },
                ()->{
                    //失敗したとき
                    System.out.println("メール送信失敗");
                    store.removeAccountInTemporaryDB(queueID);
                }
        );
        return MapBuilder.New().put("status","success").build();
    }

    /**
     * 登録申請されたアカウントの情報が正しくかつ,すでに入力されたものではないかを調べる
     * @param mail 登録申請用メールアドレス
     * @param pass 登録申請用パスワード
     * @return CheckAccountResult列挙体
     */
    public static CheckAccountResult checkAccount (IAccountStore store, String mail, String pass,String nickname){
        if(!StringChecker.isMailAddress(mail)){
            //Invalid e-address
            return CheckAccountResult.ERROR_MAIL;
        }
        if(StringChecker.containsNotAllowCharacter(mail) || StringChecker.containsNotAllowCharacter(pass) || StringChecker.containsNotAllowCharacter(nickname)){
            return CheckAccountResult.ERROR_NOT_ALLOW_CHAR;
        }
        if(store.containsAccountInSQL(mail)){
            //Already registered e-address
            return CheckAccountResult.ERROR_SAME;
        }
        if(pass.length() < 7){
            //Need 8 characters or more
            return CheckAccountResult.ERROR_PASSWORD_LENGTH;
        }
        //SUCCESS
        return CheckAccountResult.SUCCESS;
    }


}

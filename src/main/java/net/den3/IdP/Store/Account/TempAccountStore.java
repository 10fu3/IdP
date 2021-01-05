package net.den3.IdP.Store.Account;

import net.den3.IdP.Entity.Account.ITempAccount;
import net.den3.IdP.Entity.Account.TempAccountBuilder;
import net.den3.IdP.Store.IDBAccess;
import net.den3.IdP.Store.InjectionStore;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class TempAccountStore implements ITempAccountStore {

    private final IDBAccess store = (IDBAccess) InjectionStore.get().get("rdbms").orElseThrow(NullPointerException::new);

    public TempAccountStore(){
        store.controlSQL((con)->{
            try{
                return Optional.of(Collections.singletonList(
                        //テーブルがなかったら作る仕組み
                        con.prepareStatement("CREATE TABLE IF NOT EXISTS temp_account_repository ("
                                +"active_key VARCHAR(256) PRIMARY KEY, "
                                +"registered_date VARCHAR(256), "
                                +"mail VARCHAR(256), "
                                +"pass VARCHAR(256), "
                                +"nick VARCHAR(256)"
                                +")")));
            }catch (SQLException ex){
                ex.printStackTrace();
                return Optional.empty();
            }
        });
    }

    /**
     * データベースに登録されたアカウントの中に指定した有効化キーを持つアカウントがあるか探す
     *
     * @param key 有効化キー
     * @return true->存在する false->存在しない
     */
    @Override
    public boolean containsAccountByKey(String key) {
        Optional<List<Map<String, String>>> optionalList = store.getLineBySQL(Collections.singletonList("active_key"),(con) -> {
            try {
                //account_repositoryからmailの一致するものを探してくる
                PreparedStatement pS = con.prepareStatement("SELECT * FROM temp_account_repository WHERE active_key = ?;");
                pS.setString(1,key);
                return Optional.of(pS);
            } catch (SQLException sqlex) {
                return Optional.empty();
            }
        });
        return optionalList.isPresent() && optionalList.get().size() == 1;
    }

    /**
     * データベースに登録されたアカウントの中に指定したメールアドレスを持つアカウントがあるか探す
     *
     * @param mail アカウントのメールアドレス
     * @return true->存在する false->存在しない
     */
    @Override
    public boolean containsAccountByMail(String mail) {
        Optional<List<Map<String, String>>> optionalList = store.getLineBySQL(Collections.singletonList("mail"),(con) -> {
            try {
                //account_repositoryからmailの一致するものを探してくる
                PreparedStatement pS = con.prepareStatement("SELECT * FROM temp_account_repository WHERE mail= ?;");
                pS.setString(1,mail);
                return Optional.of(pS);
            } catch (SQLException sqlex) {
                return Optional.empty();
            }
        });
        return optionalList.isPresent() && optionalList.get().size() == 1;
    }

    /**
     * 有効化キーを持つアカウントを返す
     *
     * @param mail 有効化キー
     * @return 仮アカウントエンティティ
     */
    @Override
    public Optional<ITempAccount> getAccountByMail(String mail) {
        List<String> columns = Arrays.asList("active_key","registered_date","mail","pass","nick");
        Optional<List<Map<String, String>>> optionalList = store.getLineBySQL(columns,(con) -> {
            try {
                //account_repositoryからmailの一致するものを探してくる
                PreparedStatement pS = con.prepareStatement("SELECT * FROM temp_account_repository WHERE mail = ?;");
                pS.setString(1,mail);
                return Optional.of(pS);
            } catch (SQLException sqlex) {
                sqlex.printStackTrace();
                return Optional.empty();
            }
        });
        if(!optionalList.isPresent()){
            return Optional.empty();
        }
        Map<String, String> map = optionalList.get().stream().findAny().get();
        ITempAccount a = new TempAccountBuilder()
                            .setKey(map.get("active_key"))
                            .setRegisteredDate(Long.parseLong(map.get("registered_date")))
                            .setMail(map.get("mail"))
                            .setPass(map.get("pass"))
                            .setNick(map.get("nick"))
                            .build();
        return Optional.of(a);
    }

    /**
     * 有効化キーを持つアカウントを返す
     *
     * @param key 有効化キー
     * @return 仮アカウントエンティティ
     */
    @Override
    public Optional<ITempAccount> getAccountByKey(String key) {
        List<String> columns = Arrays.asList("active_key","registered_date","mail","pass","nick");
        Optional<List<Map<String, String>>> optionalList = store.getLineBySQL(columns,(con) -> {
            try {
                //account_repositoryからkeyの一致するものを探してくる
                PreparedStatement pS = con.prepareStatement("SELECT * FROM temp_account_repository WHERE active_key = ?;");
                pS.setString(1,key);
                return Optional.of(pS);
            } catch (SQLException sqlex) {
                sqlex.printStackTrace();
                return Optional.empty();
            }
        });
        if(!optionalList.isPresent()){
            return Optional.empty();
        }
        Map<String, String> map = optionalList.get().stream().findAny().get();
        ITempAccount a = new TempAccountBuilder()
                .setKey(map.get("active_key"))
                .setRegisteredDate(Long.parseLong(map.get("registered_date")))
                .setMail(map.get("mail"))
                .setPass(map.get("pass"))
                .setNick(map.get("nick"))
                .build();
        return Optional.of(a);
    }

    /**
     * アカウントを仮登録DBに登録する 1日後に無効化
     *
     * @param tempAccount 仮アカウントエンティティ
     * @return 成功->true 失敗->false
     */
    @Override
    public boolean addAccountInTemporaryDB(ITempAccount tempAccount) {
        return store.controlSQL((con)->{
            try {
                //INSET文の発行 uuid mail pass nick icon last_login_timeの順
                PreparedStatement pS = con.prepareStatement("INSERT INTO temp_account_repository VALUES (?,?,?,?,?) ;");
                //
                pS.setString(1, tempAccount.getKey());
                //
                pS.setString(2, String.valueOf(tempAccount.getRegisteredDate()));
                //
                pS.setString(3, tempAccount.getMail());
                //SQL文の1個めの?にmailを代入する
                pS.setString(4, tempAccount.getPassHash());
                //SQL文の1個めの?にmailを代入する
                pS.setString(5, tempAccount.getNick());
                return Optional.of(Collections.singletonList(pS));
            } catch (SQLException sqlex) {
                sqlex.printStackTrace();
                return Optional.empty();
            }
        });
    }

    /**
     * データベースに登録されたアカウントをすべて取得する
     *
     * @return アカウントエンティティのリスト
     */
    @Override
    public Optional<List<ITempAccount>> getAccountsAll() {
        List<String> columns = Arrays.asList("active_key","registered_date","mail","pass","nick");
        Optional<List<Map<String, String>>> optionalList = store.getLineBySQL(columns,(con) -> {
            try {
                //account_repositoryからmailの一致するものを探してくる
                PreparedStatement pS = con.prepareStatement("SELECT * FROM temp_account_repository;");
                return Optional.of(pS);
            } catch (SQLException sqlex) {
                sqlex.printStackTrace();
                return Optional.empty();
            }
        });
        return optionalList.map(list-> list.stream().map(map->
                new TempAccountBuilder()
                    .setKey(map.get("active_key"))
                    .setRegisteredDate(Long.parseLong(map.get("registered_date")))
                    .setMail(map.get("mail"))
                    .setPass(map.get("pass"))
                    .setNick(map.get("nick"))
                    .build()
            ).collect(Collectors.toList()));
    }

    /**
     * データベースに登録されたキーと紐付けされたアカウントを削除する
     *
     * @param key 有効化キー
     * @return 成功->true 失敗->false
     */
    @Override
    public boolean removeAccountInTemporaryDB(String key) {
        return store.controlSQL(con->{
            try{
                PreparedStatement ps = con.prepareStatement("DELETE FROM temp_account_repository WHERE active_key = ?");
                ps.setString(1,key);
                return Optional.of(Collections.singletonList(ps));
            }catch (SQLException ex){
                ex.printStackTrace();
                return Optional.empty();
            }
        });
    }
}

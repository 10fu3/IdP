package net.den3.IdP.Store.Account;

import net.den3.IdP.Entity.Account.AccountAttribute;
import net.den3.IdP.Entity.Account.AccountBuilder;
import net.den3.IdP.Entity.Account.IAccount;
import net.den3.IdP.Entity.Account.ITempAccount;
import net.den3.IdP.Store.IDBAccess;
import net.den3.IdP.Store.InjectionStore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AccountStore implements IAccountStore{

    private final IDBAccess store = (IDBAccess)InjectionStore.get().get("rdbms").orElseThrow(NullPointerException::new);

    public AccountStore(){
        store.controlSQL((con)->{
            try{
                return Optional.of(Collections.singletonList(
                        //テーブルがなかったら作る仕組み
                        con.prepareStatement(
                                //auth_flowテーブルを作る
                                "CREATE TABLE account_repository(" +
                                        "uuid VARCHAR(255) PRIMARY KEY," +
                                        "mail VARCHAR(255)," +
                                        "pass VARCHAR(255)," +
                                        "nick VARCHAR(255)," +
                                        "icon VARCHAR(255)," +
                                        "last_login_time VARCHAR(255))")));
            }catch (SQLException ex){
                ex.printStackTrace();
                return Optional.empty();
            }
        });
    }

    /**
     * 指定されたUUIDを持つアカウントがアカウントストアに登録されているかどうか
     * @param uuid 調べる対象のUUID
     * @return true->存在する false->存在しない
     */
    @Override
    public boolean containsAccountInSQLByUUID(String uuid) {
        List<String> columns = Arrays.asList("uuid","mail","pass","nick","icon","last_login_time");
        Optional<List<Map<String, String>>> optionalList = store.getLineBySQL(columns,(con) -> {
            try {
                //account_repositoryからmailの一致するものを探してくる
                PreparedStatement pS = con.prepareStatement("SELECT * FROM account_repository WHERE uuid = ?;");
                pS.setString(1,uuid);
                return Optional.of(pS);
            } catch (SQLException sqlex) {
                sqlex.printStackTrace();
                return Optional.empty();
            }
        });

        return optionalList.isPresent() && !optionalList.get().isEmpty();
    }

    /**
     * 指定されたメールアドレスを持つアカウントがアカウントストアに登録されているかどうか
     *
     * @param mail 調べる対象のメールアドレス
     * @return true->存在する false->存在しない
     */
    @Override
    public boolean containsAccountInSQLByMail(String mail) {
        List<String> columns = Arrays.asList("uuid","mail","pass","nick","icon","last_login_time");
        Optional<List<Map<String, String>>> optionalList = store.getLineBySQL(columns,(con) -> {
            try {
                //account_repositoryからmailの一致するものを探してくる
                PreparedStatement pS = con.prepareStatement("SELECT * FROM account_repository WHERE mail = ?;");
                pS.setString(1,mail);
                return Optional.of(pS);
            } catch (SQLException sqlex) {
                sqlex.printStackTrace();
                return Optional.empty();
            }
        });

        return optionalList.isPresent() && !optionalList.get().isEmpty();
    }

    /**
     * アカウントの情報を更新する
     *
     * @param account 更新するエンティティ
     * @return true → 成功 false → 失敗
     */
    @Override
    public boolean updateAccountInSQL(IAccount account) {

        //属性ストアの属性を変更する
        IAccountAttributeStore.getInstance().updateAttribute(account);

        return store.controlSQL((con)->{
            try {
                //account_repositoryからmailの一致するものを探してくる
                PreparedStatement pS = con.prepareStatement("UPDATE account_repository SET mail=?, pass=?, nick=?, icon=?, last_login_time=? WHERE uuid=?;");
                //SQL文の1個めの?にmailを代入する
                pS.setString(1, account.getMail());
                //SQL文の1個めの?にmailを代入する
                pS.setString(2, account.getPasswordHash());
                //SQL文の1個めの?にmailを代入する
                pS.setString(3, account.getNickName());
                //SQL文の1個めの?にmailを代入する
                pS.setString(4, account.getIconURL());
                //SQL文の1個めの?にmailを代入する
                pS.setString(5, String.valueOf(account.getLastLoginTime()));

                pS.setString(6,account.getUUID());
                return Optional.of(Collections.singletonList(pS));
            } catch (SQLException sqlex) {
                sqlex.printStackTrace();
                return Optional.empty();
            }
        });
    }

    /**
     * アカウントをDBに登録する
     *
     * @param account アカウントエンティティ
     * @return 登録されたアカウントエンティティ
     */
    @Override
    public boolean addAccountInSQL(IAccount account) {

        //アカウントUUID
        String accountUUID = UUID.randomUUID().toString();

        //属性ストアに属性を登録する
        IAccountAttributeStore.getInstance().addAttribute(accountUUID,account.getAttribute());

        return store.controlSQL((con)->{
            try {
                //INSET文の発行 uuid mail pass nick icon last_login_timeの順
                PreparedStatement pS = con.prepareStatement("INSERT INTO account_repository VALUES (?,?,?,?,?,?) ;");
                //SQL文の1個目の?にuuidを代入する
                pS.setString(1, accountUUID);
                //SQL文の2個目の?にmailを代入する
                pS.setString(2, account.getMail());
                //SQL文の3個目の?にpasshashを代入する
                pS.setString(3, account.getPasswordHash());
                //SQL文の4個目の?にnickを代入する
                pS.setString(4, account.getNickName());
                //SQL文の5個目の?に仮アカウントのアイコンを入れる
                pS.setString(5, "https://i.imgur.com/R6tktJ6.jpg");
                //SQL文の6個目の?に登録日時代入する
                pS.setString(6, String.valueOf(account.getLastLoginTime()));

                return Optional.of(Collections.singletonList(pS));
            } catch (SQLException sqlex) {
                sqlex.printStackTrace();
                return Optional.empty();
            }
        });
    }

    /**
     * アカウントをDBに登録する
     *
     * @param tempAccount 仮アカウントエンティティ
     * @return 登録されたアカウントエンティティ
     */
    @Override
    public boolean addAccountInSQL(ITempAccount tempAccount,ITempAccountStore tempStore) {

        //アカウントUUID
        String accountUUID = UUID.randomUUID().toString();

        //属性ストアに属性を登録する
        IAccountAttributeStore.getInstance().addAttribute(accountUUID,new AccountAttribute());

        return store.controlSQL((con)->{
            try {
                //INSET文の発行 uuid mail pass nick icon last_login_timeの順
                PreparedStatement pS = con.prepareStatement("INSERT INTO account_repository VALUES (?,?,?,?,?,?) ;");
                //SQL文の1個目の?にuuidを代入する
                pS.setString(1, accountUUID);
                //SQL文の2個目の?にmailを代入する
                pS.setString(2, tempAccount.getMail());
                //SQL文の3個目の?にpasshashを代入する
                pS.setString(3, tempAccount.getPassHash());
                //SQL文の4個目の?にnickを代入する
                pS.setString(4, tempAccount.getNick());
                //SQL文の5個目の?に仮アカウントのアイコンを入れる
                pS.setString(5, "https://i.imgur.com/R6tktJ6.jpg");
                //SQL文の6個目の?に登録日時代入する
                pS.setString(6, String.valueOf(tempAccount.getRegisteredDate()));

                return Optional.of(Collections.singletonList(pS));
            } catch (SQLException sqlex) {
                sqlex.printStackTrace();
                return Optional.empty();
            }
        });
    }

    /**
     * アカウントをDBから削除する
     *
     * @param deleteAccount 削除対象のアカウントエンティティのUUID
     * @return true → 削除成功 false → 失敗
     */
    @Override
    public boolean deleteAccountInSQL(String deleteAccount) {
        return store.controlSQL((con)->{
            List<PreparedStatement> psL = new ArrayList<>();
            PreparedStatement statement;
            try {
                statement = con.prepareStatement("DELETE FROM account_repository WHERE uuid = ?;");
                statement.setString(1,deleteAccount);
                psL.add(statement);
                return Optional.of(psL);
            }catch (SQLException ignore){
                return Optional.empty();
            }
        });
    }


    /**
     * データベースに登録されたアカウントをすべて取得する
     * @return アカウントエンティティのリスト
     */
    @Override
    public Optional<List<IAccount>> getAccountsAll() {
        return getAccountBySQL((con)->{
            try {
                //account_repositoryからmailの一致するものを探してくる
                PreparedStatement pS = con.prepareStatement("SELECT * FROM account_repository");
                return Optional.of(pS);
            } catch (SQLException sqlex) {
                sqlex.printStackTrace();
                return Optional.empty();
            }
        });
    }

    /**
     * データベースの中にあるアカウントをメールアドレスで検索して取得する
     * @param mail メールアドレス
     * @return アカウントエンティティ
     */
    @Override
    public Optional<IAccount> getAccountByMail(String mail) {
        return getAccountBySQL((con) -> {
            try {
                //account_repositoryからmailの一致するものを探してくる
                PreparedStatement pS = con.prepareStatement("SELECT * FROM account_repository WHERE mail = ?");
                //SQL文の1個めの?にmailを代入する
                pS.setString(1, mail);
                return Optional.of(pS);
            } catch (SQLException sqlex) {
                sqlex.printStackTrace();
                return Optional.empty();
            }
        }).flatMap(i -> i.stream().findAny());
    }

    /**
     * データベースの中にあるアカウントをuuidで検索して取得する
     * @param id　アカウント固有のUUID
     * @return アカウントエンティティ
     */
    @Override
    public Optional<IAccount> getAccountByUUID(String id) {
        Optional<List<IAccount>> accountBySQL = getAccountBySQL((con) -> {
            try {
                //account_repositoryからmailの一致するものを探してくる
                PreparedStatement pS = con.prepareStatement("SELECT * FROM account_repository WHERE uuid = ?");
                //SQL文の1個めの?にuuidを代入する
                pS.setString(1, id);
                return Optional.of(pS);
            } catch (SQLException sqlex) {
                sqlex.printStackTrace();
                return Optional.empty();
            }
        });
        return  accountBySQL.flatMap(i -> i.stream().findAny());
    }

    /**
     * 発行したSQLに合致するアカウントを取得する
     * @param query Connectionを引数に持ち戻り値がPreparedStatement>のラムダ式/クロージャ
     * @return List[IAccount]
     */
    @Override
    public Optional<List<IAccount>> getAccountBySQL(Function<Connection, Optional<PreparedStatement>> query) {
        List<String> columns = Arrays.asList("uuid","mail","pass","nick","icon","last_login_time");
        Optional<List<Map<String, String>>> wrapResultList = store.getLineBySQL(columns,query);
        return wrapResultList.map(maps -> maps.stream().map(m -> new AccountBuilder()
                .setUUID(m.get("uuid"))
                .setMail(m.get("mail"))
                .setPasswordHash(m.get("pass"))
                .setNickName(m.get("nick"))
                .setIconURL(m.get("icon"))
                .setLastLogin(m.get("last_login_time"))
                .setAttribute(IAccountAttributeStore.getInstance().getAttribute(m.get("uuid")).orElse(new AccountAttribute()))
                .build())
                .collect(Collectors.toList()));
    }

    /**
     * 凍結されたアカウントをすべて取得する
     *
     * @return [凍結されたアカウント]
     */
    @Override
    public List<IAccount> getFrozenAccount() {
        return IAccountAttributeStore
                .getInstance()
                .getFrozenAccount()
                .stream()
                .filter(this::containsAccountInSQLByUUID)
                .map(uuid->getAccountByUUID(uuid).get())
                .collect(Collectors.toList());
    }

    /**
     * 管理者権限を持つアカウントをすべて取得する
     *
     * @return [管理者権限を持つアカウント]
     */
    @Override
    public List<IAccount> getAdminAccount() {
        return IAccountAttributeStore
                .getInstance()
                .getAdminAccount()
                .stream()
                .filter(this::containsAccountInSQLByUUID)
                .map(uuid->getAccountByUUID(uuid).get())
                .collect(Collectors.toList());
    }
}

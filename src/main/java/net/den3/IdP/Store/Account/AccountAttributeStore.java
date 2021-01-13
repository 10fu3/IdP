package net.den3.IdP.Store.Account;

import net.den3.IdP.Entity.Account.AccountAttribute;
import net.den3.IdP.Entity.Account.IAccount;
import net.den3.IdP.Store.IDBAccess;
import net.den3.IdP.Store.InjectionStore;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * アカウント属性を管理するストア
 */
public class AccountAttributeStore implements IAccountAttributeStore{
    private final IDBAccess store = (IDBAccess) InjectionStore.get().get("rdbms").orElseThrow(NullPointerException::new);
    private final String TABLE = "account_attribute_repository";

    private final List<String> fieldName = Arrays.asList("uuid","admin","frozen");

    public AccountAttributeStore(){
        store.controlSQL((con)->{
            try{
                return Optional.of(Collections.singletonList(
                        //テーブルがなかったら作る仕組み
                        con.prepareStatement(
                                //auth_flowテーブルを作る
                                "CREATE TABLE "+TABLE+"(" +
                                        "uuid VARCHAR(255) PRIMARY KEY," +
                                        "admin VARCHAR(5)," +
                                        "frozen VARCHAR(5)" +
                                        ")")));
            }catch (SQLException ex){
                ex.printStackTrace();
                return Optional.empty();
            }
        });
    }


    /**
     * 指定した列名から値の一致する行をすべて返す
     * @param targetField 列名
     * @param targetValue 値
     * @return [アカウントのUUID]
     */
    private Optional<List<String>> getUUIDs(String targetField, String targetValue){
        if(!fieldName.contains(targetField)){
            return Optional.empty();
        }
        return Optional.of(store.getLineBySQL(fieldName, (con) -> {
            try {
                //SQL文を組み立てている
                //文字列結合: SQL Injectionを考慮する必要があるが,限られた列名しかアクセスされないため利用する
                PreparedStatement ps = con.prepareStatement("SELECT * FROM "+TABLE+" WHERE "+targetField+" = ?");
                ps.setString(1, targetValue);
                return Optional.of(ps);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return Optional.empty();
            }
        })
        .orElse(new ArrayList<>())
        .stream()
        .map(p->p.get("uuid"))
        .collect(Collectors.toList()));
    }

    /**
     * 指定したアカウントの属性を返す
     * @param uuid アカウントのUUID
     * @return 属性エンティティ
     */
    @Override
    public Optional<AccountAttribute> getAttribute(String uuid){
        return Optional.of(store.getLineBySQL(fieldName, (con) -> {
            try {
                //SQL文を組み立てている
                //文字列結合: SQL Injectionを考慮する必要があるが,限られた列名しかアクセスされないため利用する
                PreparedStatement ps = con.prepareStatement("SELECT * FROM "+TABLE+" WHERE uuid = ?");
                ps.setString(1, uuid);
                return Optional.of(ps);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return Optional.empty();
            }
        })
                .orElse(new ArrayList<>())
                .stream()
                .map(p->new AccountAttribute("true".equalsIgnoreCase(p.get("admin")),"true".equalsIgnoreCase(p.get("frozen"))))
                .findAny()).flatMap(p->p);
    }

    /**
     * アカウントUUIDに紐付いているアカウント属性を削除
     *
     * @param uuid アカウントUUID
     */
    @Override
    public void deleteAttribute(String uuid) {
        store.controlSQL((con)->{
            List<PreparedStatement> psL = new ArrayList<>();
            PreparedStatement statement;
            try {
                statement = con.prepareStatement("DELETE FROM "+TABLE+" WHERE uuid = ?;");
                statement.setString(1,uuid);
                psL.add(statement);
                return Optional.of(psL);
            }catch (SQLException ignore){
                return Optional.empty();
            }
        });
    }

    /**
     * アカウントエンティティに紐付いてる属性を更新する
     *
     * @param account アカウントエンティティ
     */
    @Override
    public void updateAttribute(IAccount account) {
        store.controlSQL((con)->{
            try {
                //account_repositoryからmailの一致するものを探してくる
                PreparedStatement pS = con.prepareStatement("UPDATE "+TABLE+" SET admin=?, frozen=? WHERE uuid=?;");
                //SQL文の1個めの?にmailを代入する
                pS.setString(1, String.valueOf(account.getAttribute().isAdmin()));
                //SQL文の1個めの?にmailを代入する
                pS.setString(2, String.valueOf(account.getAttribute().isFrozen()));

                pS.setString(3,account.getUUID());
                return Optional.of(Collections.singletonList(pS));
            } catch (SQLException sqlex) {
                sqlex.printStackTrace();
                return Optional.empty();
            }
        });
    }

    /**
     * アカウントのUUIDとアカウントの属性をセットでDBに保存する
     *
     * @param uuid      アカウントのUUID
     * @param attribute 保存する属性
     */
    @Override
    public void addAttribute(String uuid, AccountAttribute attribute) {

        store.controlSQL((con)->{
            try {
                //INSET文の発行 uuid mail pass nick icon last_login_timeの順
                PreparedStatement pS = con.prepareStatement("INSERT INTO "+TABLE+" VALUES (?,?,?) ;");
                //SQL文の1個目の?にuuidを代入する
                pS.setString(1, uuid);
                //SQL文の2個目の?にfrozenを代入する
                pS.setString(2, String.valueOf(attribute.isAdmin()));
                //SQL文の3個目の?にadminを代入する
                pS.setString(3, String.valueOf(attribute.isFrozen()));

                return Optional.of(Collections.singletonList(pS));
            } catch (SQLException sqlex) {
                sqlex.printStackTrace();
                return Optional.empty();
            }
        });
    }

    /**
     * 凍結されたアカウントをすべて取得する
     *
     * @return [凍結されたアカウントのUUID]
     */
    @Override
    public List<String> getFrozenAccount() {
        return getUUIDs("frozen","true").orElse(new ArrayList<>());
    }

    /**
     * 管理者権限を持つアカウントをすべて取得する
     *
     * @return [管理者権限を持つアカウントのUUID]
     */
    @Override
    public List<String> getAdminAccount() {
        return getUUIDs("admin","true").orElse(new ArrayList<>());
    }
}

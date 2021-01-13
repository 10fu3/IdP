package net.den3.IdP.Store.Account;

import net.den3.IdP.Entity.Account.IPPID;
import net.den3.IdP.Entity.Account.PPIDBuilder;
import net.den3.IdP.Store.IDBAccess;
import net.den3.IdP.Store.InjectionStore;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * サービスごとにことなるアカウントUUIDを発行するためのストア
 */
public class PPIDStore implements IPPIDStore{

    private final IDBAccess store = (IDBAccess) InjectionStore.get().get("rdbms").orElseThrow(NullPointerException::new);
    private final String TABLE = "ppid_repository";

    private final List<String> fieldName = Arrays.asList("ppid","account_id","service_id");

    public PPIDStore(){
        store.controlSQL((con)->{
            try{
                return Optional.of(Collections.singletonList(
                        //テーブルがなかったら作る仕組み
                        con.prepareStatement(
                                //auth_flowテーブルを作る
                                "CREATE TABLE "+TABLE+"(" +
                                        "ppid VARCHAR(255) PRIMARY KEY," +
                                        "account_id VARCHAR(255)," +
                                        "service_id VARCHAR(255)" +
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
     * @return [PPIDエンティティ]
     */
    private List<IPPID> getPPIDs(String targetField, String targetValue){
        if(!fieldName.contains(targetField)){
            return new ArrayList<>();
        }
        return store.getLineBySQL(fieldName, (con) -> {
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
                .map(p-> new PPIDBuilder()
                       .setID(p.get("ppid"))
                       .setAccountID(p.get("account_id"))
                       .setServiceID(p.get("service_id"))
                       .build())
                .collect(Collectors.toList());
    }

    /**
     * 元のアカウントUUIDを返す
     *
     * @param ppid 仮名ID
     * @return アカウントUUID
     */
    @Override
    public Optional<IPPID> getPPID(String ppid) {
        return getPPIDs("ppid",ppid).stream().findAny();
    }

    /**
     * アカウントが持つ仮名ID(PPID)を返す
     *
     * @param accountUUID アカウントUUID
     * @return [仮名ID]
     */
    @Override
    public List<IPPID> getPPIDs(String accountUUID) {
        return getPPIDs("account_id",accountUUID);
    }

    /**
     * 仮名IDとアカウントUUIDを紐付けて保存する
     *
     * @param ppid 仮名IDエンティティ
     */
    @Override
    public void addPPID(IPPID ppid) {
        store.controlSQL((con)->{
            try {
                //INSET文の発行 uuid mail pass nick icon last_login_timeの順
                PreparedStatement pS = con.prepareStatement("INSERT INTO "+TABLE+" VALUES (?,?,?) ;");
                //SQL文の1個目の?にuuidを代入する
                pS.setString(1, ppid.getID());
                //SQL文の2個目の?にfrozenを代入する
                pS.setString(2, ppid.getAccountID());
                //SQL文の3個目の?にadminを代入する
                pS.setString(3, ppid.getServiceID());

                return Optional.of(Collections.singletonList(pS));
            } catch (SQLException sqlex) {
                sqlex.printStackTrace();
                return Optional.empty();
            }
        });
    }

    /**
     * 仮名IDがストアに保存されているかどうか
     *
     * @param ppid 仮名ID
     * @return true -> 含まれている false->含まれていない
     */
    @Override
    public boolean containsPPID(String ppid) {
        return getPPID(ppid).isPresent();
    }

    /**
     * 仮名IDをストアから削除する
     *
     * @param ppid 仮名ID
     */
    @Override
    public void removePPID(String ppid) {
        store.controlSQL((con)->{
            List<PreparedStatement> psL = new ArrayList<>();
            PreparedStatement statement;
            try {
                statement = con.prepareStatement("DELETE FROM "+TABLE+" WHERE ppid = ?;");
                statement.setString(1,ppid);
                psL.add(statement);
                return Optional.of(psL);
            }catch (SQLException ignore){
                return Optional.empty();
            }
        });
    }

    /**
     * 発行サービスIDと合致するPPIDをすべて削除する
     *
     * @param serviceID サービスID
     */
    @Override
    public void removePPIDbyServiceID(String serviceID) {
        store.controlSQL((con)->{
            List<PreparedStatement> psL = new ArrayList<>();
            PreparedStatement statement;
            try {
                statement = con.prepareStatement("DELETE FROM "+TABLE+" WHERE service_id = ?;");
                statement.setString(1,serviceID);
                psL.add(statement);
                return Optional.of(psL);
            }catch (SQLException ignore){
                return Optional.empty();
            }
        });
    }

    /**
     * アカウントUUIDと合致するPPIDをさべて削除する
     *
     * @param accountID
     */
    @Override
    public void removePPIDbyAccountID(String accountID) {
        store.controlSQL((con)->{
            List<PreparedStatement> psL = new ArrayList<>();
            PreparedStatement statement;
            try {
                statement = con.prepareStatement("DELETE FROM "+TABLE+" WHERE account_id = ?;");
                statement.setString(1,accountID);
                psL.add(statement);
                return Optional.of(psL);
            }catch (SQLException ignore){
                return Optional.empty();
            }
        });
    }
}

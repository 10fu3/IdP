package net.den3.IdP.Store.Auth;

import net.den3.IdP.Entity.Auth.AuthFlowBuilder;
import net.den3.IdP.Entity.Auth.CodeChallengeMethod;
import net.den3.IdP.Entity.Auth.IAuthFlow;
import net.den3.IdP.Store.IDBAccess;
import net.den3.IdP.Store.InjectionStore;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class LoginTokenStore implements ILoginTokenStore{

    private final Long DAY_UNIX_TIME = 24L*60L*60L;

    private final List<String> fieldName = Arrays.asList("uuid","account_id","expired_at");

    IDBAccess db = (IDBAccess) InjectionStore.get().get("rdbms").orElseThrow(NullPointerException::new);

    static ILoginTokenStore getInstance(){
        return (ILoginTokenStore) InjectionStore.get().get("login_token").orElseThrow(NullPointerException::new);
    }

    public LoginTokenStore(){
        //DBにauth_flowテーブルがなければ作成するSQL文を実行する
        db.controlSQL((con)-> {
            try {
                return Optional.of(Collections.singletonList(
                        //テーブルがなかったら作る仕組み
                        con.prepareStatement(
                                //auth_flowテーブルを作る
                                "CREATE TABLE IF NOT EXISTS login_token ("
                                        //
                                        +"uuid VARCHAR(256) PRIMARY KEY, "
                                        //ログインするアカウントのID
                                        +"account_id VARCHAR(256)"
                                        +"expired_at VARCHAR(256)"+
                                        ")")));
            } catch (SQLException e) {
                return Optional.empty();
            }
        });
    }

    /**
     * 登録されたトークンから紐づけられたアカウントUUIDを取得する
     *
     * @param token トークン
     * @return Optional[紐づけられたアカウントUUID]
     */
    @Override
    public Optional<String> getAccountUUID(String token) {
        Optional<List<Map<String, String>>> sql = db.getLineBySQL(fieldName, (con) -> {
            try {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM login_token WHERE uuid = ?");
                ps.setString(1, token);
                return Optional.of(ps);
            } catch (SQLException throwables) {}
            return Optional.empty();
        });
        return sql.orElse(new ArrayList<>())
                .stream()
                .findFirst()
                .filter(m->System.currentTimeMillis() > Long.parseLong(m.get("expired_at")))
                .map(m->m.get("account_id"));

    }

    /**
     * トークンの存在を確認する
     *
     * @param token トークン
     * @return true->存在する false->存在しない
     */
    @Override
    public boolean containsToken(String token) {
        return getAccountUUID(token).isPresent();
    }

    /**
     * トークンを登録する
     *
     * @param token トークン
     * @param uuid  アカウントに紐付けされたUUID
     */
    @Override
    public void putToken(String token, String uuid) {
        db.controlSQL((con)->{
            try {
                PreparedStatement ps = con.prepareStatement("INSERT INTO login_token VALUES (?,?) ;");
                ps.setString(1,token);
                ps.setString(2,uuid);
                return Optional.of(Collections.singletonList(ps));
            } catch (SQLException ex) {
                ex.printStackTrace();
                return Optional.empty();
            }
        });
    }

    /**
     * トークンを削除する
     *
     * @param uuid アカウントに紐付けされたUUID
     * @return true->成功 false->失敗
     */
    @Override
    public boolean deleteTokenByAccount(String uuid) {
        return db.controlSQL((con)->{
            try {
                PreparedStatement ps = con.prepareStatement("DELETE FROM login_token where account_id = ?");
                ps.setString(1,uuid);
                return Optional.of(Collections.singletonList(ps));
            } catch (SQLException ex) {
                ex.printStackTrace();
                return Optional.empty();
            }
        });
    }

    /**
     * トークンを削除する
     *
     * @param token ログイントークン
     * @return true->成功 false->失敗
     */
    @Override
    public boolean deleteToken(String token) {
        return db.controlSQL((con)->{
            try {
                PreparedStatement ps = con.prepareStatement("DELETE FROM login_token where uuid = ?");
                ps.setString(1,token);
                return Optional.of(Collections.singletonList(ps));
            } catch (SQLException ex) {
                ex.printStackTrace();
                return Optional.empty();
            }
        });
    }
}

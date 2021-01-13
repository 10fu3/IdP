package net.den3.IdP.Store.Auth;

import net.den3.IdP.Entity.Auth.*;
import net.den3.IdP.Store.IDBAccess;
import net.den3.IdP.Store.InjectionStore;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class AccessTokenStore implements IAccessTokenStore {

    IDBAccess db = (IDBAccess) InjectionStore.get().get("rdbms").orElseThrow(NullPointerException::new);

    private final List<String> fieldName =
            Arrays.asList("uuid","access_token","refresh_token","account_id","service_id","scope","token_lifetime","refresh_lifetime","nonce");

    public AccessTokenStore(){
        //DBにauth_flowテーブルがなければ作成するSQL文を実行する
        db.controlSQL((con)->{
            try{
                return Optional.of(Collections.singletonList(
                        //テーブルがなかったら作る仕組み
                        con.prepareStatement(
                                //auth_flowテーブルを作る
                                "CREATE TABLE IF NOT EXISTS access_token_store ("
                                        //内部ID
                                        +"uuid VARCHAR(256) PRIMARY KEY,"
                                        +"access_token VARCHAR(256),"
                                        +"refresh_token VARCHAR(256),"
                                        +"account_id VARCHAR(256),"
                                        +"service_id VARCHAR(256),"
                                        +"scope VARCHAR(256),"
                                        +"token_lifetime VARCHAR(256),"
                                        +"refresh_lifetime VARCHAR(256),"
                                        +"nonce VARCHAR(256)"
                                        +")")));
            }catch (SQLException ex){
                return Optional.empty();
            }
        });
    }

    /**
     * 指定した列名から値の一致する行をすべて返す
     * @param targetField 列名
     * @param targetValue 値
     * @return 認可フローエンティティのリスト
     */
    private Optional<List<IAccessToken>> getAccessToken(String targetField, String targetValue){
        if(!fieldName.contains(targetField)){
            return Optional.empty();
        }
        return db.getLineBySQL(fieldName, (con) -> {
            try {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM access_token_store WHERE "+targetField+" = ?");;
                ps.setString(1, targetValue);
                return Optional.of(ps);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return Optional.empty();
            }
        }).map(p -> p.stream().map(line -> {
                    AccessTokenBuilder builder = AccessTokenBuilder
                            .New()
                            .setUUID(line.get("uuid"))
                            .setAccessToken(line.get("access_token"))
                            .setRefreshToken(line.get("refresh_token"))
                            .setAccountID(line.get("account_id"))
                            .setClientID(line.get("service_id"))
                            .setScope(line.get("scope"))
                            .setLifeTimeAccessToken(Long.parseLong(line.get("token_lifetime")))
                            .setLifeTimeRefreshToken(Long.parseLong(line.get("refresh_lifetime")))
                            .setNonce(line.get("nonce").isEmpty() ? Optional.empty() : Optional.of(line.get("nonce")));
                    return builder.build();
                }
        ).collect(Collectors.toList()));
    }

    /**
     * アクセストークンエンティティをUUIDから取得する
     *
     * @param uuid アカウント固有の内部ID
     * @return アクセストークンエンティティ
     */
    @Override
    public List<IAccessToken> getTokenByAccountUUID(String uuid) {
        return getAccessToken("account_id",uuid).orElse(new ArrayList<>());
    }

    /**
     * アクセストークンエンティティをUUIDから取得する
     *
     * @param id 内部ID
     * @return アクセストークンエンティティ
     */
    @Override
    public Optional<IAccessToken> getTokenByID(String id) {
        return getAccessToken("uuid",id).map(p -> p.stream().findAny()).flatMap(p -> p);
    }

    /**
     * アクセストークンエンティティをリフレッシュトークンから取得する
     *
     * @param refreshToken リフレッシュトークン
     * @return アクセストークンエンティティ
     */
    @Override
    public Optional<IAccessToken> getTokenByRefresh(String refreshToken) {
        return getAccessToken("refresh_token",refreshToken).map(p -> p.stream().findAny()).flatMap(p -> p);
    }

    /**
     * アクセストークンエンティティをトークンから取得する
     *
     * @param token トークン
     * @return アクセストークンエンティティ
     */
    @Override
    public Optional<IAccessToken> getTokenByToken(String token) {
        return getAccessToken("access_token",token).map(p -> p.stream().findAny()).flatMap(p -> p);
    }

    /**
     * 特定の内部IDを持つアクセストークンエンティティをIdPから削除する
     *
     * @param id 内部ID
     */
    @Override
    public void deleteTokenByID(String id) {
        db.controlSQL((con)->{
            try {
                PreparedStatement ps = con.prepareStatement("DELETE FROM access_token_store where uuid = ?");
                ps.setString(1,id);
                return Optional.of(Collections.singletonList(ps));
            } catch (SQLException ex) {
                ex.printStackTrace();
                return Optional.empty();
            }
        });
    }

    @Override
    public void deleteTokenByAccountUUID(String uuid) {
        db.controlSQL((con)->{
            try {
                PreparedStatement ps = con.prepareStatement("DELETE FROM access_token_store where account_id = ?");
                ps.setString(1,uuid);
                return Optional.of(Collections.singletonList(ps));
            } catch (SQLException ex) {
                ex.printStackTrace();
                return Optional.empty();
            }
        });
    }

    /**
     * 保存されているアクセストークンを更新する
     *
     * @param token 更新後のアクセストークンエンティティ
     */
    @Override
    public void updateToken(IAccessToken token) {
        db.controlSQL((con)->{
            try{
                PreparedStatement ps = con.prepareStatement(
                        "UPDATE access_token_store SET "+
                        "access_token = ?,"+
                        "refresh_token = ?,"+
                        "account_id = ?,"+
                        "service_id = ?,"+
                        "scope = ?,"+
                        "token_lifetime = ?,"+
                        "refresh_lifetime = ?, "+
                        "nonce = ? "
                        +"where uuid=?"
                        +");");
                ps.setString(1,token.getAccessToken());
                ps.setString(2,token.getRefreshToken());
                ps.setString(3,token.getAccountID());
                ps.setString(4,token.getClientID());
                ps.setString(5,token.getScope());
                ps.setString(6,String.valueOf(token.getLifeTimeAccessToken()));
                ps.setString(7,String.valueOf(token.getLifeTimeRefreshToken()));
                ps.setString(8,token.getNonce().orElse(""));
                ps.setString(9,token.getUUID());
                return Optional.of(Collections.singletonList(ps));
            }catch (SQLException ex){
                ex.printStackTrace();
                return Optional.empty();
            }
        });
    }

    /**
     * アクセストークンを保存する
     *
     * @param token アクセストークンエンティティ
     * @return 成功->true 失敗->false
     */
    @Override
    public boolean addToken(IAccessToken token) {
        return db.controlSQL((con)->{
            try {
                PreparedStatement ps = con.prepareStatement("INSERT INTO access_token_store VALUES (?,?,?,?,?,?,?,?,?) ;");
                ps.setString(1,token.getUUID());
                ps.setString(2,token.getAccessToken());
                ps.setString(3,token.getRefreshToken());
                ps.setString(4,token.getAccountID());
                ps.setString(5,token.getClientID());
                ps.setString(6,token.getScope());
                ps.setString(7,String.valueOf(token.getLifeTimeAccessToken()));
                ps.setString(8,String.valueOf(token.getLifeTimeRefreshToken()));
                ps.setString(9,token.getNonce().orElse(""));
                return Optional.of(Collections.singletonList(ps));
            } catch (SQLException ex) {
                ex.printStackTrace();
                return Optional.empty();
            }
        });
    }
}

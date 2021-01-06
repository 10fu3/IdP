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

public class AuthFlowStore implements IAuthFlowStore{

    IDBAccess db = (IDBAccess) InjectionStore.get().get("rdbms").orElseThrow(NullPointerException::new);

    private final List<String> fieldName =
            Arrays.asList("uuid","authorization_code","account_id","client_id","access_token_uuid","code_challenge_method","code_challenge","lifetime");

    public AuthFlowStore(){
        //DBにauth_flowテーブルがなければ作成するSQL文を実行する
        db.controlSQL((con)->{
            try{
                return Optional.of(Collections.singletonList(
                        //テーブルがなかったら作る仕組み
                        con.prepareStatement(
                                //auth_flowテーブルを作る
                                "CREATE TABLE IF NOT EXISTS auth_flow ("
                                //認可フローエンティティ固有のID
                                +"uuid VARCHAR(256) PRIMARY KEY, "
                                //認可コード
                                +"authorization_code VARCHAR(256), "
                                //認可するアカウントのID
                                +"account_id VARCHAR(256), "
                                //認可されるサービスのID
                                +"client_id VARCHAR(256), "
                                //認可前なのか認可後なのか
                                +"access_token_uuid VARCHAR(256), "
                                //コードチャレンジの種類
                                +"code_challenge_method VARCHAR(256), "
                                //コードチャレンジ
                                +"code_challenge VARCHAR(256), "
                                //有効期限
                                +"lifetime VARCHAR(256) "
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
    private Optional<List<IAuthFlow>> getAuthFlow(String targetField,String targetValue){
        if(!fieldName.contains(targetField)){
            return Optional.empty();
        }
        return db.getLineBySQL(fieldName, (con) -> {
            try {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM auth_flow WHERE ? = ?");
                ps.setString(1, targetField);
                ps.setString(2, targetValue);
                return Optional.of(ps);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return Optional.empty();
        }).map(p -> p.stream().map(line -> {
                    AuthFlowBuilder builder = AuthFlowBuilder
                            .create()
                            .setUUID(line.get("uuid"))
                            .setAccountID(line.get("account_id"))
                            .setAuthorizationCode("authorization_code")
                            .setClientID(line.get("client_id"))
                            .setAccessToken(line.get("access_token_uuid"))
                            .setCodeChallengeMethod(CodeChallengeMethod.of(line.get("code_challenge_method")))
                            .setLifeTime(Long.parseLong(line.get("lifetime")));
                    if(!line.get("code_challenge_method").isEmpty()){
                        builder.setCodeChallenge(line.get("code_challenge"));
                    }
                    return builder.build();
                }
        ).collect(Collectors.toList()));
    }

    /**
     * 認可フロー固有のIDから認可フローエンティティを取得する
     * @param id 認可フロー固有のID
     * @return 認可フローエンティティ
     */
    @Override
    public Optional<IAuthFlow> getAuthFlowByID(String id) {
         return getAuthFlow("uuid", id).map(p -> p.stream().findAny()).flatMap(p -> p);
    }

    /**
     * アクセストークンから認可フローエンティティを取得する
     * @param token アクセストークン
     * @return 認可フローエンティティ
     */
    @Override
    public Optional<IAuthFlow> getAuthFlowByAuthorizationCode(String token) {
        return getAuthFlow("authorization_code", token).map(p -> p.stream().findAny()).flatMap(p -> p);
    }

    /**
     * リフレッシュトークンから認可フローエンティティを取得する
     * @param token リフレッシュトークン
     * @return 認可フローエンティティ
     */
    @Override
    public Optional<IAuthFlow> getAuthFlowByAccessToken(String token) {
        return getAuthFlow("access_token", token).map(p -> p.stream().findAny()).flatMap(p -> p);
    }

    /**
     * 認可コードから認可フローエンティティを取得する
     * @param token 認可コード
     * @return 認可フローエンティティ
     */
    @Override
    public Optional<IAuthFlow> getAuthFlowByRefreshToken(String token) {
        return getAuthFlow("refresh_token", token).map(p -> p.stream().findAny()).flatMap(p -> p);
    }

    /**
     * DBを更新する
     * @param flow 認可フローエンティティ
     */
    @Override
    public void updateAuthFlow(IAuthFlow flow) {
        db.controlSQL((con)->{
            try{
                PreparedStatement ps = con.prepareStatement(
                    "UPDATE auth_flow SET "
                    //認可コード 1
                    +"authorization_code=?,"
                    //認可するアカウントのID 2
                    +"account_id=?,"
                    //認可されるサービスのID 4
                    +"client_id=?,"
                    //アクセストークンエンティティの内部ID 5
                    +"access_token_uuid=?, "
                    //コードチャレンジの種類 6
                    +"code_challenge_method=?, "
                    //コードチャレンジ 7
                    +"code_challenge=?, "
                    //有効期限 8
                    +"lifetime=? "
                    //認可フローIDを指定してアップデートする 9
                    +"where uuid=?"
                    +");");
                ps.setString(1,flow.getAuthorizationCode());
                ps.setString(2,flow.getAccountID());
                ps.setString(3,flow.getClientID());
                ps.setString(4,flow.getAccessToken());
                ps.setString(5,flow.getCodeChallengeMethod().orElse(CodeChallengeMethod.NONE).data);
                ps.setString(6,flow.getCodeChallenge().orElse(""));
                ps.setString(7,String.valueOf(flow.getLifeTime()));
                ps.setString(8,flow.getUUID());
                return Optional.of(Collections.singletonList(ps));
            }catch (SQLException ex){
                ex.printStackTrace();
                return Optional.empty();
            }
        });
    }
    /**
     * DBから削除する
     * @param id 認可フローエンティティ固有ID
     */
    @Override
    public void deleteAuthFlow(String id) {
        db.controlSQL((con)->{
            try {
                PreparedStatement ps = con.prepareStatement("DELETE FROM auth_flow where = ?");
                ps.setString(1,id);
                return Optional.of(Collections.singletonList(ps));
            } catch (SQLException ex) {
                ex.printStackTrace();
                return Optional.empty();
            }
        });
    }

    /**
     * DBに追加する
     * @param flow 認可フローエンティティ
     */
    @Override
    public void addAuthFlow(IAuthFlow flow) {
        db.controlSQL((con)->{
            try {
                PreparedStatement ps = con.prepareStatement("INSERT INTO auth_flow VALUES (?,?,?,?,?,?,?,?) ;");
                ps.setString(1,flow.getUUID());
                ps.setString(2,flow.getAuthorizationCode());
                ps.setString(3,flow.getAccountID());
                ps.setString(4,flow.getClientID());
                ps.setString(5,flow.getAccessToken());
                ps.setString(6,flow.getCodeChallengeMethod().orElse(CodeChallengeMethod.NONE).data);
                ps.setString(7,flow.getCodeChallenge().orElse(""));
                ps.setString(8,String.valueOf(flow.getLifeTime()));
                return Optional.of(Collections.singletonList(ps));
            } catch (SQLException ex) {
                ex.printStackTrace();
                return Optional.empty();
            }
        });
    }
}

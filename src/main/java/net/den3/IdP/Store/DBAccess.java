package net.den3.IdP.Store;

import com.zaxxer.hikari.HikariDataSource;
import net.den3.IdP.Config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

public class DBAccess implements IDBAccess{

    private HikariDataSource hikari;

    public DBAccess(){
        setupHikariCP();
    }

    /**
     * コネクションプールの設定
     */
    private void setupHikariCP(){
        //https://jyn.jp/java-hikaricp-mysql-sqlite/

        HikariDataSource ds = new HikariDataSource();
        System.out.println("DB_URL "+Config.get().getDBURL());
        ds.setJdbcUrl(Config.get().getDBURL());
        ds.setUsername(Config.get().getDBAccountName());
        ds.setPassword(Config.get().getDBAccountPassword());
        hikari = ds;
    }

    /**
     * SQLの接続を解放する 終了時必須処理
     */
    public void closeDB(){
        if(hikari != null){
            hikari.close();
        }
    }


    /**
     * 発行したSQLに合致する行をリストで返す
     * SQLに登録したデータはすべて文字列であるという前提なので数字を含むテーブルの場合は使わないこと
     * @param columns テーブルに存在するすべての列名のリスト
     * @param statement Connectionを引数に持ち戻り値がPreparedStatement>のラムダ式/クロージャ
     * @return Optional<List<Map<列名,値>>>
     */
    @Override
    public Optional<List<Map<String,String>>> getLineBySQL(List<String> columns, Function<Connection, Optional<PreparedStatement>> statement) {

        //結果格納用リスト
        List<Map<String,String>> resultList = new ArrayList<>();
        //結果そのものを表す
        Optional<List<Map<String,String>>> returnResult;

        //データベースに接続
        try(Connection con = hikari.getConnection()){
            //ラムダ式内で作られたSQL文を発行して結果を得る
            Optional<PreparedStatement> sqlGenerateResult = statement.apply(con);
            if(!sqlGenerateResult.isPresent()){
                return Optional.empty();
            }
            try (ResultSet sqlResult = sqlGenerateResult.get().executeQuery()){
                Map<String,String> keyValue;
                //読み込まれていない結果が複数ある限りWhileの中が実行される
                while (sqlResult.next()){
                    keyValue = new HashMap<>();
                    for(String name:columns){
                        keyValue.put(name,sqlResult.getString(name));
                    }
                    if(keyValue.size() > 0){
                        resultList.add(keyValue);
                    }
                }
            }
            if(resultList.size() == 0){
                return Optional.empty();
            }
        }catch (SQLException ex){
            //SQL文の発行に失敗すると実行される
            ex.printStackTrace();
            return Optional.empty();
        }
        //正常にSQLが発行されたことを保証し
        //値も取得できる
        returnResult = Optional.of(resultList);
        return  returnResult;
    }



    /**
     * 発行したSQLを使ってデータベースを更新する
     * @param mission Connectionを引数に持ち戻り値がList<PreparedStatement>>のラムダ式/クロージャ
     * @return boolean クロージャのSQLの結果 true→成功 false→失敗
     */
    public boolean controlSQL(Function<Connection,Optional<List<PreparedStatement>>> mission){
        try(Connection con = hikari.getConnection()){
            if(mission.apply(con).isPresent()){
                con.setAutoCommit(false);
                for (int i = 0; i < mission.apply(con).get().size(); i++) {
                    mission.apply(con).get().get(i).executeUpdate();
                }
                con.commit();
                return true;

            }else{
                return false;
            }
        }catch (SQLException ex){
            ex.printStackTrace();
            return false;
        }
    }
}

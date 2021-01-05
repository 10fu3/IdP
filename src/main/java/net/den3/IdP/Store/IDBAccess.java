package net.den3.IdP.Store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public interface IDBAccess {
    /**
     * DBとのコネクションを閉じる
     */
    void closeDB();
    /**
     * 発行したSQLに合致する行をリストで返す
     * SQLに登録したデータはすべて文字列であるという前提なので数字を含むテーブルの場合は使わないこと
     * @param statement Connectionを引数に持ち戻り値がPreparedStatement>のラムダ式/クロージャ
     * @return Optional<List<Map<列名,値>>>
     */
    Optional<List<Map<String,String>>> getLineBySQL(List<String> columns, Function<Connection, Optional<PreparedStatement>> statement);
    /**
     * 発行したSQLを使ってデータベースを更新する
     * @param mission Connectionを引数に持ち戻り値がPreparedStatement>のラムダ式/クロージャ
     * @return boolean クロージャのSQLの結果 true→成功 false→失敗
     */
    boolean controlSQL(Function<Connection, Optional<List<PreparedStatement>>> mission);
}

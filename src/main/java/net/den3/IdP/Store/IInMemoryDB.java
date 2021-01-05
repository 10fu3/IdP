package net.den3.IdP.Store;

import java.util.Map;
import java.util.Optional;

public interface IInMemoryDB {

    /**
     * インメモリデータベースとの接続を解放する
     */
    void closeDB();

    /**
     * インメモリデータベース(key:value形式)から値を得る
     * @param prefix 識別子
     * @param key キー
     * @return 保存した値
     */
    Optional<String> getValue(String prefix, String key);

    /**
     * インメモリデータベース(key:value形式)に値を保存する
     * @param prefix 識別子
     * @param key キー
     * @param value 保存した値
     */
    void putValue(String prefix, String key, String value);

    /**
     * 時間指定で消滅するKey Value
     * @param key キー
     * @param value 値
     * @param seconds 登録してから消滅するまでの時間(秒)
     */
    void putTimeValue(String prefix, String key, String value, int seconds);

    /**
     * キーの存在確認
     * @param key キー
     * @return true → 存在する /  false → 存在しない
     */
    boolean containsKey(String prefix, String key);

    /**
     * 指定した値を持つキーを返す
     * @param value 値
     * @return キー
     */
    Optional<String> searchKey(String prefix, String value);

    /**
     * 指定したキーを削除する
     * @param key キー
     * @return true->成功 false->失敗
     */
    boolean delete(String prefix, String key);

    /**
     * 登録されたすべてのキーと値をリストにして返す
     *
     * @param prefix 識別子
     * @return List<Map < key:String, 値:String>>
     */
    Map<String, String> getPairs(String prefix);

}

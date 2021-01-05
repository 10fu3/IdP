package net.den3.IdP.Store;

import net.den3.IdP.Config;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class InMemoryRedis implements IInMemoryDB{
    private final JedisPool pool;

    public InMemoryRedis(){
        pool = new JedisPool(new JedisPoolConfig(), Config.get().getRedisURL(),6379);
    }


    /**
     * インメモリデータベースとの接続を解放する
     */
    @Override
    public void closeDB(){
        pool.close();
    }

    /**
     * インメモリデータベースとのやりとりを行う
     * @param doSomething 引数Jedis型のラムダ式/クロージャ
     */
    private void doIt(Consumer<Jedis> doSomething){
        try(Jedis jedis = pool.getResource()){
            doSomething.accept(jedis);
        }
    }



    /**
     * インメモリデータベース(key:value形式)から値を得る
     * @param key キー
     * @return 保存した値
     */
    @Override
    public Optional<String> getValue(String prefix,String key) {
        final Optional<String>[] a = new Optional[1];
        if(!containsKey(prefix,key)){
            return Optional.empty();
        }
        doIt((r)-> a[0] = Optional.of(r.get(prefix+key).replaceFirst(prefix,"")));
        return a[0];
    }

    /**
     * インメモリデータベース(key:value形式)に値を保存する
     * @param key キー
     * @param value 保存した値
     */
    @Override
    public void putValue(String prefix,String key, String value) {
        doIt((r)-> r.set(prefix+key,prefix+value));
    }

    /**
     * 時間指定で消滅するKey Value
     *
     * @param key     キー
     * @param value   値
     * @param seconds 登録してから消滅するまでの時間(秒)
     */
    @Override
    public void putTimeValue(String prefix,String key, String value, int seconds) {
        doIt((r)->{
            r.set(prefix+key,prefix+value);
            r.expire(prefix+key,seconds);
        });
    }

    /**
     * キーの存在確認
     *
     * @param key キー
     * @return true → 存在する /  false → 存在しない
     */
    @Override
    public boolean containsKey(String prefix,String key) {
        AtomicReference<Boolean> flag = new AtomicReference<>();
        doIt((r)-> flag.set(r.exists(prefix+key)));
        return flag.get();
    }


    /**
     * 指定したキーを削除する
     *
     * @param key キー
     * @return true->成功 false->失敗
     */
    @Override
    public boolean delete(String prefix,String key) {
        if(!containsKey(prefix,key)){
            return false;
        }
        doIt((r)-> r.del(prefix+key));
        return true;
    }

    /**
     * 指定した値を持つキーを返す
     * @param value 値
     * @return キー
     */
    @Override
    public Optional<String> searchKey(String prefix,String value) {
        if(value == null || value.length() == 0){
            return Optional.empty();
        }
        final String[] temp = new String[]{null};
        doIt((r)->{
            for (String val : r.keys("*")) {
                if(val.equalsIgnoreCase(prefix+value)){
                    temp[0] = value.replaceFirst(prefix,"");
                    break;
                }
            }
        });
        return Optional.ofNullable(temp[0]);
    }

    /**
     * 登録されたすべてのキーと値を返す
     * @param prefix 追加時に付加された接頭詞
     * @return Map<key:String,value:String>
     */
    @Override
    public Map<String, String> getPairs(String prefix) {
        final Map<String, String> result = new HashMap<>();
        doIt((r)-> r.keys("*").forEach(k->{
            if(k.contains(prefix)){
                result.put(k.replaceFirst(prefix,""),r.get(k));
            }
        }));
        return result;
    }
}

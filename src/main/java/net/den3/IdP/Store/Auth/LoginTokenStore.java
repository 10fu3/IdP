package net.den3.IdP.Store.Auth;

import net.den3.IdP.Store.IInMemoryDB;
import net.den3.IdP.Store.InjectionStore;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class LoginTokenStore implements ILoginTokenStore {
    private final IInMemoryDB store = (IInMemoryDB)InjectionStore.get().get("memory_db").orElseThrow(NullPointerException::new);

    //一か月を秒数で定義
    private final static Integer MONTH = 60*60*24*30;

    private static final String PREFIX = "ACCOUNT_TOKEN: ";

    /**
     * アカウントのUUIDから登録されたトークンを取得する
     *
     * @param accountUUID アカウントに紐付けされたUUID
     * @return Optional String->トークン empty->存在しない
     */
    @Override
    public List<String> getTokens(String accountUUID) {
        Map<String, String> stores = store.getPairs(PREFIX);
        return stores.keySet().stream().filter(k->accountUUID.equalsIgnoreCase(stores.get(k))).map(stores::get).collect(Collectors.toList());
    }

    /**
     * 登録されたトークンから紐づけられたアカウントUUIDを取得する
     *
     * @param token トークン
     * @return Optional[紐づけられたアカウントUUID]
     */
    @Override
    public Optional<String> getAccountUUID(String token) {
        return store.getValue(PREFIX,token);
    }

    /**
     * トークンの登録を確認する
     * @param token アカウントに紐付けされたUUID
     * @return true->存在する false->存在しない
     */
    @Override
    public boolean containsToken(String token) {
        return store.containsKey(PREFIX,token);
    }

    /**
     * トークンを登録する 一か月で削除される
     *
     * @param uuid  アカウントに紐付けされたUUID
     * @param token トークン
     */
    @Override
    public void putToken(String token, String uuid) {
        store.putTimeValue(PREFIX,token,uuid,MONTH);
    }

    /**
     * トークンを削除する
     *
     * @param uuid アカウントに紐付けされたUUID
     * @return true->成功 false->失敗
     */
    @Override
    public boolean deleteTokenByAccount(String uuid) {
        return store.delete(PREFIX,uuid);
    }

    /**
     * トークンを削除する
     * @param token　ログイントークン
     * @return true->成功 false->失敗
     */
    @Override
    public boolean deleteToken(String token) {
        Optional<String> key = store.searchKey(PREFIX,token);
        if (!key.isPresent()){
            return false;
        }else{
            deleteTokenByAccount(key.get());
            return true;
        }
    }

    /**
     * 登録されたアカウントのUUIDとトークンを返す
     *
     * @return List<Map < アカウントのUUID:String, トークン:String>>
     */
    @Override
    public Map<String, String> getAllTokens() {
        return store.getPairs(PREFIX);
    }

    /**
     * アカウントに紐づけられたトークンを更新する
     * @param token アカウントに紐付けされたUUID
     * @return 更新後のトークン
     */
    @Override
    public Optional<String> updateToken(String token) {
        if(!containsToken(token)){
            return Optional.empty();
        }
        Optional<String> uuid = getAccountUUID(token);
        deleteToken(token);
        String generatedUUID = UUID.randomUUID().toString();
        putToken(generatedUUID,uuid.orElse(""));
        return Optional.of(generatedUUID);
    }
}

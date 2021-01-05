package net.den3.IdP.Store.Service;

import net.den3.IdP.Entity.Service.IService;
import net.den3.IdP.Store.InjectionStore;

import java.util.List;
import java.util.Optional;

public interface IServiceStore {

    static IServiceStore getInstance() {
        return (IServiceStore) InjectionStore.get().get("service").orElseThrow(NullPointerException::new);
    }

    /**
     * 登録された外部連携サービスをすべてDBから取得する
     * @return サービスのリスト
     */
    Optional<List<IService>> getServices();

    /**
     * 登録された外部連携サービスのうちサービス固有のIDが合致するものを返す
     * @param id 外部連携サービスのID
     * @return 外部連携サービスエンティティ
     */
    Optional<IService> getService(String id);

    /**
     *　サービスの情報をアップデートする
     * @param service サービスエンティティ
     * @return 成功->true 失敗->false
     */
    boolean updateService(IService service);

    /**
     * サービスを削除する
     * @param id サービス固有のUUID
     * @return 成功->true 失敗->false
     */
    boolean deleteService(String id);

    /**
     * 外部連携サービスを登録する
     * @return 成功->true 失敗->false
     */
    boolean addService(IService service);

    /**
     * 同じ管理者IDを持つ外部連携サービスをすべてDBから取得する
     * @return サービスのリスト
     */
    Optional<List<IService>> getServices(String adminID);


}

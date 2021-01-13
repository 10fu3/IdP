package net.den3.IdP.Store.Upload;

import net.den3.IdP.Entity.Upload.IUploadEntity;
import net.den3.IdP.Store.InjectionStore;

import java.util.List;
import java.util.Optional;

public interface IUploaderStore {

    static IUploaderStore getInstance() {
        return (IUploaderStore) InjectionStore.get().get("upload_request").orElseThrow(NullPointerException::new);
    }

    /**
     * 登録されたアップロードリクエストをすべてDBから取得する
     * @return アップロードリクエストのリスト
     */
    Optional<List<IUploadEntity>> getUploadEntity();

    /**
     * 登録されたアップロードリクエストのうちアップロードリクエスト固有のIDが合致するものを返す
     * @param id アップロードリクエストのID
     * @return アップロードリクエストエンティティ
     */
    Optional<IUploadEntity> getUploadEntity(String id);

    /**
     *　アップロードリクエストの情報をアップデートする
     * @param UploadEntity アップロードリクエストエンティティ
     * @return 成功->true 失敗->false
     */
    boolean updateUploadEntity(IUploadEntity UploadEntity);

    /**
     * アップロードリクエストを削除する
     * @param id アップロードリクエスト固有のUUID
     * @return 成功->true 失敗->false
     */
    boolean deleteUploadEntity(String id);

    /**
     * アップロードリクエストを登録する
     * @return 成功->true 失敗->false
     */
    boolean addUploadEntity(IUploadEntity UploadEntity);
}

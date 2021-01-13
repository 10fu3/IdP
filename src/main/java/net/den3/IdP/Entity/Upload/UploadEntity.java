package net.den3.IdP.Entity.Upload;

import java.util.UUID;

/**
 * アップロードリクエストを管理するエンティティ
 */
class UploadEntity implements IUploadEntity{
    String uuid = UUID.randomUUID().toString();
    String ip;
    String uploaderURL;
    String deleteKey;


    /**
     * アップロードリクエストエンティティ固有のUUID
     *
     * @return
     */
    @Override
    public String getUUID() {
        return this.uuid;
    }

    /**
     *  アップロードリクエスト送信者のIPアドレス
     * @return IPアドレス
     */
    public String getRequestIP() {
        return ip;
    }

    /**
     * アップローダーから返されたURL
     * @return URL
     */
    public String getUploaderURL() {
        return uploaderURL;
    }

    /**
     * アップローダーから返された削除キー
     * @return 削除キー
     */
    public String getDeleteKey() {
        return deleteKey;
    }
}

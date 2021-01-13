package net.den3.IdP.Entity.Upload;

/**
 * アップロードリクエストを管理するエンティティ
 */

public interface IUploadEntity {

    /**
     * アップロードリクエストエンティティ固有のUUID
     * @return
     */
    String getUUID();

    /**
     *  アップロードリクエスト送信者のIPアドレス
     * @return IPアドレス
     */
    String getRequestIP();

    /**
     * アップローダーから返されたURL
     * @return URL
     */
    String getUploaderURL();

    /**
     * アップローダーから返された削除キー
     * @return 削除キー
     */
    String getDeleteKey();
}

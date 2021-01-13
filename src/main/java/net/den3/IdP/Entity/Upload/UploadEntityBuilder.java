package net.den3.IdP.Entity.Upload;

public class UploadEntityBuilder {
    private UploadEntity ue = new UploadEntity();

    /**
     * エンティティ固有のIDを設定する
     * @param uuid アップロードリクエストエンティティ
     * @return ビルダー
     */
    public UploadEntityBuilder setUUID(String uuid){
        this.ue.uuid = uuid;
        return this;
    }

    /**
     * アップロードリクエスト送信者のIPアドレスをビルダーにセットする
     * @param ip IPアドレス
     * @return ビルダー
     */
    public UploadEntityBuilder setRequestIp(String ip) {
        this.ue.ip = ip;
        return this;
    }

    /**
     * アップローダーから返されたURLをビルダーにセットする
     * @param uploaderURL アップローダーから返されたURL
     * @return ビルダー
     */
    public UploadEntityBuilder setUploaderURL(String uploaderURL) {
        this.ue.uploaderURL = uploaderURL;
        return this;
    }

    /**
     * アップローダーから返された削除キーをビルダーにセットする
     * @param deleteKey
     * @return ビルダー
     */
    public UploadEntityBuilder setDeleteKey(String deleteKey) {
        this.ue.deleteKey = deleteKey;
        return this;
    }

    /**
     * アップロードエンティティを生成する
     * @return アップロードエンティティ
     */
    public IUploadEntity build(){
        return ue;
    }
}

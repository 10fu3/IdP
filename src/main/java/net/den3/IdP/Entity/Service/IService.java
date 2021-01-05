package net.den3.IdP.Entity.Service;

import java.util.List;
import java.util.Map;

public interface IService {
    static Service create(){
        return new Service();
    }

    /**
     * シークレットIDを返す
     * @return シークレットID
     */
    String getSecretID();

    /**
     * シークレットIDを更新
     * @return 更新後のシークレットID
     */
    String updateSecretID();

    /**
     * 外部連携サービスのIDを返すメソッド
     * @return 外部連携サービスのID
     */
    String getServiceID();

    /**
     * 外部連携サービスの名前を返すメソッド
     * @return 外部連携サービスの名前
     */
    String getServiceName();

    /**
     * 外部連携サービスの管理者IDを返すメソッド AccountEntityで使うIDと同一
     * @return 管理者ID
     */
    String getAdminID();

    /**
     * 認証後にリダイレクトするURLを取得するメソッド
     * @return 認証後にリダイレクトするURL
     */
    String getRedirectURL();

    /**
     * 外部連携サービスのアイコン(画像)のURLを返すメソッド
     * @return 外部連携サービスのアイコン(画像)のURL
     */
    String getServiceIconURL();

    /**
     * 外部連携サービスの説明文を返すメソッド
     * @return 外部連携サービスの説明文
     */
    String getServiceDescription();

    /**
     * 外部連携アプリの使用する権限をリストで返すメソッド
     * @return 使用する権限のリスト
     */
    List<ServicePermission> getUsedPermission();
}

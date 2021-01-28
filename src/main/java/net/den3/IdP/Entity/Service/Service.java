package net.den3.IdP.Entity.Service;



import net.den3.IdP.Util.LongUUID;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class Service implements IService {
    String ServiceID = UUID.randomUUID().toString();
    String ServiceSecret = LongUUID.generate();
    String AdminID = "";
    String ServiceName = "";
    String RedirectURL = "";
    String ServiceIconURL = "";
    String ServiceDescription = "";
    List<ServicePermission> UsedPermission = new ArrayList<>();

    /**
     * シークレットIDを返す
     * @return シークレットID
     */
    @Override
    public String getSecretID() {
        return this.ServiceSecret;
    }

    /**
     * 外部連携サービスのIDを返すメソッド
     * @return 外部連携サービスのID
     */
    @Override
    public String getServiceID() {
        return ServiceID;
    }


    /**
     * 外部連携サービスの管理者IDを返すメソッド AccountEntityで使うIDと同一
     * @return 管理者ID
     */
    @Override
    public String getAdminID() {
        return AdminID;
    }


    /**
     * 外部連携サービスの名前を返すメソッド
     * @return 外部連携サービスの名前
     */
    @Override
    public String getServiceName() {
        return ServiceName;
    }

    /**
     * 認証後にリダイレクトするURLを取得するメソッド
     * @return 認証後にリダイレクトするURL
     */
    @Override
    public String getRedirectURL() {
        return RedirectURL;
    }

    /**
     * 外部連携サービスのアイコン(画像)のURLを返すメソッド
     * @return 外部連携サービスのアイコン(画像)のURL
     */
    @Override
    public String getServiceIconURL() {
        return ServiceIconURL;
    }

    /**
     * 外部連携サービスの説明文を返すメソッド
     * @return 外部連携サービスの説明文
     */
    @Override
    public String getServiceDescription() {
        return ServiceDescription;
    }

    /**
     * 外部連携サービスの使用する権限をリストで返すメソッド
     * @return 使用する権限のリスト
     */
    @Override
    public List<ServicePermission> getUsedPermission() {
        return UsedPermission;
    }
}

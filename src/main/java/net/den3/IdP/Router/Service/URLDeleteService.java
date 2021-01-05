package net.den3.IdP.Router.Service;

import net.den3.IdP.Entity.Service.IService;
import net.den3.IdP.Store.Auth.ILoginTokenStore;
import net.den3.IdP.Store.Service.IServiceStore;
import net.den3.IdP.Util.ParseJSON;
import net.den3.IdP.Util.StatusCode;

import java.util.Map;
import java.util.Optional;

public class URLDeleteService {
    public static void mainFlow(io.javalin.http.Context ctx){
        Optional<Map<String, Object>> j = ParseJSON.convertToMap(ctx.body());
        //JSON文字列をMapにコンバートできない
        if((!j.isPresent()) || !j.get().containsKey("service-id")){
            ctx.status(StatusCode.BadRequest.code());
            return;
        }
        Optional<IService> s = IServiceStore.getInstance().getService(String.valueOf(j.get().get("service-id")));
        //削除要求のあったサービスIDが存在しない場合
        if(!s.isPresent()){
            ctx.status(StatusCode.NotFound.code());
            return;
        }
        Optional<String> accountUUID = ILoginTokenStore.getInstance().getAccountUUID(ctx.header("authorization"));
        s.ifPresent(service -> {
            //権限なしユーザーが削除を試そうとした場合
            if(!service.getAdminID().equalsIgnoreCase(accountUUID.orElse(""))){
                //サービスの存在の有無にかかわらず404を返しておく
                ctx.status(StatusCode.NotFound.code());
                return;
            }
            //ここでストアの削除処理を呼び出す
            if(IServiceStore.getInstance().deleteService(service.getServiceID())){
                ctx.status(StatusCode.OK.code());
            }else{
                ctx.status(StatusCode.InternalServerError.code());
            }
        });
    }
}

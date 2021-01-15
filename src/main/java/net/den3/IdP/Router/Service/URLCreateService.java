package net.den3.IdP.Router.Service;

import net.den3.IdP.Entity.Service.IService;
import net.den3.IdP.Entity.Service.ServiceBuilder;
import net.den3.IdP.Entity.Service.ServicePermission;
import net.den3.IdP.Store.Auth.ILoginTokenStore;
import net.den3.IdP.Store.Service.IServiceStore;
import net.den3.IdP.Util.MapUtil;
import net.den3.IdP.Util.ParseJSON;
import net.den3.IdP.Util.StatusCode;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class URLCreateService {
    public static void mainFlow(io.javalin.http.Context ctx){

        Optional<Map<String, Object>> j = ParseJSON.convertToMap(ctx.body());

        //Optional<Map<String, String>> jsonString = ParseJSON.convertToStringMap(ctx.body());
        //JSON文字列をMapにコンバートできない
        if((!j.isPresent())){
            ctx.status(StatusCode.BadRequest.code());
            return;
        }else if(!new MapUtil<>().hasKey(j.get(), "service_name","redirect_url","icon_url","description","permissions")){
            ctx.status(StatusCode.BadRequest.code());
            return;
        }
        //登録しようとしたアカウントのUUID取得
        Optional<String> accountUUID = ILoginTokenStore.getInstance().getAccountUUID(Optional.ofNullable(ctx.header("authorization")).orElse(""));
        if(!accountUUID.isPresent()){
            ctx.status(StatusCode.Unauthorized.code());
            return;
        }
        IService s = readJSON(j.get(),accountUUID.get());

        //登録
        if(IServiceStore.getInstance().addService(s)){
            ctx.status(StatusCode.OK.code());
        }else{
            //失敗
            ctx.status(StatusCode.InternalServerError.code());
        }
    }

    private static IService readJSON(Map<String,Object> json,String uuid){
        ServiceBuilder builder = new ServiceBuilder();
        builder.setRedirectURL(String.valueOf(json.get("redirect_url")))
                .setServiceName(String.valueOf(json.get("service_name")))
                .setServiceIconURL(String.valueOf(json.get("icon_url")))
                .setServiceDescription(String.valueOf(json.get("description")))
                .setAdminID(uuid);
        List<String> perms = (List<String>) json.get("permissions");
        for (int i = 0; i < perms.size(); i++) {
            Optional<ServicePermission> optionalPerm = ServicePermission.getPermission(perms.get(i));
            optionalPerm.ifPresent(builder::setUsedPermission);
        }
        return builder.build();
    }
}

package net.den3.IdP.Router.Service;

import net.den3.IdP.Entity.Service.IService;
import net.den3.IdP.Entity.Service.ServiceBuilder;
import net.den3.IdP.Entity.Service.ServicePermission;
import net.den3.IdP.Store.Auth.ILoginTokenStore;
import net.den3.IdP.Store.Service.IServiceStore;
import net.den3.IdP.Util.LongUUID;
import net.den3.IdP.Util.ParseJSON;
import net.den3.IdP.Util.StatusCode;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class URLUpdateService {
    public static void mainFlow(io.javalin.http.Context ctx){
        Optional<Map<String, Object>> jsonMap = ParseJSON.convertToMap(ctx.body());
        //JSON文字列をMapにコンバートできない
        if((!jsonMap.isPresent())){
            ctx.status(StatusCode.BadRequest.code());
            return;
        }

        Optional<IService> oldService = IServiceStore.getInstance().getService(String.valueOf(jsonMap.get().get("client_id")));
        if(!oldService.isPresent()){
            ctx.status(StatusCode.NotFound.code());
            return;
        }

        Optional<String> accountUUID = ILoginTokenStore.getInstance().getAccountUUID(Optional.ofNullable(ctx.header("Authorization")).orElse(""));
        if(!accountUUID.isPresent()){
            ctx.status(StatusCode.Unauthorized.code());
            return;
        }

        if(!oldService.get().getAdminID().equalsIgnoreCase(accountUUID.get())){
            ctx.status(StatusCode.Forbidden.code());
            return;
        }

        IService s = readJSON(oldService.get(),jsonMap.get());
        //登録
        if(IServiceStore.getInstance().updateService(s)){
            ctx.status(StatusCode.OK.code());
        }else{
            //失敗
            ctx.status(StatusCode.InternalServerError.code());
        }
    }

    private static IService readJSON(IService old, Map<String,Object> json){
        ServiceBuilder builder = new ServiceBuilder(old);
        for(String key : json.keySet()){
            switch (key) {
                case "redirect_uri":
                    builder.setRedirectURL(String.valueOf(json.get("redirect_uri")));
                    break;
                case "service_name":
                    builder.setServiceName(String.valueOf(json.get("service_name")));
                    break;
                case "icon_url":
                    builder.setServiceIconURL(String.valueOf(json.get("icon_url")));
                    break;
                case "description":
                    builder.setServiceDescription(String.valueOf(json.get("description")));
                    break;
                case "secret_update":
                    builder.setSecret(LongUUID.generate());
                    break;
            }
        }
        if(json.containsKey("permissions")){
            List<String> perms = (List<String>) json.get("permissions");
            builder.resetUsedPermission();
            for (String perm : perms) {
                Optional<ServicePermission> optionalPerm = ServicePermission.getPermission(perm);
                optionalPerm.ifPresent(builder::setUsedPermission);
            }
        }
        return builder.build();
    }
}

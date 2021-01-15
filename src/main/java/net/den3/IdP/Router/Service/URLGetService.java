package net.den3.IdP.Router.Service;

import net.den3.IdP.Entity.Service.IService;
import net.den3.IdP.Entity.Service.ServicePermission;
import net.den3.IdP.Store.Auth.ILoginTokenStore;
import net.den3.IdP.Store.Service.IServiceStore;
import net.den3.IdP.Util.MapBuilder;
import net.den3.IdP.Util.StatusCode;

import java.util.*;
import java.util.stream.Collectors;

public class URLGetService {

    public static void mainFlow(io.javalin.http.Context ctx) {

        if(ctx.headerMap().containsKey("client_id")){

            Optional<IService> service = IServiceStore.getInstance().getService(ctx.header("client_id"));
            if(!service.isPresent()){
                ctx.status(StatusCode.NotFound.code());
                return;
            }
            IService s = service.get();
            ctx.json(MapBuilder
                    .New()
                    .put("service_name",s.getServiceName())
                    .put("icon_uri",s.getServiceIconURL())
                    .put("description",s.getServiceDescription())
                    .put("permissions",s.getUsedPermission().stream().map(ServicePermission::getName).collect(Collectors.toList())).build());

            return;
        }

        String uuid = ILoginTokenStore.getInstance().getAccountUUID(Optional.ofNullable(ctx.header("authorization")).orElse("")).orElse("");
        if(ctx.queryParamMap().containsKey("id")){
            Optional<IService> service = IServiceStore.getInstance().getService(ctx.queryParam("id"));
            if(service.isPresent() && service.get().getAdminID().equalsIgnoreCase(uuid)){
                List<String> permissions = service.get().getUsedPermission().stream().map(ServicePermission::getName).collect(Collectors.toList());
                ctx.json(MapBuilder
                        .New()
                        .put("admin_id", service.get().getAdminID())
                        .put("client_id", service.get().getServiceID())
                        .put("service_secret",service.get().getSecretID())
                        .put("redirect_uri", service.get().getRedirectURL())
                        .put("service_name", service.get().getServiceName())
                        .put("redirect_url", service.get().getRedirectURL())
                        .put("icon_url", service.get().getServiceIconURL())
                        .put("description", service.get().getServiceDescription())
                        .put("permissions",permissions)
                        .build());
                return;
            }
            ctx.status(StatusCode.NotFound.code());
            return;
        }

        ctx.json(IServiceStore.getInstance().getServices(uuid).orElse(new ArrayList<>()).stream().map(service->{
            List<String> permissions = service.getUsedPermission().stream().map(ServicePermission::getName).collect(Collectors.toList());
                return MapBuilder
                        .New()
                        .put("admin_id", service.getAdminID())
                        .put("service_id", service.getServiceID())
                        .put("redirect_uri", service.getRedirectURL())
                        .put("service_name", service.getServiceName())
                        .put("redirect_url", service.getRedirectURL())
                        .put("icon_url", service.getServiceIconURL())
                        .put("description", service.getServiceDescription())
                        .put("permissions",permissions)
                        .build();
            }).collect(Collectors.toList()));

//        if(ctx.pathParamMap().containsKey("id")){
//            Optional<IService> service = IServiceStore.getInstance().getService(ctx.pathParam("id"));
//            if(service.isPresent() && service.get().getAdminID().equalsIgnoreCase(uuid)){
//                Optional<String> listJSON = ParseJSON.convertToFromList(service.get().getUsedPermission().stream().map(ServicePermission::getName).collect(Collectors.toList()));
//                ctx.json(MapBuilder
//                        .New()
//                        .put("admin-id", service.get().getAdminID())
//                        .put("service-id", service.get().getServiceID())
//                        .put("redirect-url", service.get().getRedirectURL())
//                        .put("service-name", service.get().getServiceName())
//                        .put("redirect-url", service.get().getRedirectURL())
//                        .put("icon-url", service.get().getServiceIconURL())
//                        .put("description", service.get().getServiceDescription())
//                        .put("permissions",listJSON)
//                        .build());
//                return;
//            }
//            ctx.status(StatusCode.NotFound.code());
//        }else{
//            ctx.json(IServiceStore.getInstance().getServices(uuid).orElse(new ArrayList<>()).stream().map(service->{
//                Optional<String> listJSON = ParseJSON.convertToFromList(service.getUsedPermission().stream().map(ServicePermission::getName).collect(Collectors.toList()));
//                return MapBuilder
//                        .New()
//                        .put("admin-id", service.getAdminID())
//                        .put("service-id", service.getServiceID())
//                        .put("redirect-url", service.getRedirectURL())
//                        .put("service-name", service.getServiceName())
//                        .put("redirect-url", service.getRedirectURL())
//                        .put("icon-url", service.getServiceIconURL())
//                        .put("description", service.getServiceDescription())
//                        .put("permissions",listJSON)
//                        .build();
//            }).collect(Collectors.toList()));
//        }
    }
}

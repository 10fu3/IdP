package net.den3.IdP.Router.OAuth2.Token;

import net.den3.IdP.Entity.Account.IAccount;
import net.den3.IdP.Entity.Service.IService;
import net.den3.IdP.Entity.Service.ServicePermission;
import net.den3.IdP.Store.Account.IAccountStore;
import net.den3.IdP.Store.Auth.IAccessTokenStore;
import net.den3.IdP.Store.Auth.ILoginTokenStore;
import net.den3.IdP.Store.Service.IServiceStore;
import net.den3.IdP.Util.MapBuilder;
import net.den3.IdP.Util.StatusCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class URLGetTokenInfo {
    public static void mainFlow(io.javalin.http.Context ctx){
        String uuid = ILoginTokenStore.getInstance().getAccountUUID(Optional.ofNullable(ctx.header("authorization")).orElse("")).orElse("");
        Optional<IAccount> account = IAccountStore.getInstance().getAccountByUUID(uuid);
        if(!account.isPresent()){
            ctx.status(StatusCode.Unauthorized.code());
            return;
        }
        List<String> appendedID = new ArrayList<>();
        ctx.json(IAccessTokenStore.getInstance().getTokenByAccountUUID(uuid).stream()
                .filter(token->{
                    if(!appendedID.contains(token.getClientID())){
                        appendedID.add(token.getClientID());
                        return true;
                    }
                    return false;
                })
                .map(a->{
            MapBuilder builder = MapBuilder.New();
            Optional<IService> service = IServiceStore.getInstance().getService(a.getClientID());
            if(!service.isPresent()){
                return builder.build();
            }
            builder.put("icon_url",service.get().getServiceIconURL())
                   .put("service_name",service.get().getServiceName())
                   .put("permissions", ServicePermission.convertFromScope(a.getScope()))
                   .put("id",a.getClientID());
            return builder.build();
        }).collect(Collectors.toList()));
    }
}

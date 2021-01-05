package net.den3.IdP.Router.OAuth2;

import net.den3.IdP.Util.MapBuilder;

/**
 * 認可ページを表示するためのURL
 */

public class URLAuthorizePage {

    public static void mainFlow(io.javalin.http.Context ctx){
        URLAuthorize.checkParameter(ctx,(param,service)->{
            //TODO 認可認証画面に遷移する
            ctx.render("/template/authorize.html",MapBuilder.New().put("client_id",service.getServiceID()).build());
        });

    }
}

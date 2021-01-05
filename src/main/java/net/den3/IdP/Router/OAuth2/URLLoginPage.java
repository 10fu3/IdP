package net.den3.IdP.Router.OAuth2;

import net.den3.IdP.Util.MapBuilder;

public class URLLoginPage {
    public static void mainFlow(io.javalin.http.Context ctx){
        String redirect = "";

        if(ctx.queryParamMap().containsKey("redirect")){
            redirect = ctx.queryString().replaceFirst("redirect=","");
        }

        System.out.println("query "+ctx.queryString());

        //ログイン画面
        ctx.render("/template/login.html",
                MapBuilder
                .New()
                .put("apiserver","localhost/api/v1")
                .put("redirect",redirect)
                .build());
    }
}

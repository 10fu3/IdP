package net.den3.IdP.Router;

import net.den3.IdP.Router.Account.*;
import net.den3.IdP.Router.Account.API.*;
import net.den3.IdP.Router.OAuth2.*;
import net.den3.IdP.Router.OAuth2.Token.URLGetTokenInfo;
import net.den3.IdP.Router.OAuth2.Token.URLRevokeToken;
import net.den3.IdP.Router.OAuth2.Token.URLTokenRouter;
import net.den3.IdP.Router.OAuth2.Token.URLVerifyToken;
import net.den3.IdP.Router.Service.URLCreateService;
import net.den3.IdP.Router.Service.URLDeleteService;
import net.den3.IdP.Router.Service.URLGetService;
import net.den3.IdP.Router.Service.URLUpdateService;
import net.den3.IdP.Router.Uploader.URLGetUploadFiles;
import net.den3.IdP.Router.Uploader.URLPostFile;


import java.util.Optional;

import static io.javalin.apibuilder.ApiBuilder.*;

public class URLTask {

    public static io.javalin.Javalin webApp;


    public static void setupRouting(){
        webApp = io.javalin.Javalin.create().start(80);
        webApp.config.enableCorsForAllOrigins();
        webApp.before(ctx-> {

            //リバースプロキシ使用時に元のアドレスをたどるためのヘッダーをセットする
            Optional.ofNullable(ctx.header("X-Forwarded-For")).ifPresent(header->{
                String[] ips = header.split(",");
                if(ips.length > 0){
                    ctx.header("ip",ips[0]);
                }else{
                    ctx.header("ip",ctx.ip());
                }
            });

            ctx.header("Access-Control-Allow-Origin","*")
                .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, PATCH")
                .header("Access-Control-Allow-Headers","Authorization, Content-Type, client_id");
        });
        webApp.routes(()-> path("/api/v1",()->{
            path("/account",()->{
                //C
                post("/",URLEntryAccount::mainFlow);
                post("/entry", URLEntryAccount::mainFlow);
                //U
                put("/",URLUpdateProfile::mainFlow);
                put("/profile",URLUpdateProfile::mainFlow);
                //R
                get("/", URLGetProfile::mainFlow);
                get("/profile", URLGetProfile::mainFlow);
                //D
                delete("/",URLDeleteAccount::mainFlow);

                post("/login", URLLogin::mainFlow);
                post("/logout", URLLogout::mainFlow);
                get("/token", URLLoginTokenLive::mainFlow);
            });
            path("/service",()->{
                //C
                post("/",URLCreateService::mainFlow);
                //U
                put("/",URLUpdateService::mainFlow);
                //R
                get("/",URLGetService::mainFlow);
                //D
                delete("/",URLDeleteService::mainFlow);
            });
            post("/img", URLPostFile::mainFlow);
            get("/img", URLGetUploadFiles::mainFlow);
        }));

        webApp.routes(()-> path("/oauth2/v1",()->{
            post("/authorize", URLAuthorize::mainFlow);
            post("/token", URLTokenRouter::mainFlow);
            get("/token", URLGetTokenInfo::mainFlow);
            post("/verify", URLVerifyToken::mainFlow);
            post("/revoke", URLRevokeToken::mainFlow);
        }));
        webApp.get("/account/register/goal/:key",URLConfirmedEntry::mainFlow);
        webApp.get("/account/register/invalid",URLConfirmedEntry::invalid);


    }

}

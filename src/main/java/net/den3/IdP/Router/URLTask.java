package net.den3.IdP.Router;

import net.den3.IdP.Router.Account.*;
import net.den3.IdP.Router.Account.API.*;
import net.den3.IdP.Router.OAuth2.*;
import net.den3.IdP.Router.OAuth2.Token.URLRevokeToken;
import net.den3.IdP.Router.OAuth2.Token.URLTokenRouter;
import net.den3.IdP.Router.OAuth2.Token.URLVerifyToken;
import net.den3.IdP.Router.Service.URLCreateService;
import net.den3.IdP.Router.Service.URLDeleteService;
import net.den3.IdP.Router.Service.URLGetService;
import net.den3.IdP.Router.Service.URLUpdateService;

import static io.javalin.apibuilder.ApiBuilder.*;

public class URLTask {

    public static io.javalin.Javalin webApp;


    public static void setupRouting(){
        webApp = io.javalin.Javalin.create().start(80);
        webApp.before(ctx-> ctx.header("Access-Control-Allow-Origin","*"));

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
        }));

        webApp.routes(()-> path("/oauth2/v1",()->{
            get("/authorize", URLAuthorizePage::mainFlow);
            post("/authorize", URLAuthorize::mainFlow);
            post("/token", URLTokenRouter::mainFlow);
            post("/verify", URLVerifyToken::mainFlow);
            post("/revoke", URLRevokeToken::mainFlow);
        }));
        webApp.get("/login", URLLoginPage::mainFlow);
        webApp.get("/account/register/goal/:key",URLConfirmedEntry::mainFlow);
        webApp.get("/account/register/invalid",URLConfirmedEntry::invalid);


    }

}

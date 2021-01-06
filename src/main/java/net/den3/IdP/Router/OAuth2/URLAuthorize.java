package net.den3.IdP.Router.OAuth2;

import net.den3.IdP.Entity.Account.IAccount;
import net.den3.IdP.Entity.Auth.*;
import net.den3.IdP.Entity.Service.IService;
import net.den3.IdP.Entity.Service.ServicePermission;
import net.den3.IdP.Store.Account.IAccountStore;
import net.den3.IdP.Store.Auth.IAccessTokenStore;
import net.den3.IdP.Store.Auth.IAuthFlowStore;
import net.den3.IdP.Store.Auth.ILoginTokenStore;
import net.den3.IdP.Store.Service.IServiceStore;
import net.den3.IdP.Util.ListUtil;
import net.den3.IdP.Util.MapBuilder;
import net.den3.IdP.Util.StatusCode;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

public class URLAuthorize {

    //openid email profile
    private static boolean hasServicePermission(IService service,String scope){
        ListUtil<ServicePermission> checker = new ListUtil<>();
        if(scope.contains("openid") && !checker.hasElement(service.getUsedPermission(),ServicePermission.READ_UUID)){
            return false;
        }
        if(scope.contains("email") && !checker.hasElement(service.getUsedPermission(),ServicePermission.READ_MAIL)){
            return false;
        }
        return !scope.contains("profile") || checker.hasElement(service.getUsedPermission(), ServicePermission.READ_PROFILE);
    }

    public static void checkParameter(io.javalin.http.Context ctx, BiConsumer<AuthorizeParam,IService> ok){

        AuthorizeParam param = new AuthorizeParam(ctx);
        if(!param.isValidParam()){
            ctx.status(StatusCode.BadRequest.code())
                    .json(MapBuilder.New().put("status","bad parameter").build());
            return;
        }
        Optional<IService> serviceOptional = IServiceStore.getInstance().getService(param.getClientID());
        if(!serviceOptional.isPresent()){
            ctx.status(StatusCode.BadRequest.code())
                    .json(MapBuilder.New().put("status","Client ID is not valid").build());
            return;
        }
        serviceOptional.ifPresent(service-> {
            if (!service.getRedirectURL().equalsIgnoreCase(param.getRedirect_uri())) {
                ctx.status(StatusCode.BadRequest.code())
                        .json(MapBuilder.New().put("status", "Redirect_URL is not valid").build());
                return;
            }
            if (!hasServicePermission(service, param.getScope())) {
                ctx.status(StatusCode.BadRequest.code())
                        .json(MapBuilder.New().put("status", "Service hasn't permission").build());
                return;
            }
            ok.accept(param,service);
        });

    }

    public static void mainFlow(io.javalin.http.Context ctx){
        //認証済みアカウントであればUUIDが入る
        String uuid = ILoginTokenStore.getInstance().getAccountUUID(Optional.ofNullable(ctx.header("authorization")).orElse("")).orElse("");

        //存在しているアカウントであればemptyにならない
        Optional<IAccount> account = IAccountStore.getInstance().getAccountByUUID(uuid);

        if(!account.isPresent()){
            ctx.status(StatusCode.Unauthorized.code());
            return;
        }

        checkParameter(ctx,(param,service)->{
            String accessToken = UUID.randomUUID().toString();

            //アクセストークンエンティティと認可フローエンティティを生成する
            IAccessToken accessTokenEntity = AccessTokenBuilder
                    .New()
                    .setAccountID(account.get().getUUID())
                    .setClientID(service.getServiceID())
                    .setAccessToken(accessToken)
                    .setScope(param.getScope())
                    .setNonce(param.getNonce())
                    .build();

            AuthFlowBuilder authBuilder = AuthFlowBuilder
                    .create()
                    .setAccessToken(accessToken)
                    .setClientID(service.getServiceID())
                    .setAccountID(account.get().getUUID())
                    .setLifeTimeNow();

            //コードチャレンジを含む認可リクエストであればコードチャレンジの情報を追加する
            param.getCodeChallengeMethod().ifPresent(c->authBuilder.setCodeChallengeMethod(CodeChallengeMethod.of(c)));
            param.getCodeChallenge().ifPresent(authBuilder::setCodeChallenge);

            IAuthFlow flow = authBuilder.build();

            //ここでDBに登録される
            IAccessTokenStore.getInstance().addToken(accessTokenEntity);
            IAuthFlowStore.getInstance().addAuthFlow(flow);

            ctx.json(MapBuilder
                    .New()
                    .put("redirect",param.getRedirect_uri()
                            +"?code="+flow.getAuthorizationCode()
                            +"&"+"state="+param.getState())
                    .build());

        });
    }
}

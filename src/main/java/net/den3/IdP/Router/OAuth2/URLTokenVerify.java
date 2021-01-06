package net.den3.IdP.Router.OAuth2;

import com.auth0.jwt.interfaces.DecodedJWT;
import net.den3.IdP.Config;
import net.den3.IdP.Entity.Service.IService;
import net.den3.IdP.Security.JWTVerify;
import net.den3.IdP.Store.Service.IServiceStore;
import net.den3.IdP.Util.ContentsType;
import net.den3.IdP.Util.MapBuilder;
import net.den3.IdP.Util.StatusCode;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class URLTokenVerify {
    public static void mainFlow(io.javalin.http.Context ctx){
        //必須
        String idToken = Optional.ofNullable(ctx.formParam("id_token")).orElse("");
        String clientID = Optional.ofNullable(ctx.formParam("client_id")).orElse("");

        if(idToken.isEmpty() || clientID.isEmpty()){
            ctx.status(StatusCode.BadRequest.code());
            return;
        }

        //任意
        Optional<String> opNonce = Optional.ofNullable(ctx.formParam("nonce"));
        Optional<String> opAccountID = Optional.ofNullable(ctx.formParam("user_id"));

        Optional<IService> opService = IServiceStore.getInstance().getService(clientID);
        if(!opService.isPresent()){
            ctx.status(StatusCode.NotFound.code());
            return;
        }

        MapBuilder builder = MapBuilder.New().put("error","invalid_request");
        opService.ifPresent(service -> {
            AtomicReference<Boolean> valid = new AtomicReference<>(true);
            Optional<DecodedJWT> opJwt = JWTVerify.check(idToken, service.getSecretID(), clientID);
            if(!opJwt.isPresent()){
                builder.put("error_description","Invalid IdToken.");
                valid.set(false);
            }
            opJwt.ifPresent(jwt->{
                opNonce.ifPresent(nonce->{
                    if(!nonce.equalsIgnoreCase(jwt.getClaim("nonce").asString())){
                        builder.put("error_description","Invalid IdToken Nonce.");
                        valid.set(false);
                    }
                });
                opAccountID.ifPresent(accountID->{
                    if(!accountID.equalsIgnoreCase(jwt.getSubject())){
                        builder.put("error_description","Invalid IdToken Subject Identifier.");
                        valid.set(false);
                    }
                });
                //有効期限切れ
                if(jwt.getExpiresAt().after(new Date())){
                    builder.put("error_description","IdToken expired.");
                    valid.set(false);
                    return;
                }
                //受取先が違う
                if(!jwt.getAudience().contains(clientID)){
                    builder.put("error_description","Invalid IdToken Audience.");
                    valid.set(false);
                    return;
                }
                //発行元が違う
                if(!jwt.getIssuer().equalsIgnoreCase(Config.get().getSelfURL())){
                    builder.put("error_description","Invalid IdToken Issuer.");
                    valid.set(false);
                }
            });
            if(valid.get() && opJwt.isPresent()){
                ctx.contentType(ContentsType.JSON.get()).result(opJwt.get().getPayload());
            }else{
                ctx.json(builder.build());
            }
        });
    }
}

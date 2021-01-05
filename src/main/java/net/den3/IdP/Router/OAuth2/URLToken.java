package net.den3.IdP.Router.OAuth2;

import net.den3.IdP.Util.StatusCode;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class URLToken {
    public static void mainFlow(io.javalin.http.Context ctx){
        Map<String, List<String>> param = ctx.formParamMap();
        if(param.containsKey("grant_type")){
            switch (Optional.ofNullable(ctx.formParam("grant_type")).orElse("")){
                case "authorization_code":
                    URLCreateToken.mainFlow(ctx);
                    break;
                case "refresh_token":
                    URLUpdateToken.mainFlow(ctx);
                    break;
                default:
                    ctx.status(StatusCode.BadRequest.code());
                    break;
            }
        }
    }
}

package net.den3.IdP.Security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import net.den3.IdP.Entity.Account.IAccount;
import net.den3.IdP.Entity.Service.IService;
import net.den3.IdP.Entity.Service.ServicePermission;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JWTTokenCreator {
    /**
     * アカウントエンティティから取得できるフィールドの名前リスト
     */
    private static final List<String> fields = Arrays.asList("mail","profile","last_login_time");

    /**
     * 1日を秒で表現
     */
    private static final Long DAY = (60L * 60L * 24L);

    /**
     * 認証に用いるJWTを組み立てる
     * @param builder クレーム追加前のJWT
     * @param service 発行先のサービスのエンティティ
     * @param account 認証したアカウントのエンティティ
     * @param selfID 認証基盤サービスそのもののID
     * @return 組み立てた終わったJWT
     */
    public static JWTCreator.Builder addAuthenticateClaims(JWTCreator.Builder builder, IService service, IAccount account,Optional<String> nonce, String selfID){
        Instant now = Instant.now();
        //トークンの発行者 この場合はこのIdPのURLを使う
        builder.withClaim("iss",selfID);
        //どのアカウントかを示す文字列 Account.UUIDがこれに該当する
        builder.withClaim("sub",account.getUUID());
        //どのサービスに向けて発行したJWTなのかを示す文字列 Service.ServiceIDがこれに該当する
        builder.withClaim("aud",service.getServiceID());
        //JWTがいつまで有効なのか UNIXTime,秒で
        builder.withClaim("exp",now.plusSeconds(DAY).getEpochSecond());
        //JWTが発行された時間 この場合は発行している最中 UNIXTime,秒で
        builder.withClaim("iat",now.getEpochSecond());
        //JWTがオリジナルであることを証明する文字列 被らないものを使う必要がある
        builder.withClaim("jti", UUID.randomUUID().toString());
        //nonce値をJWTに含ませる
        nonce.ifPresent(n->builder.withClaim("nonce",n));
        //プロフィールをJWTに含ませる
        if(service.getUsedPermission().contains(ServicePermission.READ_PROFILE)){
            builder.withClaim("name",account.getNickName());
            builder.withClaim("picture",account.getIconURL());
        }
        //メールアドレスをJWTに含ませる
        if(service.getUsedPermission().contains(ServicePermission.READ_MAIL)){
            builder.withClaim("mail",account.getMail());
        }

        return builder;
    }

    /**
     * 組み立てたJWTをConfigの秘密鍵を使ってHMAC256で署名する
     * @param builder 組み立てたJWT
     * @param secret 秘密鍵
     * @return 署名済みJWT
     */
    public static String signHMAC256(JWTCreator.Builder builder,String secret){
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return builder.sign(algorithm);
    }

    /**
     * 受け取ったJWTの署名を検証をする
     * @param token JWT
     * @param selfID 認証基盤サービスそのもののID
     * @param secret 秘密鍵
     * @return 署名の検証に成功した-> Optional<JWTのペイロードを返す> 署名の検証失敗-> Optional.empty
     */
    public static Optional<String> verifyToken(String token,String selfID,String secret){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(selfID)
                    .build(); //Reusable verifier instance
            DecodedJWT jwt = verifier.verify(token);
            return Optional.ofNullable(jwt.getPayload());
        } catch (JWTVerificationException exception){
            return Optional.empty();
        }
    }
}

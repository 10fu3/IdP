package net.den3.IdP.Security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Optional;

public class JWTVerify {

    public static Optional<DecodedJWT> check(String token, String secret, String serverID){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(serverID)
                    .build(); //Reusable verifier instance
            DecodedJWT verify = verifier.verify(token);
            return Optional.ofNullable(verify);
        } catch (JWTVerificationException exception){
            return Optional.empty();
        }
    }
}

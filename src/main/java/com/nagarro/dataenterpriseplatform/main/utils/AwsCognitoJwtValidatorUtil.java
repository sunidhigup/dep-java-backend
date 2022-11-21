package com.nagarro.dataenterpriseplatform.main.utils;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

public class AwsCognitoJwtValidatorUtil {
    private AwsCognitoJwtValidatorUtil() {
    }

    @Value("${aws.region}")
    public String aws_cognito_region;

    @Value("${aws.cognito.poolID}")
    public String aws_user_pools_id;

    @Value("${aws.cognito.clientId}")
    private String clientId;

    public static final String JWK_URl_SUFFIX = "/.well-known/jwks.json";
//    private static String ISS = "https://cognito-idp." + aws_cognito_region + ".amazonaws.com/" +  aws_user_pools_id + JWK_URl_SUFFIX;

    /**
     * This validates the Aws Jwt Token using Nimbus Jose Jwt Library. For reference please see.
     *
     * @param token
     * @return JWTClaimsSet
     * @see <a href= "https://docs.aws.amazon.com/cognito/latest/developerguide/amazon-cognito-user-pools-using-tokens-with-identity-providers.html#amazon-cognito-identity-user-pools-using-id-and-access-tokens-in-web-api"> AWS JWT Token</>
     */
    public static JWTClaimsSet validateAWSJwtToken(String token) throws ParseException, JOSEException, BadJOSEException, MalformedURLException, CustomException {

        /**
         * AwsCognitoJwtParserUtil class parse the jwt token and gives back the payload.
         */
        String jsonWebKeyFileURL = AwsCognitoJwtParserUtil.getJsonWebKeyURL(token);

//        String iss = String.format("https://cognito-idp.%s.amazonaws.com/%s", aws_cognito_region, aws_user_pools_id);
//        System.out.print(ISS);

        ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();
        JWKSource jwkSource = null;
        jwkSource = new RemoteJWKSet(new URL(jsonWebKeyFileURL));
        JWSAlgorithm jwsAlgorithm = JWSAlgorithm.RS256;
        JWSKeySelector keySelector = new JWSVerificationKeySelector(jwsAlgorithm, jwkSource);
        jwtProcessor.setJWSKeySelector(keySelector);
        try {
            JWTClaimsSet claimsSet = jwtProcessor.process(token, null);
            return claimsSet;

        } catch (BadJWTException e) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, AuthenticationError.TOKEN_EXPIRED, e.getLocalizedMessage());
        }

    }

}

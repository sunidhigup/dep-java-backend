package com.nagarro.dataenterpriseplatform.main.utils;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ValidateAwsCognitoJwtToken {
    @Value("${aws.region}")
    private String aws_cognito_region;

    @Value("${aws.cognito.poolID}")
    private String aws_user_pools_id;

    @Value("${aws.cognito.clientId}")
    private String clientId;

    @Autowired
    private AWSCognitoIdentityProvider cognitoIdentityProvider;

    public static final String JWK_URl_SUFFIX = "/.well-known/jwks.json";

    public JWTClaimsSet verifyToken(String token) throws CustomException {
        String iss = String.format("https://cognito-idp.%s.amazonaws.com/%s", aws_cognito_region, aws_user_pools_id);
//        RSAKeyProvider keyProvider = new AwsCognitoRSAKeyProvider(aws_cognito_region, aws_user_pools_id);
//        Algorithm algorithm = Algorithm.RSA256(keyProvider);
//        JWTVerifier jwtVerifier = JWT.require(algorithm)
//                .withIssuer(iss)
//                .withClaim("token_use", "access")
//                //.withAudience("2qm9sgg2kh21masuas88vjc9se") // Validate your apps audience if needed
//                .build();

        try {
            ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();
            JWKSource jwkSource = null;
            jwkSource = new RemoteJWKSet(new URL(iss + JWK_URl_SUFFIX));
            JWSAlgorithm jwsAlgorithm = JWSAlgorithm.RS256;
            JWSKeySelector keySelector = new JWSVerificationKeySelector(jwsAlgorithm, jwkSource);
            jwtProcessor.setJWSKeySelector(keySelector);

            JWTClaimsSet claimsSet = jwtProcessor.process(token, null);
            return claimsSet;

        } catch (ParseException | BadJOSEException | JOSEException | MalformedURLException e) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, AuthenticationError.TOKEN_EXPIRED, e.getLocalizedMessage());
        }


//        return jwtVerifier.verify(token);

    }

    public LinkedHashMap<String, String> generateNewToken(String token) {
        try {
            Map<String, String> authParams = new LinkedHashMap<String, String>() {{
                put("REFRESH_TOKEN", token);
            }};

            InitiateAuthRequest authRequest = new InitiateAuthRequest()
                    .withAuthFlow(AuthFlowType.REFRESH_TOKEN_AUTH)
                    .withClientId(clientId)
                    .withAuthParameters(authParams);
            InitiateAuthResult authResult = cognitoIdentityProvider.initiateAuth(authRequest);
            AuthenticationResultType resultType = authResult.getAuthenticationResult();

            LinkedHashMap<String, String> result = new LinkedHashMap<String, String>() {{
                put("access_token", resultType.getAccessToken());
            }};

            return result;
        } catch (AWSCognitoIdentityProviderException e) {
            System.err.println(e.getMessage());
            LinkedHashMap<String, String> result = new LinkedHashMap<String, String>() {{
                put("error", e.getMessage());
            }};
            return result;
        }
    }

    public void setAccessToken(String cookieName, String token, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, token);

        cookie.setMaxAge(60 * 60);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); //only allows HTTPS
        cookie.setPath("/");

        response.addCookie(cookie);
    }

    public boolean checkToken(HttpServletRequest req,HttpServletResponse response) {
        Cookie[] cookies = req.getCookies();

        if (cookies != null) {
            Cookie refreshToken = null;
            Cookie accessToken = null;
            Cookie eata = null;

            LocalDateTime date = LocalDateTime.now();
            int seconds = date.toLocalTime().toSecondOfDay();

            for (Cookie cookie : cookies) {

                if (cookie.getName().equals("refresh_token")) {
                    refreshToken = cookie;
                }
                if (cookie.getName().equals("eata")) {
                    eata = cookie;
                }
                if (cookie.getName().equals("access_token")) {
                    accessToken = cookie;
                }
            }

            if (refreshToken != null && eata != null && accessToken != null) {

                String strEata = eata.getValue();
                long fetchEata = Long.parseLong(strEata);

                long timeDiff = seconds - fetchEata;

                System.out.println(seconds);
                System.out.println(fetchEata);
                System.out.println(timeDiff);
                if (timeDiff > 3600 && timeDiff < 21300) {
                    LinkedHashMap<String, String> result = generateNewToken(refreshToken.getValue());

                    if(result.get("access_token") == null){
                        return false;
                    }

                    setAccessToken("access_token", result.get("access_token"),response);
                    setAccessToken("eata", String.valueOf(seconds),response);
                    return true;

                } else return timeDiff < 3300;
            }
        }
        return false;
    }
}

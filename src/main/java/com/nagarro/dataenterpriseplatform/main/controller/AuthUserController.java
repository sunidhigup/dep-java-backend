package com.nagarro.dataenterpriseplatform.main.controller;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.*;
import com.amazonaws.services.secretsmanager.model.InvalidParameterException;
import com.amazonaws.services.secretsmanager.model.ResourceNotFoundException;
import com.google.gson.Gson;
import com.nagarro.dataenterpriseplatform.main.dto.UserDto;
import com.nagarro.dataenterpriseplatform.main.utils.CookieUtil;
import com.nagarro.dataenterpriseplatform.main.utils.CustomException;
import com.nagarro.dataenterpriseplatform.main.utils.ValidateAwsCognitoJwtToken;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.*;

import static com.amazonaws.services.cognitoidp.model.AuthFlowType.USER_PASSWORD_AUTH;

/**
 * Controller class for user authentication using AWS secret manager
 */

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class AuthUserController {

    @Autowired
    private ValidateAwsCognitoJwtToken validateAwsCognitoJwtToken;

    @Autowired
    private AWSSecretsManager awsSecretsManager;

    @Autowired
    private AWSCognitoIdentityProvider cognitoIdentityProvider;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.cognito.clientId}")
    private String clientId;

    @Autowired
    private CookieUtil cookieUtil;

    /*
     *  API for user authentication
     */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody UserDto user) {

        String secretName = "arn:aws:secretsmanager:us-east-1:955658629586:secret:dep/login-HpofYH";

        String secret = null, decodedBinarySecret;
        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId(secretName);
        GetSecretValueResult getSecretValueResult = null;

        try {
            getSecretValueResult = awsSecretsManager.getSecretValue(getSecretValueRequest);
        } catch (DecryptionFailureException | InternalServiceErrorException | InvalidParameterException | ResourceNotFoundException | InvalidRequestException e) {
            throw e;
        }

        if (getSecretValueResult.getSecretString() != null) {
            secret = getSecretValueResult.getSecretString();
        } else {
            decodedBinarySecret = new String(Base64.getDecoder().decode(getSecretValueResult.getSecretBinary()).array());
        }

        HashMap<String, String> map = new Gson().fromJson(secret.toString(), HashMap.class);

        String username = map.getOrDefault(user.getUsername(), "null");

        if (Objects.equals(username, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.OK).body(user.getUsername());
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /*
     * API for user signup using aws cognito service
     * */

    @PostMapping("/signup-user")
    public ResponseEntity<?> signUpUser(@RequestBody UserDto user) {
        try {
            SignUpRequest signUpRequest = new SignUpRequest().withClientId(clientId).
                    withUsername(user.getUsername())
                    .withPassword(user.getPassword()).withUserAttributes(new AttributeType()
                            .withName("email")
                            .withValue(user.getEmail()));

            SignUpResult signUpResult = cognitoIdentityProvider.signUp(signUpRequest);

            return ResponseEntity.status(HttpStatus.CREATED).body(signUpResult);
        } catch (AWSCognitoIdentityProviderException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getErrorMessage());
        }

    }

    /*
     * API for user confirmation using aws cognito service
     * */

    @PostMapping("/verify-user")
    public ResponseEntity<?> confirmSignUp(@RequestBody UserDto user) {
        try {
            ConfirmSignUpRequest confirmSignUpRequest = new ConfirmSignUpRequest().withClientId(clientId)
                    .withUsername(user.getUsername()).withConfirmationCode(user.getConfirmationCode());
            ConfirmSignUpResult res = cognitoIdentityProvider.confirmSignUp(confirmSignUpRequest);
            return ResponseEntity.status(HttpStatus.OK).body(res);
        } catch (AWSCognitoIdentityProviderException e) {
            System.err.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getErrorMessage());
        }

    }

    @PostMapping("/signin-user")
    public ResponseEntity<Object> signinUser(@RequestBody UserDto user, HttpServletResponse response) {

        try {
            Map<String, String> authParams = new LinkedHashMap<String, String>() {{
                put("USERNAME", user.getUsername());
                put("PASSWORD", user.getPassword());
            }};

            InitiateAuthRequest initiateAuthRequest = new InitiateAuthRequest()
                    .withAuthFlow(USER_PASSWORD_AUTH)
                    .withClientId(clientId)
                    .withAuthParameters(authParams);
            InitiateAuthResult res = cognitoIdentityProvider.initiateAuth(initiateAuthRequest);

            Cookie refreshTokenCookie = new Cookie("refresh_token", res.getAuthenticationResult().getRefreshToken());
            refreshTokenCookie.setMaxAge(21600);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(false); //only allows HTTPS
            refreshTokenCookie.setPath("/");
//            refreshTokenCookie.setDomain("api.lsp.com");

            response.addCookie(refreshTokenCookie);

            Cookie accessTokenCookie = new Cookie("access_token", res.getAuthenticationResult().getAccessToken());

            accessTokenCookie.setMaxAge(60 * 60);
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(false); //only allows HTTPS
            accessTokenCookie.setPath("/");
//            accessTokenCookie.setDomain("api.lsp.com");
            response.addCookie(accessTokenCookie);


            LocalDateTime date = LocalDateTime.now();
            int seconds = date.toLocalTime().toSecondOfDay();

            Cookie eataCookie = new Cookie("eata", String.valueOf(seconds));

            eataCookie.setMaxAge(60 * 60);
            eataCookie.setHttpOnly(true);
            eataCookie.setSecure(false); //only allows HTTPS
            eataCookie.setPath("/");

            response.addCookie(eataCookie);
            return ResponseEntity.ok().body(user.getUsername());

        } catch (AWSCognitoIdentityProviderException e) {
            System.err.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getErrorMessage());
        }

    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody UserDto user) {
        try {
            Map<String, String> authParams = new LinkedHashMap<String, String>() {{
                put("REFRESH_TOKEN", user.getRefreshToken());
            }};

            InitiateAuthRequest authRequest = new InitiateAuthRequest()
                    .withAuthFlow(AuthFlowType.REFRESH_TOKEN_AUTH)
                    .withClientId(clientId)
                    .withAuthParameters(authParams);
            InitiateAuthResult authResult = cognitoIdentityProvider.initiateAuth(authRequest);
            AuthenticationResultType resultType = authResult.getAuthenticationResult();
            LinkedHashMap<String, String> result = new LinkedHashMap<String, String>() {{
                put("idToken", resultType.getIdToken());
                put("accessToken", resultType.getAccessToken());
                put("message", "Successfully login");
            }};
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (AWSCognitoIdentityProviderException e) {
            System.err.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getErrorMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        try {
//            GlobalSignOutRequest request = new GlobalSignOutRequest().withAccessToken(user.getAccessToken());
//            GlobalSignOutResult result = cognitoIdentityProvider.globalSignOut(request);
//            return ResponseEntity.status(HttpStatus.CREATED).body(result);
            Cookie accessTokenCookie = new Cookie("access_token", "");

            accessTokenCookie.setMaxAge(-1);
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(false); //only allows HTTPS
            accessTokenCookie.setPath("/");
//            accessTokenCookie.setDomain("api.lsp.com");
            response.addCookie(accessTokenCookie);

            Cookie refreshTokenCookie = new Cookie("refresh_token", "");

            refreshTokenCookie.setMaxAge(-1);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(false); //only allows HTTPS
            refreshTokenCookie.setPath("/");
//            accessTokenCookie.setDomain("api.lsp.com");
            response.addCookie(refreshTokenCookie);

            Cookie eataCookie = new Cookie("eata", "");

            eataCookie.setMaxAge(-1);
            eataCookie.setHttpOnly(true);
            eataCookie.setSecure(false); //only allows HTTPS
            eataCookie.setPath("/");
//            accessTokenCookie.setDomain("api.lsp.com");
            response.addCookie(eataCookie);

            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (AWSCognitoIdentityProviderException e) {
            System.err.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getErrorMessage());
        }
    }

    @PostMapping("/verify-token")
    public ResponseEntity<?> verifyAccessToken(@RequestBody UserDto user) throws MalformedURLException, CustomException {
        try{
            JWTClaimsSet val = this.validateAwsCognitoJwtToken.verifyToken(user.getAccessToken());
            System.out.println(val.getClaim("exp"));
            return ResponseEntity.status(HttpStatus.CREATED).body(val);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }
}



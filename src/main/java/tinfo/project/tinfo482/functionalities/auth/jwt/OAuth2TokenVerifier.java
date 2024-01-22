package tinfo.project.tinfo482.functionalities.auth.jwt;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GooglePublicKeysManager;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Slf4j
public class OAuth2TokenVerifier {

    @Value("${google.client.id}")
    private String GOOGLE_CLIENT_ID;


    public void verifyAndGenerateJwt(String idToken){
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();


        // Google API Client Library에서 제공하는 GoogleNetHttpTransport를 사용하여 httpTransport 생성
        try {
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            GooglePublicKeysManager publicKeysManager = new GooglePublicKeysManager.Builder(httpTransport, jsonFactory)
                    .setPublicCertsEncodedUrl("https://www.googleapis.com/oauth2/v3/certs").build();
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(publicKeysManager)
                    .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID)).build();

            GoogleIdToken googleIdToken = verifier.verify(idToken);

            if(googleIdToken != null){
                String userId = googleIdToken.getPayload().getSubject();
                log.info(userId);
            }else{
                log.error("Invalid ID Token");
            }

        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }
}

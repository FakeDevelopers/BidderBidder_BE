package com.fakedevelopers.bidderbidder.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {
    // https://firebase.google.com/docs/admin/setup?hl=ko#java_1
    private static final String FIREBASE_SDK_PATH = "./firebase.json";

    @Bean
    public FirebaseAuth firebaseAuth() throws IOException {
        FileInputStream fis = new FileInputStream(FIREBASE_SDK_PATH);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(fis))
                .build();

        FirebaseApp.initializeApp(options);
        return FirebaseAuth.getInstance(FirebaseApp.getInstance());
    }
}

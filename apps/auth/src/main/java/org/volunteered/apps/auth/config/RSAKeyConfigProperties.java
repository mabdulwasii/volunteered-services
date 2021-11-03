package org.volunteered.apps.auth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.volunteered.apps.auth.security.RSAEncryptionUtils;
import org.volunteered.apps.auth.service.AuthService;

import javax.annotation.PostConstruct;
import java.security.PrivateKey;
import java.security.PublicKey;


@Configuration
@ConfigurationProperties(prefix = "rsa.key")
public class RSAKeyConfigProperties {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);


    @Value("${rsa.key.publicKeyFile}")
    private String publicKeyFile;

    @Value("${rsa.key.privateKeyFile}")
    private String privateKeyFile;

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Value("${rsa.key.secret}")
    private String secret;

    @PostConstruct
    public void createRSAKey() throws Exception {
        logger.info("This.publicKeyFile " + publicKeyFile);
        logger.info("This.privateKeyFile " + privateKeyFile);
        RSAEncryptionUtils.generateKey(publicKeyFile, privateKeyFile, secret, 1);

        this.publicKey = RSAEncryptionUtils.getPublicKey(publicKeyFile);
        this.privateKey = RSAEncryptionUtils.getPrivateKey(privateKeyFile);
        logger.info("This.PrivateKey " + privateKey);
        logger.info("This.PublicKey " + publicKey);
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
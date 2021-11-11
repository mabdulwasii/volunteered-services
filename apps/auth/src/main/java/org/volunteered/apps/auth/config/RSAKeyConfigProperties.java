package org.volunteered.apps.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.volunteered.apps.auth.security.encryption.RSAEncryptionUtils;

import java.security.PrivateKey;
import java.security.PublicKey;


@Configuration
@ConfigurationProperties(prefix = "rsa.key")
public class RSAKeyConfigProperties {

    @Value("${rsa.key.publicKeyFile}")
    private String publicKeyFile;

    @Value("${rsa.key.privateKeyFile}")
    private String privateKeyFile;

    private PublicKey publicKey;

    private PrivateKey privateKey;

    public void loadRSAKey() throws Exception {
        this.publicKey = RSAEncryptionUtils.getPublicKey(publicKeyFile);
        this.privateKey = RSAEncryptionUtils.getPrivateKey(privateKeyFile);
    
    }
    
    
    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() throws Exception {
        loadRSAKey();
        return privateKey;
    }
}
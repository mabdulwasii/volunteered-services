package org.volunteered.apps.auth.security.encryption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAEncryptionUtils {

    /**
     * encryption algorithm
     */
    public static final String ENCRYPT_ALGORITHM = "RSA";


    private static final Logger logger = LoggerFactory.getLogger(RSAEncryptionUtils.class);

    private final ResourceLoader resourceLoader;

    public RSAEncryptionUtils(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Read public key from file
     *
     * @param filename Public key save path
     * @return
     * @throws Exception
     */
    public static PublicKey getPublicKey(String filename) throws Exception {
        byte[] bytes = readFile(filename);
        logger.info("Public bytes  ===> " + new String(bytes));
        return getPublicKey(bytes);
    }

    /**
     * Get key from file
     *
     * @param filename
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String filename) throws Exception {
        byte[] bytes = readFile(filename);
        logger.info("Private bytes  ===> " + new String(bytes));
        return getPrivateKey(bytes);
    }

    /**
     * Get public key
     *
     * @param bytes Public key byte form
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private static PublicKey getPublicKey(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        bytes = Base64.getDecoder().decode(bytes);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance(ENCRYPT_ALGORITHM);
        return factory.generatePublic(spec);
    }

    /**
     * Get key
     *
     * @param bytes Key byte form
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private static PrivateKey getPrivateKey(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        bytes = Base64.getDecoder().decode(bytes);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance(ENCRYPT_ALGORITHM);
        return factory.generatePrivate(spec);
    }

    private static byte[] readFile(String filename) throws IOException {
        var s = Files.readString(new File(filename).getAbsoluteFile().toPath(), StandardCharsets.UTF_8);
        return s.getBytes(StandardCharsets.UTF_8);
    }

}
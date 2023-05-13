package uk.co.bbr.services.security;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

@UtilityClass
public class PasswordTools {
    private static final String LATEST_PASSWORD_VERSION = "1";

    public static String createSalt() {
        return RandomStringUtils.randomAlphanumeric(8);
    }

    public static String latestVersion() {
        return LATEST_PASSWORD_VERSION;
    }

    public static String hashPassword(String passwordVersion, String salt, String usercode, String plaintextPassword) {
        switch (passwordVersion) {
            case "1":
            default:
                try {
                    String plaintext = salt + ";" + plaintextPassword;
                    KeySpec spec = new PBEKeySpec(plaintext.toCharArray(), salt.getBytes(), 100000, 256);
                    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                    byte[] hashBytes = factory.generateSecret(spec).getEncoded();
                    return PasswordTools.bytesToHex(hashBytes);
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    throw new UnsupportedOperationException("Algorithm not found", e);
                }

        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}

package labs.web4_backend.utils;


import jakarta.ejb.Stateless;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
@Stateless
public class PasswordCrypter {
    private final MessageDigest digest;

    public PasswordCrypter() {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String hashPassword(String password) {
        byte[] hashBytes = digest.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hashBytes);
    }

}
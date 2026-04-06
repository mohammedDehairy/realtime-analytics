package com.eldoheiri.realtime_analytics.security.idgeneration;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public final class IdentifierUtil {
    private String secretKeyString;

    public IdentifierUtil() {
        secretKeyString = System.getenv("DEVICE_ID_SECRET_KEY");
    }

    public String generateId(String contexString) throws NoSuchAlgorithmException, InvalidKeyException {
        if (!StringUtils.hasText(contexString)) {
            throw new IllegalArgumentException("contexString cannot be null");
        }
        
        String newIdentifier = UUID.randomUUID().toString();
        String newIdWithContextString = contexString + "_" + newIdentifier;

        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(Base64.getDecoder().decode(secretKeyString), "HmacSHA256");
        mac.init(secretKey);
        byte[] hash = mac.doFinal(newIdWithContextString.getBytes());
        String hmacBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);

        return newIdentifier + "." + hmacBase64;
    }

    public boolean validateIdentifier(String contexString, String inputIdentifier) throws NoSuchAlgorithmException, InvalidKeyException {
        if (!StringUtils.hasText(inputIdentifier)) {
            throw new IllegalArgumentException("inputIdentifier cannot be null");
        }

        if (!StringUtils.hasText(contexString)) {
            throw new IllegalArgumentException("contexString cannot be null");
        }

        String[] parts = inputIdentifier.split("\\.");
        if (parts.length != 2) {
            return false;
        }

        String identifier = parts[0];
        String idWithContextString = contexString + "_" + identifier;

        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(Base64.getDecoder().decode(secretKeyString), "HmacSHA256");
        mac.init(secretKey);
        byte[] hash = mac.doFinal(idWithContextString.getBytes());
        String hmacBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);

        return hmacBase64.equals(parts[1]);
    }
}

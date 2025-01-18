package com.eldoheiri.realtime_analytics.security.idgeneration;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

@Component
public final class IdentifierUtil {
    private String secretKeyString;

    public IdentifierUtil() {
        secretKeyString = System.getenv("DEVICE_ID_SECRET_KEY");
    }

    public String generateId(String applicationId) throws NoSuchAlgorithmException, InvalidKeyException {
        String newIdentifier = UUID.randomUUID().toString();
        String newIdWithApplicationId = applicationId + "_" + newIdentifier;

        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(Base64.getDecoder().decode(secretKeyString), "HmacSHA256");
        mac.init(secretKey);
        byte[] hash = mac.doFinal(newIdWithApplicationId.getBytes());
        String hmacBase64 = Base64.getEncoder().encodeToString(hash);

        return newIdentifier + "." + hmacBase64;
    }

    public boolean validateIdentifier(String applicationId, String inputIdentifier) throws NoSuchAlgorithmException, InvalidKeyException {
        String[] parts = inputIdentifier.split("\\.");
        if (parts.length != 2) {
            return false;
        }

        String identifier = parts[0];
        String idWithApplicationId = applicationId + "_" + identifier;

        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(Base64.getDecoder().decode(secretKeyString), "HmacSHA256");
        mac.init(secretKey);
        byte[] hash = mac.doFinal(idWithApplicationId.getBytes());
        String hmacBase64 = Base64.getEncoder().encodeToString(hash);

        return hmacBase64.equals(parts[1]);
    }
}

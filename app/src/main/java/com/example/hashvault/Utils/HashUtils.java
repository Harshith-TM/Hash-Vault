package com.example.hashvault.Utils;

import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {
    public static String generateHash(String text, String algorithm) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            byte[] hashBytes = messageDigest.digest(text.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            Log.e("Algorithm Error", "Error: " + e);
            return null;
        }
    }

    private static String bytesToHex(byte[] hashBytes) {
        char[] hex_array = "0123456789abcdef".toCharArray();
        if (hashBytes == null) return "";
        char[] hexChars = new char[hashBytes.length*2];
        for (int i = 0; i < hashBytes.length; i++) {
            int v = hashBytes[i] & 0xFF;
            hexChars[i * 2] = hex_array[v >>> 4];
            hexChars[i * 2 + 1] = hex_array[v & 0x0F];
        }
        return new String(hexChars);
    }
}

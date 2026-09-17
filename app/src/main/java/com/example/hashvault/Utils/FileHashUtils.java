package com.example.hashvault.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileHashUtils {
    private static final int BUFFER_SIZE = 8192;

    public static String generateFileHash(InputStream inputStream, String algorithm)
            throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            digest.update(buffer, 0, bytesRead);
        }
        return bytesToHex(digest.digest());
    }

    private static String bytesToHex(byte[] hashBytes) {
        char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[hashBytes.length * 2];
        for (int i = 0; i < hashBytes.length; i++) {
            int v = hashBytes[i] & 0xFF;
            hexChars[i * 2] = hexArray[v >>> 4];
            hexChars[i * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
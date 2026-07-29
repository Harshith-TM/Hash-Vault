package com.example.hashvault.Repository;

import com.example.hashvault.Model.HashModel;
import com.example.hashvault.Utils.HashUtils;

public class HashRepository {
    public HashModel generateHash(String text) {
        String md5Hash = HashUtils.generateHash(text,"MD5");
        String sha1Hash = HashUtils.generateHash(text,"SHA1");
        String sha256Hash = HashUtils.generateHash(text,"SHA256");
        return new HashModel(md5Hash, sha1Hash, sha256Hash);
    }
}

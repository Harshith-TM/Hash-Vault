package com.example.hashvault.Model;

public class HashModel {
    private String md5Hash;
    private String sha1Hash;
    private String sha256Hash;

    public HashModel(String md5Hash, String sha1Hash, String sha256Hash) {
        this.md5Hash = md5Hash;
        this.sha1Hash = sha1Hash;
        this.sha256Hash = sha256Hash;
    }

    public String getMd5Hash() {
        return md5Hash;
    }

    public String getSha1Hash() {
        return sha1Hash;
    }

    public String getSha256Hash() {
        return sha256Hash;
    }
}
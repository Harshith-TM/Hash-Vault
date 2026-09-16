package com.example.hashvault.Model;

public class HashModel {
    private String md5Hash;
    private String sha1Hash;
    private String sha256Hash;
    private String sha384Hash;
    private String sha512Hash;

    public HashModel(String md5Hash, String sha1Hash, String sha256Hash, String sha384Hash, String sha512Hash) {
        this.md5Hash = md5Hash;
        this.sha1Hash = sha1Hash;
        this.sha256Hash = sha256Hash;
        this.sha384Hash = sha384Hash;
        this.sha512Hash = sha512Hash;
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

    public String getSha384Hash() {
        return sha384Hash;
    }

    public String getSha512Hash() {
        return sha512Hash;
    }
}
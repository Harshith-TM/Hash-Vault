package com.example.hashvault.Model;

public class FileHashModel {
    private String fileName;
    private String algorithm;
    private String hashValue;

    public FileHashModel(String fileName, String algorithm, String hashValue) {
        this.fileName = fileName;
        this.algorithm = algorithm;
        this.hashValue = hashValue;
    }

    public String getFileName() { return fileName; }
    public String getAlgorithm() { return algorithm; }
    public String getHashValue() { return hashValue; }
}
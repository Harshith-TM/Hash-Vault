package com.example.hashvault.Model;

public class HashMatchResult {
    private boolean md5Match;
    private boolean sha1Match;
    private boolean sha256Match;
    private boolean sha384Match;
    private boolean sha512Match;

    public HashMatchResult(boolean md5Match, boolean sha1Match, boolean sha256Match, boolean sha384Match, boolean sha512Match) {
        this.md5Match = md5Match;
        this.sha1Match = sha1Match;
        this.sha256Match = sha256Match;
        this.sha384Match = sha384Match;
        this.sha512Match = sha512Match;
    }

    public boolean isMd5Match() { return md5Match; }
    public boolean isSha1Match() { return sha1Match; }
    public boolean isSha256Match() { return sha256Match; }
    public boolean isSha384Match() { return sha384Match; }
    public boolean isSha512Match() { return sha512Match; }
}
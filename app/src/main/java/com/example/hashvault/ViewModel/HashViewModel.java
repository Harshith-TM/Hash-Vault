package com.example.hashvault.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hashvault.Model.HashMatchResult;
import com.example.hashvault.Model.HashModel;
import com.example.hashvault.Repository.HashRepository;

public class HashViewModel extends ViewModel {
    private MutableLiveData<HashModel> hashData = new MutableLiveData<>();
    private MutableLiveData<HashMatchResult> matchResult = new MutableLiveData<>();
    private HashRepository hashRepository = new HashRepository();

    public LiveData<HashModel> getHashData() {
        return hashData;
    }

    public LiveData<HashMatchResult> getMatchResult() {
        return matchResult;
    }

    public void generateHashes(String text) {
        HashModel hashes = hashRepository.generateHash(text);
        hashData.setValue(hashes);
    }

    public void compareHash(String inputHash) {
        HashModel current = hashData.getValue();
        if (current == null || inputHash == null || inputHash.trim().isEmpty()) {
            matchResult.setValue(null);
            return;
        }

        String normalizedInput = inputHash.trim().toLowerCase();
        boolean md5Match = normalizedInput.equals(safeLower(current.getMd5Hash()));
        boolean sha1Match = normalizedInput.equals(safeLower(current.getSha1Hash()));
        boolean sha256Match = normalizedInput.equals(safeLower(current.getSha256Hash()));
        boolean sha384Match = normalizedInput.equals(safeLower(current.getSha384Hash()));
        boolean sha512Match = normalizedInput.equals(safeLower(current.getSha512Hash()));

        matchResult.setValue(new HashMatchResult(md5Match, sha1Match, sha256Match, sha384Match, sha512Match));
    }

    public void clearMatchResult() {
        matchResult.setValue(null);
    }

    private String safeLower(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
package com.example.hashvault.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hashvault.Model.HashModel;
import com.example.hashvault.Repository.HashRepository;

public class HashViewModel extends ViewModel {
    private MutableLiveData<HashModel> hashData = new MutableLiveData<>();
    private HashRepository hashRepository = new HashRepository();

    public LiveData<HashModel> getHashData() {
        return hashData;
    }

    public void generateHashes(String text) {
        HashModel hashes = hashRepository.generateHash(text);
        hashData.setValue(hashes);
    }
}

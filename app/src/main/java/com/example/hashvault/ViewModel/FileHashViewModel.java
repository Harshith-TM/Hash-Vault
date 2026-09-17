package com.example.hashvault.ViewModel;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hashvault.Model.FileHashModel;
import com.example.hashvault.Repository.FileHashRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileHashViewModel extends ViewModel {

    private final FileHashRepository repository = new FileHashRepository();
    private final MutableLiveData<FileHashModel> fileHashData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public LiveData<FileHashModel> getFileHashData() { return fileHashData; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void generateFileHash(Context context, Uri fileUri, String fileName, String algorithm) {
        isLoading.postValue(true);
        executor.execute(() -> {
            try {
                FileHashModel result = repository.generateFileHash(context, fileUri, fileName, algorithm);
                fileHashData.postValue(result);
            } catch (Exception e) {
                errorMessage.postValue("Error: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        });
    }

    public void clear() {
        fileHashData.postValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
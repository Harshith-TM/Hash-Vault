package com.example.hashvault.Repository;

import android.content.Context;
import android.net.Uri;

import com.example.hashvault.Model.FileHashModel;
import com.example.hashvault.Utils.FileHashUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

public class FileHashRepository {

    public FileHashModel generateFileHash(Context context, Uri fileUri, String fileName, String algorithm)
            throws IOException, NoSuchAlgorithmException {
        try (InputStream inputStream = context.getContentResolver().openInputStream(fileUri)) {
            if (inputStream == null) {
                throw new IOException("Unable to open file");
            }
            String hash = FileHashUtils.generateFileHash(inputStream, algorithm);
            return new FileHashModel(fileName, algorithm, hash);
        }
    }
}
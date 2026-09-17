package com.example.hashvault.Utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.example.hashvault.Model.HashModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ExportUtils {

    private static final String FILE_NAME = "HashVaultExport.txt";

    public static void exportHashes(Context context, String text, HashModel hashModel) throws IOException {
        String content = buildExportContent(text, hashModel);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            exportViaMediaStore(context, content);
        } else {
            exportLegacy(content);
        }
    }

    private static String buildExportContent(String text, HashModel hashModel) {
        StringBuilder sb = new StringBuilder();
        sb.append("Text: ").append(text).append("\n");
        sb.append("MD5: ").append(hashModel.getMd5Hash()).append("\n");
        sb.append("SHA1: ").append(hashModel.getSha1Hash()).append("\n");
        sb.append("SHA256: ").append(hashModel.getSha256Hash()).append("\n");
        sb.append("SHA384: ").append(hashModel.getSha384Hash()).append("\n");
        sb.append("SHA512: ").append(hashModel.getSha512Hash()).append("\n");
        sb.append("------------------------------\n");
        return sb.toString();
    }

    private static void exportViaMediaStore(Context context, String content) throws IOException {
        ContentResolver resolver = context.getContentResolver();
        Uri collection = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        }
        Uri existingUri = findExistingFile(resolver, collection);

        OutputStream out;
        if (existingUri != null) {
            out = resolver.openOutputStream(existingUri, "wa"); // "wa" = write-append
        } else {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, FILE_NAME);
            values.put(MediaStore.Downloads.MIME_TYPE, "text/plain");
            assert collection != null;
            Uri newUri = resolver.insert(collection, values);
            if (newUri == null) {
                throw new IOException("Failed to create export file");
            }
            out = resolver.openOutputStream(newUri);
        }

        if (out == null) {
            throw new IOException("Failed to open output stream");
        }

        out.write(content.getBytes());
        out.close();
    }

    private static Uri findExistingFile(ContentResolver resolver, Uri collection) {
        String[] projection = { MediaStore.Downloads._ID };
        String selection = MediaStore.Downloads.DISPLAY_NAME + "=?";
        String[] selectionArgs = { FILE_NAME };

        try (Cursor cursor = resolver.query(collection, projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID));
                return ContentUris.withAppendedId(collection, id);
            }
        }
        return null;
    }

    private static void exportLegacy(String content) throws IOException {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs();
        }
        File file = new File(downloadsDir, FILE_NAME);
        try (FileOutputStream fos = new FileOutputStream(file, true)) { // true = append
            fos.write(content.getBytes());
        }
    }
}
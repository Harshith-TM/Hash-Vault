package com.example.hashvault.UI;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hashvault.Model.FileHashModel;
import com.example.hashvault.R;
import com.example.hashvault.Utils.ExportUtils;
import com.example.hashvault.ViewModel.FileHashViewModel;

import java.io.IOException;

public class FileHashFragment extends Fragment implements View.OnClickListener {

    private TextView fileNameDisplay, resultAlgorithmLabel, fileHashResult;
    private Button buttonSelectFile, buttonGenerateFileHash, buttonCopyFileHash, buttonExportFileHash;
    private RadioGroup radioGroupAlgorithm;
    private ProgressBar progressBar;

    private Uri selectedFileUri;
    private String selectedFileName;
    private FileHashViewModel fileHashViewModel;

    private final ActivityResultLauncher<String[]> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri != null) {
                    selectedFileUri = uri;
                    selectedFileName = getFileNameFromUri(uri);
                    fileNameDisplay.setText(selectedFileName);
                    fileHashViewModel.clear();
                    resultAlgorithmLabel.setText("");
                    fileHashResult.setText(R.string.hash_text);
                }
            });

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    performExport();
                } else {
                    Toast.makeText(requireContext(), "Storage permission needed to export", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_hash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fileNameDisplay = view.findViewById(R.id.file_name_display);
        buttonSelectFile = view.findViewById(R.id.button_select_file);
        radioGroupAlgorithm = view.findViewById(R.id.radio_group_algorithm);
        buttonGenerateFileHash = view.findViewById(R.id.button_generate_file_hash);
        progressBar = view.findViewById(R.id.progress_bar);
        resultAlgorithmLabel = view.findViewById(R.id.result_algorithm_label);
        fileHashResult = view.findViewById(R.id.file_hash_result);
        buttonCopyFileHash = view.findViewById(R.id.button_copy_file_hash);
        buttonExportFileHash = view.findViewById(R.id.button_export_file_hash);

        fileHashViewModel = new ViewModelProvider(this).get(FileHashViewModel.class);

        fileHashViewModel.getFileHashData().observe(getViewLifecycleOwner(), model -> {
            if (model != null) {
                resultAlgorithmLabel.setText(model.getAlgorithm());
                fileHashResult.setText(model.getHashValue());
            }
        });

        fileHashViewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            buttonGenerateFileHash.setEnabled(!loading);
        });

        fileHashViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        buttonSelectFile.setOnClickListener(this);
        buttonGenerateFileHash.setOnClickListener(this);
        buttonCopyFileHash.setOnClickListener(this);
        buttonExportFileHash.setOnClickListener(this);
    }

    private String getFileNameFromUri(Uri uri) {
        String name = "Unknown file";
        try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    name = cursor.getString(nameIndex);
                }
            }
        }
        return name;
    }

    private String getSelectedAlgorithm() {
        int checkedId = radioGroupAlgorithm.getCheckedRadioButtonId();
        if (checkedId == R.id.radio_md5) return "MD5";
        if (checkedId == R.id.radio_sha1) return "SHA1";
        if (checkedId == R.id.radio_sha256) return "SHA256";
        if (checkedId == R.id.radio_sha384) return "SHA384";
        if (checkedId == R.id.radio_sha512) return "SHA512";
        return null;
    }

    private void performExport() {
        FileHashModel model = fileHashViewModel.getFileHashData().getValue();
        if (model == null) return;
        try {
            ExportUtils.exportFileHash(requireContext(), model);
            Toast.makeText(requireContext(), "Exported to Downloads/HashVaultExport.txt", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void exportFileHash() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return;
        }
        performExport();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.button_select_file) {
            filePickerLauncher.launch(new String[]{"*/*"});
        } else if (viewId == R.id.button_generate_file_hash) {
            if (selectedFileUri == null) {
                Toast.makeText(requireContext(), "Select a file first", Toast.LENGTH_SHORT).show();
                return;
            }
            String algorithm = getSelectedAlgorithm();
            if (algorithm == null) {
                Toast.makeText(requireContext(), "Select an algorithm first", Toast.LENGTH_SHORT).show();
                return;
            }
            fileHashViewModel.generateFileHash(requireContext(), selectedFileUri, selectedFileName, algorithm);
        } else if (viewId == R.id.button_copy_file_hash) {
            FileHashModel model = fileHashViewModel.getFileHashData().getValue();
            if (model == null) {
                Toast.makeText(requireContext(), "Generate a hash first", Toast.LENGTH_SHORT).show();
                return;
            }
            ClipboardManager manager = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            manager.setPrimaryClip(ClipData.newPlainText(model.getAlgorithm() + " Hash", model.getHashValue()));
            Toast.makeText(requireContext(), model.getAlgorithm() + " Hash Copied!", Toast.LENGTH_SHORT).show();
        } else if (viewId == R.id.button_export_file_hash) {
            if (fileHashViewModel.getFileHashData().getValue() == null) {
                Toast.makeText(requireContext(), "Generate a hash first", Toast.LENGTH_SHORT).show();
                return;
            }
            exportFileHash();
        }
    }
}
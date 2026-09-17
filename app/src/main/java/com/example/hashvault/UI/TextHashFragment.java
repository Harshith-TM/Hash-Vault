package com.example.hashvault.UI;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hashvault.Model.HashMatchResult;
import com.example.hashvault.R;
import com.example.hashvault.Utils.ExportUtils;
import com.example.hashvault.Utils.ThemePreferences;
import com.example.hashvault.ViewModel.HashViewModel;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.IOException;

public class TextHashFragment extends Fragment implements View.OnClickListener {

    Button buttonGenerate, buttonClear, buttonCompare, buttonExport;
    Button buttonCopyMD5, buttonCopySHA1, buttonCopySHA256, buttonCopySHA384, buttonCopySHA512;
    TextView md5_hash, sha1_hash, sha256_hash, sha384_hash, sha512_hash;
    TextView matchStatusMd5, matchStatusSha1, matchStatusSha256, matchStatusSha384, matchStatusSha512;
    EditText inputText, compareInput;
    SwitchMaterial switchDarkMode;
    HashViewModel hashViewModel;

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
        return inflater.inflate(R.layout.fragment_text_hash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);

        hashViewModel = new ViewModelProvider(this).get(HashViewModel.class);
        hashViewModel.getHashData().observe(getViewLifecycleOwner(), hashModel -> {
            if (hashModel != null) {
                md5_hash.setText(hashModel.getMd5Hash());
                sha1_hash.setText(hashModel.getSha1Hash());
                sha256_hash.setText(hashModel.getSha256Hash());
                sha384_hash.setText(hashModel.getSha384Hash());
                sha512_hash.setText(hashModel.getSha512Hash());
            }
        });
        hashViewModel.getMatchResult().observe(getViewLifecycleOwner(), this::updateMatchUI);

        switchDarkMode.setChecked(ThemePreferences.isDarkModeActive(requireContext()));
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int mode = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            ThemePreferences.setSavedMode(requireContext(), mode);
            AppCompatDelegate.setDefaultNightMode(mode);
        });

        buttonGenerate.setOnClickListener(this);
        buttonClear.setOnClickListener(this);
        buttonCompare.setOnClickListener(this);
        buttonExport.setOnClickListener(this);
        buttonCopyMD5.setOnClickListener(this);
        buttonCopySHA1.setOnClickListener(this);
        buttonCopySHA256.setOnClickListener(this);
        buttonCopySHA384.setOnClickListener(this);
        buttonCopySHA512.setOnClickListener(this);
    }

    private void initViews(View view) {
        inputText = view.findViewById(R.id.input_text);
        compareInput = view.findViewById(R.id.compare_input);
        buttonGenerate = view.findViewById(R.id.button_generate);
        buttonClear = view.findViewById(R.id.button_clear);
        buttonCompare = view.findViewById(R.id.button_compare);
        buttonExport = view.findViewById(R.id.button_export);
        switchDarkMode = view.findViewById(R.id.switch_dark_mode);

        md5_hash = view.findViewById(R.id.md5_hash);
        buttonCopyMD5 = view.findViewById(R.id.button_copy_md5);
        matchStatusMd5 = view.findViewById(R.id.match_status_md5);

        sha1_hash = view.findViewById(R.id.sha1_hash);
        buttonCopySHA1 = view.findViewById(R.id.button_copy_sha1);
        matchStatusSha1 = view.findViewById(R.id.match_status_sha1);

        sha256_hash = view.findViewById(R.id.sha256_hash);
        buttonCopySHA256 = view.findViewById(R.id.button_copy_sha256);
        matchStatusSha256 = view.findViewById(R.id.match_status_sha256);

        sha384_hash = view.findViewById(R.id.sha384_hash);
        buttonCopySHA384 = view.findViewById(R.id.button_copy_sha384);
        matchStatusSha384 = view.findViewById(R.id.match_status_sha384);

        sha512_hash = view.findViewById(R.id.sha512_hash);
        buttonCopySHA512 = view.findViewById(R.id.button_copy_sha512);
        matchStatusSha512 = view.findViewById(R.id.match_status_sha512);
    }

    private void updateMatchUI(HashMatchResult result) {
        if (result == null) {
            matchStatusMd5.setVisibility(View.GONE);
            matchStatusSha1.setVisibility(View.GONE);
            matchStatusSha256.setVisibility(View.GONE);
            matchStatusSha384.setVisibility(View.GONE);
            matchStatusSha512.setVisibility(View.GONE);
            return;
        }
        setMatchStatus(matchStatusMd5, result.isMd5Match());
        setMatchStatus(matchStatusSha1, result.isSha1Match());
        setMatchStatus(matchStatusSha256, result.isSha256Match());
        setMatchStatus(matchStatusSha384, result.isSha384Match());
        setMatchStatus(matchStatusSha512, result.isSha512Match());
    }

    private void setMatchStatus(TextView view, boolean isMatch) {
        view.setVisibility(View.VISIBLE);
        if (isMatch) {
            view.setText(R.string.match_text);
            view.setTextColor(ContextCompat.getColor(requireContext(), R.color.match_green));
        } else {
            view.setText(R.string.no_match_text);
            view.setTextColor(ContextCompat.getColor(requireContext(), R.color.mismatch_red));
        }
    }

    private void exportHashes() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return;
        }
        performExport();
    }

    private void performExport() {
        try {
            ExportUtils.exportHashes(requireContext(), inputText.getText().toString(), hashViewModel.getHashData().getValue());
            Toast.makeText(requireContext(), "Exported to Downloads/HashVaultExport.txt", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        ClipboardManager manager = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        String input = inputText.getText().toString();

        if (input.isEmpty() && viewId != R.id.button_clear && viewId != R.id.button_compare) {
            Toast.makeText(requireContext(), "Enter Some Text", Toast.LENGTH_SHORT).show();
            return;
        }

        if (viewId == R.id.button_generate) {
            hashViewModel.generateHashes(input);
            hashViewModel.clearMatchResult();
        } else if (viewId == R.id.button_clear) {
            inputText.setText("");
            compareInput.setText("");
            md5_hash.setText(R.string.hash_text);
            sha1_hash.setText(R.string.hash_text);
            sha256_hash.setText(R.string.hash_text);
            sha384_hash.setText(R.string.hash_text);
            sha512_hash.setText(R.string.hash_text);
            hashViewModel.clearMatchResult();
        } else if (viewId == R.id.button_compare) {
            String compareHash = compareInput.getText().toString();
            if (compareHash.trim().isEmpty()) {
                Toast.makeText(requireContext(), "Enter a hash to compare", Toast.LENGTH_SHORT).show();
                return;
            }
            if (hashViewModel.getHashData().getValue() == null) {
                Toast.makeText(requireContext(), "Generate a hash first", Toast.LENGTH_SHORT).show();
                return;
            }
            hashViewModel.compareHash(compareHash);
        } else if (viewId == R.id.button_export) {
            if (hashViewModel.getHashData().getValue() == null) {
                Toast.makeText(requireContext(), "Generate a hash first", Toast.LENGTH_SHORT).show();
                return;
            }
            exportHashes();
        } else if (viewId == R.id.button_copy_md5) {
            manager.setPrimaryClip(ClipData.newPlainText("MD5 Hash", md5_hash.getText().toString()));
            Toast.makeText(requireContext(), "MD5 Hash Copied!", Toast.LENGTH_SHORT).show();
        } else if (viewId == R.id.button_copy_sha1) {
            manager.setPrimaryClip(ClipData.newPlainText("SHA1 Hash", sha1_hash.getText().toString()));
            Toast.makeText(requireContext(), "SHA1 Hash Copied!", Toast.LENGTH_SHORT).show();
        } else if (viewId == R.id.button_copy_sha256) {
            manager.setPrimaryClip(ClipData.newPlainText("SHA256 Hash", sha256_hash.getText().toString()));
            Toast.makeText(requireContext(), "SHA256 Hash Copied!", Toast.LENGTH_SHORT).show();
        } else if (viewId == R.id.button_copy_sha384) {
            manager.setPrimaryClip(ClipData.newPlainText("SHA384 Hash", sha384_hash.getText().toString()));
            Toast.makeText(requireContext(), "SHA384 Hash Copied!", Toast.LENGTH_SHORT).show();
        } else if (viewId == R.id.button_copy_sha512) {
            manager.setPrimaryClip(ClipData.newPlainText("SHA512 Hash", sha512_hash.getText().toString()));
            Toast.makeText(requireContext(), "SHA512 Hash Copied!", Toast.LENGTH_SHORT).show();
        }
    }
}
package com.example.hashvault.UI;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.hashvault.R;
import com.example.hashvault.Model.HashMatchResult;
import com.example.hashvault.Utils.ExportUtils;
import com.example.hashvault.Utils.ThemePreferences;
import com.example.hashvault.ViewModel.HashViewModel;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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
                    Toast.makeText(this, "Storage permission needed to export", Toast.LENGTH_SHORT).show();
                }
            });

    private void initViews() {
        inputText = findViewById(R.id.input_text);
        compareInput = findViewById(R.id.compare_input);
        buttonGenerate = findViewById(R.id.button_generate);
        buttonClear = findViewById(R.id.button_clear);
        buttonCompare = findViewById(R.id.button_compare);
        buttonExport = findViewById(R.id.button_export);
        switchDarkMode = findViewById(R.id.switch_dark_mode);

        md5_hash = findViewById(R.id.md5_hash);
        buttonCopyMD5 = findViewById(R.id.button_copy_md5);
        matchStatusMd5 = findViewById(R.id.match_status_md5);

        sha1_hash = findViewById(R.id.sha1_hash);
        buttonCopySHA1 = findViewById(R.id.button_copy_sha1);
        matchStatusSha1 = findViewById(R.id.match_status_sha1);

        sha256_hash = findViewById(R.id.sha256_hash);
        buttonCopySHA256 = findViewById(R.id.button_copy_sha256);
        matchStatusSha256 = findViewById(R.id.match_status_sha256);

        sha384_hash = findViewById(R.id.sha384_hash);
        buttonCopySHA384 = findViewById(R.id.button_copy_sha384);
        matchStatusSha384 = findViewById(R.id.match_status_sha384);

        sha512_hash = findViewById(R.id.sha512_hash);
        buttonCopySHA512 = findViewById(R.id.button_copy_sha512);
        matchStatusSha512 = findViewById(R.id.match_status_sha512);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int savedMode = ThemePreferences.getSavedMode(this);
        AppCompatDelegate.setDefaultNightMode(savedMode);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();

        hashViewModel = new ViewModelProvider(this).get(HashViewModel.class);
        hashViewModel.getHashData().observe(this, hashModel -> {
            if (hashModel != null) {
                md5_hash.setText(hashModel.getMd5Hash());
                sha1_hash.setText(hashModel.getSha1Hash());
                sha256_hash.setText(hashModel.getSha256Hash());
                sha384_hash.setText(hashModel.getSha384Hash());
                sha512_hash.setText(hashModel.getSha512Hash());
            }
        });
        hashViewModel.getMatchResult().observe(this, this::updateMatchUI);

        switchDarkMode.setChecked(ThemePreferences.isDarkModeActive(this));
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int mode = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            ThemePreferences.setSavedMode(MainActivity.this, mode);
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
            view.setTextColor(ContextCompat.getColor(this, R.color.match_green));
        } else {
            view.setText(R.string.no_match_text);
            view.setTextColor(ContextCompat.getColor(this, R.color.mismatch_red));
        }
    }

    private void exportHashes() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return;
        }
        performExport();
    }

    private void performExport() {
        try {
            ExportUtils.exportHashes(this, inputText.getText().toString(), hashViewModel.getHashData().getValue());
            Toast.makeText(this, "Exported to Downloads/HashVaultExport.txt", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        String input = inputText.getText().toString();

        if (input.isEmpty() && viewId != R.id.button_clear && viewId != R.id.button_compare) {
            Toast.makeText(this, "Enter Some Text", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "Enter a hash to compare", Toast.LENGTH_SHORT).show();
                return;
            }
            if (hashViewModel.getHashData().getValue() == null) {
                Toast.makeText(this, "Generate a hash first", Toast.LENGTH_SHORT).show();
                return;
            }
            hashViewModel.compareHash(compareHash);
        } else if (viewId == R.id.button_export) {
            if (hashViewModel.getHashData().getValue() == null) {
                Toast.makeText(this, "Generate a hash first", Toast.LENGTH_SHORT).show();
                return;
            }
            exportHashes();
        } else if (viewId == R.id.button_copy_md5) {
            manager.setPrimaryClip(ClipData.newPlainText("MD5 Hash", md5_hash.getText().toString()));
            Toast.makeText(this, "MD5 Hash Copied!", Toast.LENGTH_SHORT).show();
        } else if (viewId == R.id.button_copy_sha1) {
            manager.setPrimaryClip(ClipData.newPlainText("SHA1 Hash", sha1_hash.getText().toString()));
            Toast.makeText(this, "SHA1 Hash Copied!", Toast.LENGTH_SHORT).show();
        } else if (viewId == R.id.button_copy_sha256) {
            manager.setPrimaryClip(ClipData.newPlainText("SHA256 Hash", sha256_hash.getText().toString()));
            Toast.makeText(this, "SHA256 Hash Copied!", Toast.LENGTH_SHORT).show();
        } else if (viewId == R.id.button_copy_sha384) {
            manager.setPrimaryClip(ClipData.newPlainText("SHA384 Hash", sha384_hash.getText().toString()));
            Toast.makeText(this, "SHA384 Hash Copied!", Toast.LENGTH_SHORT).show();
        } else if (viewId == R.id.button_copy_sha512) {
            manager.setPrimaryClip(ClipData.newPlainText("SHA512 Hash", sha512_hash.getText().toString()));
            Toast.makeText(this, "SHA512 Hash Copied!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        View view = getCurrentFocus();
        if (view instanceof EditText) {
            Rect outRect = new Rect();
            view.getGlobalVisibleRect(outRect);
            if (motionEvent != null) {
                if (!outRect.contains((int) motionEvent.getRawX(), (int) motionEvent.getRawY())) {
                    view.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(motionEvent);
    }
}
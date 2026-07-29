package com.example.hashvault.UI;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.hashvault.R;
import com.example.hashvault.ViewModel.HashViewModel;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonGenerate, buttonClear, buttonCopyMD5, buttonCopySHA1, buttonCopySHA256;
    TextView md5_hash, sha1_hash, sha256_hash;
    EditText inputText;
    HashViewModel hashViewModel;

    private void initViews() {
        inputText = findViewById(R.id.input_text);
        buttonGenerate = findViewById(R.id.button_generate);
        buttonClear = findViewById(R.id.button_clear);
        md5_hash = findViewById(R.id.md5_hash);
        buttonCopyMD5 = findViewById(R.id.button_copy_md5);
        sha1_hash = findViewById(R.id.sha1_hash);
        buttonCopySHA1 = findViewById(R.id.button_copy_sha1);
        sha256_hash = findViewById(R.id.sha256_hash);
        buttonCopySHA256 = findViewById(R.id.button_copy_sha256);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            }
        });

        buttonGenerate.setOnClickListener(this);
        buttonClear.setOnClickListener(this);
        buttonCopyMD5.setOnClickListener(this);
        buttonCopySHA1.setOnClickListener(this);
        buttonCopySHA256.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        String input = inputText.getText().toString();
        if (input.isEmpty() && viewId != R.id.button_clear) {
            Toast.makeText(this, "Enter Some Text", Toast.LENGTH_SHORT).show();
            return;
        }
        if (viewId == R.id.button_generate) {
            hashViewModel.generateHashes(input);
        } else if (viewId == R.id.button_clear) {
            inputText.setText("");
            md5_hash.setText(R.string.hash_text);
            sha1_hash.setText(R.string.hash_text);
            sha256_hash.setText(R.string.hash_text);
        } else if (viewId == R.id.button_copy_md5) {
            manager.setPrimaryClip(ClipData.newPlainText("MD5 Hash", md5_hash.getText().toString()));
            Toast.makeText(this, "MD5 Hash Copied!", Toast.LENGTH_SHORT).show();
        } else if (viewId == R.id.button_copy_sha1) {
            manager.setPrimaryClip(ClipData.newPlainText("SHA1 Hash", sha1_hash.getText().toString()));
            Toast.makeText(this, "SHA1 Hash Copied!", Toast.LENGTH_SHORT).show();
        } else if (viewId == R.id.button_copy_sha256) {
            manager.setPrimaryClip(ClipData.newPlainText("SHA256 Hash", sha256_hash.getText().toString()));
            Toast.makeText(this, "SHA256 Hash Copied!", Toast.LENGTH_SHORT).show();
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
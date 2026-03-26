package com.tipie.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText edtInput;
    private ProgressBar progressBar;
    private TextView txtPercent;
    private Button btnDoIt;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ các view
        edtInput = findViewById(R.id.edtInput);
        progressBar = findViewById(R.id.progressBar);
        txtPercent = findViewById(R.id.txtPercent);
        btnDoIt = findViewById(R.id.btnDoIt);

        btnDoIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProgress();
            }
        });
    }

    private void startProgress() {
        String inputStr = edtInput.getText().toString();
        if (inputStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số!", Toast.LENGTH_SHORT).show();
            return;
        }

        final int totalSteps = Integer.parseInt(inputStr);
        if (totalSteps <= 0) return;

        // Disable nút bấm và reset progress
        btnDoIt.setEnabled(false);
        btnDoIt.setAlpha(0.5f); // Làm mờ nút
        edtInput.setEnabled(false);
        edtInput.setAlpha(0.5f);
        progressBar.setProgress(0);
        txtPercent.setText("0%");

        // Tạo Thread phụ để xử lý
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i <= totalSteps; i++) {
                    try {
                        // Sleep 1ms
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Tính toán phần trăm
                    final int progress = (int) ((i * 100.0) / totalSteps);
                    final int currentStep = i;

                    // Cập nhật giao diện thông qua Handler
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progress);
                            txtPercent.setText(progress + "%");

                            // Khi hoàn thành 100%
                            if (currentStep == totalSteps) {
                                btnDoIt.setEnabled(true);
                                btnDoIt.setAlpha(1.0f); // Sáng lại nút
                                edtInput.setEnabled(true);
                                edtInput.setAlpha(1.0f); // Sáng lại nút
                            }
                        }
                    });
                }
            }
        }).start();
    }
}
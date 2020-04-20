package com.bgenterprise.bglmtcinventory;

/**
 * Displays the buttons for entering and viewing all receipts.
 */

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ReceiptsPage extends AppCompatActivity {
    SharedPreferences QRPrefs;
    Button btnEnterReceipt, btnViewReceipts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipts);
        QRPrefs = getSharedPreferences("QRPreferences", MODE_PRIVATE);
        btnEnterReceipt = findViewById(R.id.btnEnterReceipt);
        btnViewReceipts = findViewById(R.id.btnViewReceipts);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ReceiptsPage.this, new String[]{Manifest.permission.CAMERA}, 20);
        }

        btnEnterReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRPrefs.edit().putString("required", "LMD_Receipt").commit();
                QRPrefs.edit().putString("scanner_title", "Scan LMD Details").commit();
                Intent intent = new Intent(ReceiptsPage.this, ScannerActivity.class);
                startActivity(intent);

            }
        });

        btnViewReceipts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReceiptsPage.this, View_All_Receipts.class));
            }
        });
    }
}

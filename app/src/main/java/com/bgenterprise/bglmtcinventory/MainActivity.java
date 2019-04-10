package com.bgenterprise.bglmtcinventory;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button openAcButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openAcButton = findViewById(R.id.btnOpenAccControl);

    }

    public void openAccessControl(View view) {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setComponent(new ComponentName("com.babbangona.accesscontrol", "com.babbangona.accesscontrol.MainActivity"));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "install access control and try again", Toast.LENGTH_SHORT).show();
        }

    }
}

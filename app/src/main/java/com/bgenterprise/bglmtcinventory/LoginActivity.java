package com.bgenterprise.bglmtcinventory;

/**
 * Login activity.
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class LoginActivity extends AppCompatActivity {
    EditText etStaffName, etStaffID;
    Button btnLogin;
    SessionManager session;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        File dir = new File(Environment.getExternalStorageDirectory().getPath(), "Exports");
        try {
            if (!dir.exists()) {
                dir.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        session = new SessionManager(LoginActivity.this);

        btnLogin = findViewById(R.id.btnLogin);
        etStaffID = findViewById(R.id.etStaffID);
        etStaffName = findViewById(R.id.etStaffName);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                session.CREATE_LOGIN_SESSION(etStaffName.getText().toString(), etStaffID.getText().toString(), BuildConfig.VERSION_NAME);

                Intent intent = new Intent(LoginActivity.this, Operations.class);
                startActivity(intent);
            }
        });

    }
}

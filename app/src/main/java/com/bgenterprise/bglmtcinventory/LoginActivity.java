package com.bgenterprise.bglmtcinventory;

/**
 * Login activity.
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    EditText etStaffName, etStaffID;
    Button btnLogin;
    SessionManager session;
    String appVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new SessionManager(LoginActivity.this);

        btnLogin = findViewById(R.id.btnLogin);
        etStaffID = findViewById(R.id.etStaffID);
        etStaffName = findViewById(R.id.etStaffName);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                appVersion = "v5.0.4";
                session.CREATE_LOGIN_SESSION(etStaffName.getText().toString(), etStaffID.getText().toString(), appVersion);

                Intent intent = new Intent(LoginActivity.this, Operations.class);
                startActivity(intent);
            }
        });

    }
}

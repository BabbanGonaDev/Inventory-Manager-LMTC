package com.bgenterprise.bglmtcinventory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class Main2Activity extends AppCompatActivity {

    String staff_name, staff_id;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        session = new SessionManager(Main2Activity.this);

        try {
            Intent intent = getIntent();
            Bundle b = intent.getExtras();
            staff_name = (String) b.get("staff_name");
            staff_id = (String) b.get("staff_id");
            session.CREATE_LOGIN_SESSION(staff_name, staff_id, BuildConfig.VERSION_NAME);

        } catch (Exception e) {
            Log.d("HERE", "" + staff_id);
        }

        startActivity(new Intent(Main2Activity.this, Operations.class));

    }
}

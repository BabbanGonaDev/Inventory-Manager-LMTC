package com.bgenterprise.bglmtcinventory;
/**
 * Displays buttons for both entry and viewing of teller activities.
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class TellerPage extends AppCompatActivity {

    Button btnEnterTeller, btnViewTellers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teller_page);

        btnEnterTeller = findViewById(R.id.btnEnterTeller);
        btnViewTellers = findViewById(R.id.btnViewTellers);

        btnEnterTeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TellerPage.this, NewTeller.class));
            }
        });

        btnViewTellers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TellerPage.this, View_All_Tellers.class));
            }
        });
    }
}

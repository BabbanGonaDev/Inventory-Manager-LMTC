package com.bgenterprise.bglmtcinventory;
/**
 * Activity that shows the buttons for viewing and generating invoice.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class InvoicesPage extends AppCompatActivity {

    Button btnGenerateInvoice, btnViewAllInvoices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoices);

        btnGenerateInvoice = findViewById(R.id.btnGenerateInvoice);
        btnViewAllInvoices = findViewById(R.id.btnViewAllInvoices);


        btnGenerateInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(InvoicesPage.this, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
                        .setIcon(R.drawable.ic_error_outline_red_24dp)
                        .setTitle("NOTE")
                        .setMessage("You can only generate an invoice when you do a stock count at the LMD Store.")
                        .setPositiveButton("OK, Got It", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        /*btnViewAllInvoices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InvoicesPage.this, View_All_Invoices.class));
            }
        });*/

    }
}

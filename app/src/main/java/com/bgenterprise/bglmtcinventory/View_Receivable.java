package com.bgenterprise.bglmtcinventory;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.DecimalFormat;
import java.util.HashMap;

public class View_Receivable extends AppCompatActivity {

    SessionManager session;
    SharedPreferences QRPrefs;
    HashMap<String, String> all_details;
    InvoiceDBHandler invoiceDB;
    ReceiptDBHandler receiptDB;
    String LMD_ID;
    TextView tvLMDID, tvinvoice_value_today, tvcurrent_receivable, tvtotal_receivable;
    Double invoiceSum, receiptSum, latestInvoice, totalReceivable, currentReceivable;
    Button btnHome, btnMakePayment, btnViewInvoices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__receiveable);

        tvLMDID = findViewById(R.id.tvLMDID);
        tvinvoice_value_today = findViewById(R.id.tvinvoice_value_today);
        tvcurrent_receivable = findViewById(R.id.tvcurrent_receivable);
        tvtotal_receivable = findViewById(R.id.tvtotal_receivable);
        btnHome = findViewById(R.id.btnHome);
        btnMakePayment = findViewById(R.id.btnMakePayment);
        btnViewInvoices = findViewById(R.id.btnViewInvoices);

        invoiceDB = new InvoiceDBHandler(View_Receivable.this);
        receiptDB = new ReceiptDBHandler(View_Receivable.this);
        QRPrefs = getSharedPreferences("QRPreferences", MODE_PRIVATE);
        session = new SessionManager(View_Receivable.this);
        all_details = session.getAllDetails();

        try{
            LMD_ID = all_details.get(SessionManager.KEY_LMD_ID);

            if(LMD_ID.isEmpty()){
                //Redirect to the Scanner Activity to scan the LMD QR Code.
                QRPrefs.edit().putString("required", "LMD_Receivable").commit();
                QRPrefs.edit().putString("scanner_title", "Scan LMD Details").commit();
                startActivity(new Intent(View_Receivable.this, ScannerActivity.class));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        try {
            //Get the sum of invoices.
            invoiceSum = invoiceDB.getInvoiceSum(LMD_ID);

            //Get the sum of receipts.
            receiptSum = receiptDB.getSumReceipts(LMD_ID);
            Log.d("CHECK", "Receipt Sum: " + String.valueOf(receiptSum));

            //Invoice - Receipt = Receivable.
            totalReceivable = invoiceSum - receiptSum;

            //Get the latest invoice.
            latestInvoice = invoiceDB.latestInvoice(LMD_ID);

            //Current receivable till today = (total receivable - today's invoice)
            currentReceivable = totalReceivable - latestInvoice;
        }catch(Exception e){
            e.printStackTrace();
        }

        DecimalFormat myFormat = new DecimalFormat("#########.###");

        try {
            tvLMDID.setText("LMD ID: " + LMD_ID + " Receivables.");
            tvinvoice_value_today.setText("Total Invoice Value till today: NGN " + myFormat.format(latestInvoice));
            tvcurrent_receivable.setText("Previous Receivable Amount till today: NGN " + myFormat.format(currentReceivable));
            tvtotal_receivable.setText("Total Receivables Now: NGN " + myFormat.format(totalReceivable));

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome(v);
            }
        });

        btnMakePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(View_Receivable.this, NewReceipt.class));
            }
        });

        btnViewInvoices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(View_Receivable.this, View_All_Invoices.class);
                intent.putExtra("KEY_PREVIOUS_ACTIVITY", "View_Receivable");
                startActivity(intent);
            }
        });

    }

    public void goHome(View view){

        new MaterialAlertDialogBuilder(View_Receivable.this, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
                .setTitle("Confirm Action")
                .setMessage("Are you sure you want to go home without collecting any payment ?")
                .setPositiveButton("Yes, Go Home", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        session.CLEAR_LMD_DETAILS();
                        finish();
                        startActivity(new Intent(View_Receivable.this, Operations.class));
                    }
                })
                .setNegativeButton("No, Wait.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }


}

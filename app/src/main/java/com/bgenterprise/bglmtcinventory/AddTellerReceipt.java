package com.bgenterprise.bglmtcinventory;

/**
  This is the activity that handles the adding of the receipts attached to each teller
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.util.HashMap;

public class AddTellerReceipt extends AppCompatActivity {
    Button btnScanReceiptID, btnSubmitReceiptDetails, btnHome;
    MaterialTextView tv_receipt_id;
    EditText et_receipt_amount1, et_receipt_amount2;
    SharedPreferences QRPrefs;
    SessionManager session;
    HashMap<String, String> allDetails;
    TellerDBHandler tellerDBHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teller_receipt);
        QRPrefs = getSharedPreferences("QRPreferences", MODE_PRIVATE);
        session = new SessionManager(AddTellerReceipt.this);
        allDetails = session.getAllDetails();
        tellerDBHandler = new TellerDBHandler(AddTellerReceipt.this);

        btnScanReceiptID = findViewById(R.id.btnScanReceiptID);
        btnHome = findViewById(R.id.btnHome);
        btnSubmitReceiptDetails = findViewById(R.id.btnSubmitReceiptDetails);
        tv_receipt_id = findViewById(R.id.tv_receipt_id);
        et_receipt_amount1 = findViewById(R.id.et_receipt_amount1);
        et_receipt_amount2 = findViewById(R.id.et_receipt_amount2);

        Intent starterIntent = getIntent();

        /*//Check for total teller amount.
        DetectTotalAmount();*/

        tv_receipt_id.setText(allDetails.get(SessionManager.KEY_RECEIPT_ID));

        //All Onclick Listeners (Actions carried out when a button is clicked).
        btnScanReceiptID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonScanReceiptID(v);
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(AddTellerReceipt.this, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
                        .setTitle("Confirm Action")
                        .setMessage("Are you sure you want to go home without collecting any payment ?")
                        .setPositiveButton("Yes, Go Home", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                session.CLEAR_TELLER_DETAILS();
                                session.CLEAR_TELLER_RECEIPT_DETAILS();
                                finish();
                                startActivity(new Intent(AddTellerReceipt.this, Operations.class));
                            }
                        })
                        .setNegativeButton("No, Wait.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        btnSubmitReceiptDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonSubmit(v);
            }
        });

    }

    public void ButtonScanReceiptID(View view){
        /*
        Calls the scanner activity to scan the receipt ID
         */
        QRPrefs.edit().putString("required", "Teller_Receipt").commit();
        QRPrefs.edit().putString("scanner_title", "Scan Receipt Number").commit();
        startActivity(new Intent(AddTellerReceipt.this, ScannerActivity.class));
    }

    public void ButtonSubmit(View view){
        /*
        Submits all the entered details for the particular receipt.
         */

        if(!tv_receipt_id.getText().toString().isEmpty() && !et_receipt_amount1.getText().toString().isEmpty() && !et_receipt_amount2.getText().toString().isEmpty() &&
                et_receipt_amount1.getText().toString().equals(et_receipt_amount2.getText().toString())
                ){
            Log.d("HERE", "onclick reached");

            final String UniqueIDForTeller = allDetails.get(SessionManager.KEY_STAFF_ID) + "_" + String.valueOf(System.currentTimeMillis()) + "_TELLER";

            //Add the dialog display here to confirm the amount entered.
            new MaterialAlertDialogBuilder(AddTellerReceipt.this, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
                    .setTitle("Confirm Receipt Details")
                    .setMessage("Are you sure you want to add Receipt: " + tv_receipt_id.getText().toString() + " of " + et_receipt_amount1.getText().toString() + "to this teller ?")
                    .setPositiveButton("Yes, Add Receipt", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //Insert into the Teller Table.
                            if(tellerDBHandler.onAdd(allDetails.get(SessionManager.KEY_TELLER_ID), allDetails.get(SessionManager.KEY_TELLER_AMOUNT), allDetails.get(SessionManager.KEY_TELLER_BANK),
                                    tv_receipt_id.getText().toString(), et_receipt_amount2.getText().toString(), allDetails.get(SessionManager.KEY_TELLER_DATE),
                                    "no", allDetails.get(SessionManager.KEY_APP_VERSION), allDetails.get(SessionManager.KEY_STAFF_ID), UniqueIDForTeller)) {

                                Toast.makeText(AddTellerReceipt.this, "Receipt " + tv_receipt_id.getText().toString() + " added successfully.", Toast.LENGTH_LONG).show();
                                new MaterialAlertDialogBuilder(AddTellerReceipt.this, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
                                        .setTitle("Confirm Action")
                                        .setMessage("Do you still want to enter another receipt for this Teller ?")
                                        .setPositiveButton("YES, Enter Another Receipt", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                session.CLEAR_TELLER_RECEIPT_DETAILS();
                                                et_receipt_amount1.setText("");
                                                et_receipt_amount2.setText("");
                                                recreate();
                                            }
                                        })
                                        .setNegativeButton("NO, Go Home", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                session.CLEAR_TELLER_RECEIPT_DETAILS();
                                                session.CLEAR_TELLER_DETAILS();
                                                startActivity(new Intent(AddTellerReceipt.this, Operations.class));
                                            }
                                        }).show();
                            }

                        }
                    })
                    .setNegativeButton("NO, Go Home", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();

        }else{
            Toast.makeText(AddTellerReceipt.this, "Kindly Check Details Again", Toast.LENGTH_LONG).show();
            et_receipt_amount1.setText("");
            et_receipt_amount2.setText("");
        }

    }

    @Override
    public void onBackPressed(){
        //Disable the back button.
        Toast.makeText(AddTellerReceipt.this, "Back Button Disabled", Toast.LENGTH_LONG).show();
    }

    public void DetectTotalAmount(){
        //The purpose of this function is to monitor and detect when the total amount for the teller has been entered.
        Double tellerAmount = 0.00;

        try{
            tellerAmount = Double.parseDouble(allDetails.get(SessionManager.KEY_TELLER_AMOUNT));
        }catch(Exception e){
            e.printStackTrace();
        }

        if(tellerDBHandler.checkTotalTellerAmount(allDetails.get(SessionManager.KEY_TELLER_ID), tellerAmount)){
            //Total amount detected.
            new MaterialAlertDialogBuilder(AddTellerReceipt.this, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
                    .setTitle("Teller Amount Complete")
                    .setMessage("Total amount has been detected, click OK to finish receipt entry.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            session.CLEAR_TELLER_RECEIPT_DETAILS();
                            session.CLEAR_TELLER_DETAILS();
                            startActivity(new Intent(AddTellerReceipt.this, Operations.class));
                        }
                    }).show();
        }
    }

    @Override
    public void recreate() {
        super.recreate();
    }

}

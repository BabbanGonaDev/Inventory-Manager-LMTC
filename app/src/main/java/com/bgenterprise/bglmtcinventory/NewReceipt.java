package com.bgenterprise.bglmtcinventory;
/**
 * Activity for entering a new receipt.
 */

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;

public class NewReceipt extends AppCompatActivity {
    TextView tvLMDName, tvDate1, tvDate2, tvReceiptDetails, tvFOD;
    EditText etAmount1, etAmount2;
    Button btnDate1, btnDate2, btnScanReceipt, btnScanFOD, btnUndo, btnHome, btnSubmit;
    SessionManager session;
    SharedPreferences QRPrefs;
    HashMap<String, String> allDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_receipt);
        session = new SessionManager(NewReceipt.this);
        QRPrefs = getSharedPreferences("QRPreferences", MODE_PRIVATE);
        allDetails = session.getAllDetails();

        tvLMDName = findViewById(R.id.tvLMDName);
        tvDate1 = findViewById(R.id.tvDate1);
        tvDate2 = findViewById(R.id.tvDate2);
        tvReceiptDetails = findViewById(R.id.tvReceiptDetails);
        tvFOD = findViewById(R.id.tvFOD);
        etAmount1 = findViewById(R.id.etAmount1);
        etAmount2 = findViewById(R.id.etAmount2);
        btnDate1 = findViewById(R.id.btnDate1);
        btnDate2 = findViewById(R.id.btnDate2);
        btnScanReceipt = findViewById(R.id.btnScanReceipt);
        btnScanFOD = findViewById(R.id.btnScanFOD);
        btnUndo = findViewById(R.id.btnUndo);
        btnHome = findViewById(R.id.btnHome);
        btnSubmit = findViewById(R.id.btnSubmit);

        tvReceiptDetails.setText(allDetails.get(SessionManager.KEY_RECEIPT_ID));
        tvDate1.setText(allDetails.get(SessionManager.KEY_DATE_1));
        tvDate2.setText(allDetails.get(SessionManager.KEY_DATE_1));
        tvLMDName.setText(allDetails.get(SessionManager.KEY_LMD_NAME));
        tvFOD.setText(allDetails.get(SessionManager.KEY_TEAM_MEMBER_NAME));

        //All Onclick Listeners.
        btnDate1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonDate1(v);
            }
        });
        btnDate2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonDate2(v);
            }
        });
        btnScanReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonReceipt(v);

            }
        });
        btnScanFOD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonFOD(v);
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonSubmit(v);
            }
        });
        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonUndo(v);
            }
        });
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonHome(v);
            }
        });



    }

    public void ButtonDate1(View view){
        //Creates a date picker dialog for the receipt.
        Calendar c = Calendar.getInstance();
        final int y = c.get(Calendar.YEAR);
        final int m = c.get(Calendar.MONTH);
        final int d = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(NewReceipt.this, android.R.style.Theme_Holo_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if((year > y) || ((year <= y) && (month > m)) || ((year <= y)&&(month <= m) && (dayOfMonth > d))){
                    Toast.makeText(NewReceipt.this, "Kindly enter a valid date", Toast.LENGTH_LONG).show();
                }else{
                    tvDate1.setText(year + "-" + String.format("%02d", month + 1) + "-" + dayOfMonth);
                }
            }
        }, y,m,d);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void ButtonDate2(View view){
        //Creates the 2nd date picker dialog for the receipt.
        Calendar c = Calendar.getInstance();
        final int y = c.get(Calendar.YEAR);
        final int m = c.get(Calendar.MONTH);
        final int d = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(NewReceipt.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //checks for the validity of the date
                if((year > y) || ((year <= y)&&(month > m)) || ((year <= y)&&(month <= m)&&(dayOfMonth > d)))
                {
                    Toast.makeText(NewReceipt.this,"Please enter a valid date", Toast.LENGTH_LONG).show();
                }
                else
                {
                   tvDate2.setText(year + "-" + String.format("%02d", month + 1) + "-" + dayOfMonth);

                    if(tvDate1.getText().toString().equals(tvDate2.getText().toString())){
                        session.CREATE_DATE(tvDate2.getText().toString());
                    }else{
                        Toast.makeText(NewReceipt.this, "Kindly Confirm Dates again", Toast.LENGTH_LONG).show();
                        tvDate1.setText("");
                        tvDate2.setText("");
                    }
                }
            }
        }, y,m,d);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    public void ButtonReceipt(View view){
        //Calls the scanner class to scan the Receipt ID.
        QRPrefs.edit().putString("required", "Receipt").commit();
        QRPrefs.edit().putString("scanner_title", "Scan Receipt ID").commit();
        Intent intent = new Intent(NewReceipt.this, ScannerActivity.class);
        startActivity(intent);
    }

    public void ButtonFOD(View view){
        //Calls the scanner class to scan the QR of the FOD collecting the money.
        QRPrefs.edit().putString("required", "TeamMember_Receipt").commit();
        QRPrefs.edit().putString("scanner_title", "Scan Team Member").commit();
        Intent intent = new Intent(NewReceipt.this, ScannerActivity.class);
        startActivity(intent);
    }

    public void ButtonSubmit(View view){
        //Confirms and then submits entered data into the database.

        if(tvReceiptDetails.getText().toString().isEmpty() || tvFOD.getText().toString().isEmpty() || tvDate1.getText().toString().isEmpty() ||
               tvLMDName.getText().toString().isEmpty() || etAmount1.getText().toString().isEmpty() || (!etAmount1.getText().toString().equals(etAmount2.getText().toString()))){

            etAmount1.setText("");
            etAmount2.setText("");
            Toast.makeText(NewReceipt.this, "Kindly Confirm Receipt Details Again", Toast.LENGTH_LONG).show();

        }else{
            new AlertDialog.Builder(NewReceipt.this)
                    .setTitle("Confirm Receipt Entry")
                    .setMessage("Are you sure you want to enter this receipt ?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            ReceiptDBHandler receiptDBHandler = new ReceiptDBHandler(NewReceipt.this);
                            if(receiptDBHandler.onAdd(tvReceiptDetails.getText().toString(), tvLMDName.getText().toString(), allDetails.get(SessionManager.KEY_LMD_ID), allDetails.get(SessionManager.KEY_LMD_HUB), etAmount1.getText().toString(),
                                    tvFOD.getText().toString(), allDetails.get(SessionManager.KEY_STAFF_ID), allDetails.get(SessionManager.KEY_DATE_1), "no", allDetails.get(SessionManager.KEY_APP_VERSION))) {
                                Toast.makeText(NewReceipt.this, "Receipt Saved Successfully", Toast.LENGTH_LONG).show();

                                session.CLEAR_LMD_DETAILS();
                                session.CLEAR_RECEIPT_DETAILS();
                                tvDate1.setText("");
                                tvDate2.setText("");
                                getIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(new Intent(NewReceipt.this, Operations.class));

                            }else{
                                Toast.makeText(NewReceipt.this, "Error: Have you entered this receipt before ?", Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).show();
        }
    }

    public void ButtonUndo(View view){
        //Clears all entries.
        session.CLEAR_RECEIPT_DETAILS();
        etAmount1.setText("");
        etAmount2.setText("");
        recreate();
    }

    public void ButtonHome(View view){
        //Returns back to home.
        session.CLEAR_RECEIPT_DETAILS();
        session.CLEAR_LMD_DETAILS();
        startActivity(new Intent(NewReceipt.this, Operations.class));
        getIntent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }


    @Override
    public void onBackPressed(){
        //Disable back press.
        Toast.makeText(getApplicationContext(), "Back button is disabled", Toast.LENGTH_LONG).show();
    }
}

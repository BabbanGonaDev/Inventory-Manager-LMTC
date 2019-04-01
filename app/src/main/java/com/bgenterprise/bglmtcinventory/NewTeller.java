package com.bgenterprise.bglmtcinventory;
/**
 * Activity for entering the details of a new teller.
 */

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class NewTeller extends AppCompatActivity {

    AutoCompleteTextView atv_bank;
    EditText et_teller_id, et_teller_amount;
    TextView tv_teller_date;
    Button btn_teller_date;
    Button btnNewReceipt;
    SharedPreferences QRPrefs;
    SessionManager session;
    String[] Banks = {"Access Bank Plc",
            "Citibank Nigeria Limited",
            "Diamond Bank Plc",
            "Ecobank Nigeria Plc",
            "Fidelity Bank Plc",
            "First City Monument Bank Plc",
            "Guaranty Trust Bank Plc",
            "Key Stone Bank",
            "Polaris Bank",
            "Stanbic IBTC Bank Ltd",
            "Standard Chartered Bank Nigeria Ltd",
            "Sterling Bank Plc",
            "SunTrust Bank Nigeria Limited",
            "Union Bank of Nigeria Plc",
            "United Bank For Africa Plc",
            "Unity Bank Plc",
            "Wema Bank Plc",
            "Zenith Bank Plc",
            "Heritage Banking Company Ltd",
            "FIRST BANK NIGERIA LIMITED",
            "Providus Bank"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_teller);

        QRPrefs = getSharedPreferences("QRPreferences", MODE_PRIVATE);
        tv_teller_date = findViewById(R.id.tv_teller_date);
        btn_teller_date = findViewById(R.id.btn_teller_date);
        atv_bank = findViewById(R.id.atv_bank);
        et_teller_id = findViewById(R.id.et_teller_id);
        et_teller_amount = findViewById(R.id.et_teller_amount);
        btnNewReceipt = findViewById(R.id.btnNewReceipt);
        session = new SessionManager(NewTeller.this);

        ArrayAdapter adapter = new ArrayAdapter(NewTeller.this, android.R.layout.simple_list_item_1, Banks);
        atv_bank.setAdapter(adapter);
        atv_bank.setThreshold(1);

        btn_teller_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                    Displays a date picker for the transaction.
                */
                Calendar c = Calendar.getInstance();
                final int y = c.get(Calendar.YEAR);
                final int m = c.get(Calendar.MONTH);
                final int d = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(NewTeller.this, android.R.style.Theme_Holo_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        if((year > y) || ((year <= y) && (month > m)) || ((year <= y)&&(month <= m) && (dayOfMonth > d))){
                            Toast.makeText(NewTeller.this, "Kindly enter a valid date", Toast.LENGTH_LONG).show();
                        }else{
                            tv_teller_date.setText(year + "-" + String.format("%02d", month + 1) + "-" + dayOfMonth);
                        }
                    }
                }, y,m,d);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        btnNewReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!atv_bank.getText().toString().isEmpty() && !et_teller_id.getText().toString().isEmpty() && !et_teller_amount.getText().toString().isEmpty() && !tv_teller_date.getText().toString().isEmpty()){

                    session.CLEAR_TELLER_RECEIPT_DETAILS();
                    session.CREATE_TELLER_SESSION(et_teller_id.getText().toString(), et_teller_amount.getText().toString(), atv_bank.getText().toString(), tv_teller_date.getText().toString());
                    startActivity(new Intent(NewTeller.this, AddTellerReceipt.class));

                }else{
                    Toast.makeText(NewTeller.this, "Kindly fill in all details", Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}

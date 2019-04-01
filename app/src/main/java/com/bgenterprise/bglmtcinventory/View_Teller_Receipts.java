package com.bgenterprise.bglmtcinventory;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class View_Teller_Receipts extends AppCompatActivity {

    private RecyclerView rv_receipts;
    TextView tv_teller_id, tv_current_receipt_amount, tv_reconciliation_status;
    Button btn_add_receipt;
    private List<Tellers> tellersList;
    private TellerReceiptAdapter tellerReceiptAdapter;
    String teller_id, teller_bank, teller_amount, teller_date;
    Integer current_amount;
    SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__teller__receipts);
        session = new SessionManager(View_Teller_Receipts.this);

        rv_receipts = findViewById(R.id.rv_receipts);
        tv_teller_id = findViewById(R.id.tv_teller_id);
        tv_current_receipt_amount = findViewById(R.id.tv_current_receipt_amount);
        tv_reconciliation_status = findViewById(R.id.tv_reconciliation_status);
        btn_add_receipt = findViewById(R.id.btn_add_receipt);

        //get intents.
        teller_id = getIntent().getStringExtra("tellerID");
        teller_bank = getIntent().getStringExtra("tellerBank");
        teller_amount = getIntent().getStringExtra("tellerAmount");
        teller_date = getIntent().getStringExtra("tellerDate");

        Log.d("CHECK", "ID: " + teller_id);
        Log.d("CHECK", "Bank: " + teller_bank);
        Log.d("CHECK", "Amount: " + teller_amount);
        Log.d("CHECK", "Date: " + teller_date);

        tellersList = new ArrayList<>();
        tv_teller_id.setText("Teller ID: " + teller_id + " Receipts.");

        TellerDBHandler tellerDBHandler = new TellerDBHandler(View_Teller_Receipts.this);
        tellersList = tellerDBHandler.getTellerReceipts(teller_id);

        current_amount = tellerDBHandler.getTotalReceiptAmount(teller_id);
        tv_current_receipt_amount.setText("Current Amount: " + current_amount );

        if(Integer.valueOf(current_amount).equals(Integer.valueOf(teller_amount))){
            tv_reconciliation_status.setBackgroundColor(Color.GREEN);
            tv_reconciliation_status.setTextColor(Color.WHITE);
            tv_reconciliation_status.setText("RECONCILED");
            btn_add_receipt.setEnabled(false);
            btn_add_receipt.setBackgroundColor(Color.LTGRAY);
        }else if(!Integer.valueOf(current_amount).equals(Integer.valueOf(teller_amount))){
            tv_reconciliation_status.setBackgroundColor(Color.RED);
            tv_reconciliation_status.setTextColor(Color.WHITE);
            tv_reconciliation_status.setText("PENDING");
        }

        tellerReceiptAdapter = new TellerReceiptAdapter(getApplicationContext(), tellersList, new TellerReceiptAdapter.OnItemClickListener() {
            @Override
            public void onClick(Tellers tellers) {
                //If we need to add an onclick later.
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rv_receipts.setLayoutManager(layoutManager);
        rv_receipts.setItemAnimator(new DefaultItemAnimator());
        rv_receipts.setAdapter(tellerReceiptAdapter);


        btn_add_receipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddExtraReceipt(v);
            }
        });


    }


    public void AddExtraReceipt(View view){

        session.CLEAR_TELLER_RECEIPT_DETAILS();
        session.CREATE_TELLER_SESSION(teller_id, teller_amount, teller_bank, teller_date);
        startActivity(new Intent(View_Teller_Receipts.this, AddTellerReceipt.class));

    }
}

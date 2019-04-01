package com.bgenterprise.bglmtcinventory;

/**
 * Activity that displays all receipts for a specific LMD.
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class View_LMD_Receipts extends AppCompatActivity {
    private RecyclerView lmd_recycler;
    private List<Receipts> LMDReceiptList;
    private LMDReceiptsAdapter lmdReceiptsAdapter;
    String LMDID, LMDName;
    TextView tv_lmd_name_receipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__lmd__receipts);
        lmd_recycler = findViewById(R.id.lmd_recycler);
        tv_lmd_name_receipt = findViewById(R.id.tv_lmd_name_receipt);


        LMDID = getIntent().getStringExtra("lmd_id");
        LMDName = getIntent().getStringExtra("lmd_name");

        LMDReceiptList = new ArrayList<>();
        tv_lmd_name_receipt.setText(LMDName + " Receipts");

        ReceiptDBHandler dbHandler = new ReceiptDBHandler(View_LMD_Receipts.this);
        LMDReceiptList = dbHandler.getLMDReceipts(LMDID);

        lmdReceiptsAdapter = new LMDReceiptsAdapter(LMDReceiptList, getApplicationContext());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        lmd_recycler.setLayoutManager(layoutManager);
        lmd_recycler.setItemAnimator(new DefaultItemAnimator());
        lmd_recycler.setAdapter(lmdReceiptsAdapter);
    }
}

package com.bgenterprise.bglmtcinventory;

/**
 * The activity that displays all Invoices in the database.
 */

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class View_All_Invoices extends AppCompatActivity {

    private RecyclerView invoiceRecycler;
    private List<Invoices> invoicesList;
    private AllInvoicesAdapter allInvoicesAdapter;
    SessionManager session;
    HashMap<String, String> allDetails;
    String LMDID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__all__invoices);
        invoiceRecycler = findViewById(R.id.invoiceRecycler);
        session = new SessionManager(View_All_Invoices.this);
        allDetails = session.getAllDetails();

        invoicesList = new ArrayList<>();
        InvoiceDBHandler db = new InvoiceDBHandler(View_All_Invoices.this);

        try{
            LMDID = allDetails.get(SessionManager.KEY_LMD_ID);

            if(LMDID.isEmpty()){
                invoicesList = db.getAllInvoices();
            }else{
                invoicesList = db.getAllInvoices(LMDID);
                Log.d("CHECK", invoicesList.get(0).toString());
            }

        }catch(Exception e){
            e.printStackTrace();
        }


        allInvoicesAdapter = new AllInvoicesAdapter(getApplicationContext(), invoicesList, new AllInvoicesAdapter.OnItemClickListener() {
            @Override
            public void onClick(Invoices invoices){
                Intent intent = new Intent(View_All_Invoices.this, View_LMD_Invoices.class);
                intent.putExtra("LMDID", invoices.getLMDID());
                intent.putExtra("TxnDate", invoices.getTxnDate());
                startActivity(intent);
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        invoiceRecycler.setLayoutManager(layoutManager);
        invoiceRecycler.setItemAnimator(new DefaultItemAnimator());
        invoiceRecycler.setAdapter(allInvoicesAdapter);

    }
}

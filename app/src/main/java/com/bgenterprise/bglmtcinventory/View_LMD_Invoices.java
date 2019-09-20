package com.bgenterprise.bglmtcinventory;

/**
 * Activity that displays all invoices for a specific LMD.
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class View_LMD_Invoices extends AppCompatActivity {

    private RecyclerView lmdinvoiceRecycler;
    private List<Invoices> lmdinvoiceList;
    private LMDInvoicesAdapter lmdInvoicesAdapter;
    String lmd_id, txn_date;
    TextView tv_lmd_invoice_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__lmd__invoices);

        lmdinvoiceRecycler = findViewById(R.id.lmdinvoice_recycler);
        tv_lmd_invoice_name = findViewById(R.id.tv_lmd_invoice_name);

        lmd_id = getIntent().getStringExtra("LMDID");
        txn_date = getIntent().getStringExtra("TxnDate");

        tv_lmd_invoice_name.setText(lmd_id + " Invoices");
        lmdinvoiceList = new ArrayList<>();

        final InvoiceDBHandler db = new InvoiceDBHandler(View_LMD_Invoices.this);
        lmdinvoiceList = db.getSpecificLMDInvoices(lmd_id, txn_date);

        lmdInvoicesAdapter = new LMDInvoicesAdapter(getApplicationContext(), lmdinvoiceList, new LMDInvoicesAdapter.OnItemClickListener() {
            @Override
            public void onClick(Invoices invoices) {
                //Get the stuff using LMDID, TxnDate & ItemID.
                HashMap<String, String> invoiceDetails = db.getInvoiceDetails(lmd_id, txn_date, invoices.getItemID());

                final MaterialAlertDialogBuilder dispute = new MaterialAlertDialogBuilder(View_LMD_Invoices.this, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert);
                LayoutInflater factory = LayoutInflater.from(getApplicationContext());
                dispute.setCancelable(false);

                final View view = factory.inflate(R.layout.dispute_invoice, null);

                final TextView tvLastCount = view.findViewById(R.id.tvLastCount);
                final TextView tvCurrentCount = view.findViewById(R.id.tvCurrentCount);
                final TextView tvCurrentInvoice = view.findViewById(R.id.tvCurrentInvoice);
                final Button btnConfirm = view.findViewById(R.id.btnConfirm);
                dispute.setView(view);
                final AlertDialog alertDialog = dispute.show();



                DecimalFormat myFormat = new DecimalFormat("#########.###");

                double lastCount = Double.parseDouble(invoiceDetails.get("LastFODCount"));
                double deliveriesSince = Double.parseDouble(invoiceDetails.get("DeliverySinceLastCount"));
                double total = lastCount + deliveriesSince;

                tvLastCount.setText("Last Count (" + invoiceDetails.get("LastFODDate") + ") " + invoiceDetails.get("LastFODCount")+ " + Delivery Since Last Count (" + invoiceDetails.get("DeliverySinceLastCount") + ") = " + myFormat.format(total));

                tvCurrentCount.setText("Current Physical Count (" + txn_date + "): " + invoiceDetails.get("CurrentCount"));

                tvCurrentInvoice.setText("Current Invoice (" + txn_date + "): " + invoiceDetails.get("CurrentInvoice"));

                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        lmdinvoiceRecycler.setLayoutManager(layoutManager);
        lmdinvoiceRecycler.setItemAnimator(new DefaultItemAnimator());
        lmdinvoiceRecycler.setAdapter(lmdInvoicesAdapter);

    }
}

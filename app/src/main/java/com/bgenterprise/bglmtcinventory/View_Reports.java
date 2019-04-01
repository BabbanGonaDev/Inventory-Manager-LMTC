package com.bgenterprise.bglmtcinventory;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class View_Reports extends AppCompatActivity {

    String lmd_id;
    TextView tvTotalNoOfProducts, tvReceiptAmount, tvInvoiceAmount, tvReceivableAmount, tvLMDPriceGroup;
    InvoiceDBHandler invoiceDBHandler;
    InventoryDBHandler inventoryDBHandler;
    ReceiptDBHandler receiptDBHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__report);
        lmd_id = getIntent().getStringExtra("LMDID");
        invoiceDBHandler = new InvoiceDBHandler(View_Reports.this);
        inventoryDBHandler = new InventoryDBHandler(View_Reports.this);
        receiptDBHandler = new ReceiptDBHandler(View_Reports.this);

        //Initialize the text views to be used.
        tvTotalNoOfProducts = findViewById(R.id.tvTotalNoOfProducts);
        tvInvoiceAmount = findViewById(R.id.tvInvoiceAmount);
        tvReceiptAmount = findViewById(R.id.tvReceiptAmount);
        tvReceivableAmount = findViewById(R.id.tvReceivableAmount);
        tvLMDPriceGroup = findViewById(R.id.tvLMDPriceGroup);

        Toast.makeText(View_Reports.this, "We are in view reports", Toast.LENGTH_LONG).show();
        DecimalFormat myFormat = new DecimalFormat("#########.###");

        tvInvoiceAmount.setText("NGN " + myFormat.format(getAmountOfInvoice(lmd_id)));
        tvLMDPriceGroup.setText("Price Group: " + getLMDsPriceGroup(lmd_id));
        tvReceiptAmount.setText("NGN " + myFormat.format(getLMDReceiptsAmount(lmd_id)));
        tvReceivableAmount.setText("NGN " + myFormat.format(getLMDsReceivables(lmd_id)));
        tvTotalNoOfProducts.setText("PENDING");
    }

    //Functions that would return the values to be used in the Text views.

    public Double getAmountOfInvoice(String LMDID){
        //This function returns the Total amount of Invoices by the LMD ever.
        return invoiceDBHandler.getLMDsInvoices(LMDID);
    }

    public String getLMDsPriceGroup(String LMDID){
        //This function returns the LMD's price group.
        return invoiceDBHandler.getPriceGroup(LMDID);
    }

    public Double getLMDReceiptsAmount(String LMDID){
        //This function returns the LMD's Total Receipt Amount.
        return receiptDBHandler.getSumReceipts(LMDID);
    }

    public Double getLMDsReceivables(String LMDID){
        //This function returns the LMD's receivable amount
        double receivable = getAmountOfInvoice(LMDID) - getLMDReceiptsAmount(LMDID);
        return receivable;
    }
}

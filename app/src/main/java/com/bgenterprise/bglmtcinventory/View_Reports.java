package com.bgenterprise.bglmtcinventory;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class View_Reports extends AppCompatActivity {

    String lmd_id, lmd_name;
    TextView tvReceiptAmount, tvInvoiceAmount, tvReceivableAmount, tvLMDPriceGroup, tvLastPaymentDate, tvLastPaymentAmount, tvLMDReceiptCount, tvLastCountDate, tvLastInvoiceAmount, tvLMDdetails;
    InvoiceDBHandler invoiceDBHandler;
    InventoryDBHandler inventoryDBHandler;
    ReceiptDBHandler receiptDBHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__report);
        lmd_id = getIntent().getStringExtra("LMDID");
        lmd_name = getIntent().getStringExtra("LMDNAME");
        invoiceDBHandler = new InvoiceDBHandler(View_Reports.this);
        inventoryDBHandler = new InventoryDBHandler(View_Reports.this);
        receiptDBHandler = new ReceiptDBHandler(View_Reports.this);

        //Initialize the text views to be used.
        tvLMDdetails = findViewById(R.id.tvLMDdetails);
        tvInvoiceAmount = findViewById(R.id.tvInvoiceAmount);
        tvReceiptAmount = findViewById(R.id.tvReceiptAmount);
        tvReceivableAmount = findViewById(R.id.tvReceivableAmount);
        tvLMDPriceGroup = findViewById(R.id.tvLMDPriceGroup);
        tvLastCountDate = findViewById(R.id.tvLastCountDate);
        tvLastInvoiceAmount = findViewById(R.id.tvLastInvoiceAmount);
        tvLMDReceiptCount = findViewById(R.id.tvLMDReceiptCount);
        tvLastPaymentDate = findViewById(R.id.tvLastPaymentDate);
        tvLastPaymentAmount = findViewById(R.id.tvLastPaymentAmount);


        //Toast.makeText(View_Reports.this, "We are in view reports", Toast.LENGTH_LONG).show();
        DecimalFormat myFormat = new DecimalFormat("#########.###");

        tvLMDdetails.setText(lmd_name + " ( " + lmd_id + " )");
        tvInvoiceAmount.setText("NGN " + myFormat.format(getAmountOfInvoice(lmd_id)));
        tvLMDPriceGroup.setText(getLMDsPriceGroup(lmd_id));
        tvReceiptAmount.setText("NGN " + myFormat.format(getLMDReceiptsAmount(lmd_id)));
        tvReceivableAmount.setText("NGN " + myFormat.format(getLMDsReceivables(lmd_id)));
        tvLastCountDate.setText(getLastCountDate(lmd_id));
        tvLastInvoiceAmount.setText("NGN " + myFormat.format(getLastInvoiceAmount(lmd_id)));
        tvLMDReceiptCount.setText(myFormat.format(getReceiptCount(lmd_id)));
        tvLastPaymentDate.setText(getLastPaymentDate(lmd_id));
        tvLastPaymentAmount.setText("NGN " + myFormat.format(getLastPaymentAmount(lmd_id)));


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

    public String getLastCountDate(String LMDID){
        //This function returns the last count done for an LMD.
        return invoiceDBHandler.getLastCountDate(LMDID);
    }

    public Integer getLastInvoiceAmount(String LMDID){
        //This function returns the last invoice amount of the LMD.
        return invoiceDBHandler.getLastInvoiceAmount(LMDID);
    }

    public Integer getReceiptCount(String LMDID){
        //This function returns the number of receipts paid by an LMD.
        return receiptDBHandler.getReceiptCount(LMDID);
    }

    public String getLastPaymentDate(String LMDID){
        //This function returns the last payment date of the LMD.
        return receiptDBHandler.getLastPaymentDate(LMDID);
    }

    public Double getLastPaymentAmount(String LMDID){
        //This function returns the last payment amount of the LMD.
        return receiptDBHandler.getLastPaymentAmount(LMDID);
    }






}

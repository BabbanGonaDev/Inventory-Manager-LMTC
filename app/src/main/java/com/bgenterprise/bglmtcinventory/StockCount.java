package com.bgenterprise.bglmtcinventory;
/**
 * The activity that handles the entry of the stock count for each product.
 */

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;

public class StockCount extends AppCompatActivity {

    Button btnSubmitCount, btnScanProduct;
    TextView tvLMDName, tvProductName;
    EditText etCount, etConfirmCount;
    SessionManager session;
    SharedPreferences QRPrefs;
    InvoiceDBHandler invoiceDBHandler;
    InventoryDBHandler inventoryDBHandler;
    RestockDBHandler restockDBHandler;
    HashMap<String, String> allDetails;
    String currentDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_count);
        QRPrefs = getSharedPreferences("QRPreferences", MODE_PRIVATE);
        session = new SessionManager(StockCount.this);
        invoiceDBHandler = new InvoiceDBHandler(StockCount.this);
        inventoryDBHandler = new InventoryDBHandler(StockCount.this);
        restockDBHandler = new RestockDBHandler(StockCount.this);
        allDetails = session.getAllDetails();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        currentDate = dateFormat.format(date);

        btnSubmitCount = findViewById(R.id.btnSubmitCount);
        btnScanProduct = findViewById(R.id.btnScanProduct);
        tvLMDName = findViewById(R.id.tvLMDName);
        tvProductName = findViewById(R.id.tvProductName);
        etCount = findViewById(R.id.etCount);
        etConfirmCount = findViewById(R.id.etConfirmCount);

        tvLMDName.setText(allDetails.get(SessionManager.KEY_LMD_NAME));
        tvProductName.setText(allDetails.get(SessionManager.KEY_PRODUCT_NAME));

        btnScanProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonScanProduct(v);
            }
        });

        btnSubmitCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonSubmitCount(v);
            }
        });

    }

    public void ButtonScanProduct(View view){
        QRPrefs.edit().putString("required", "Product_Count").commit();
        QRPrefs.edit().putString("scanner_title", "Scan Product to Count").commit();
        startActivity(new Intent(StockCount.this, ScannerActivity.class));

    }

    public void ButtonSubmitCount(View view){
        if(etCount.getText().toString().equals(etConfirmCount.getText().toString()) && !etCount.getText().toString().isEmpty() && !tvProductName.getText().toString().isEmpty()
                && !tvLMDName.getText().toString().isEmpty()){

            //TODO---> Calculate the last phy count and its date and deliveries since then. (Calculate it before entering into the database.)
            //Hence it would be the most recent entry with that LMDID and product ID. just get the date and count.

            HashMap<String, String> lastCountDetails = invoiceDBHandler.LastCountDetails(allDetails.get(SessionManager.KEY_LMD_ID), allDetails.get(SessionManager.KEY_PRODUCT_ID));

            String LastFODCount = lastCountDetails.get("FODPhysicalCount");
            String LastFODDate = lastCountDetails.get("TxnDate");

            //Get the total Ins and Outs since the lastFODdate.
            int DeliverySinceLastCount = inventoryDBHandler.DeliveriesSinceLastCount(allDetails.get(SessionManager.KEY_LMD_ID), allDetails.get(SessionManager.KEY_PRODUCT_ID), LastFODDate);

            DecimalFormat myFormat = new DecimalFormat("#########.###");

            InvoiceModule.PriceBlock priceBlock = new InvoiceModule.PriceBlock(StockCount.this);
            Double invoicePrice = priceBlock.GetThePrice();

            InvoiceModule.InvoiceBlock invoiceBlock = new InvoiceModule.InvoiceBlock(StockCount.this);
            Double invoiceQty = invoiceBlock.calculateInvoiceQuantity(Integer.parseInt(etCount.getText().toString()), allDetails.get(SessionManager.KEY_LMD_ID), allDetails.get(SessionManager.KEY_PRODUCT_ID));

            Double invoiceValue = invoicePrice * invoiceQty;
            Log.d("CHECK", "Invoice Value: " + invoiceValue.toString());

            Toast.makeText(StockCount.this,"The Invoice Value for " + tvProductName.getText().toString() + " is: " + myFormat.format(invoiceValue), Toast.LENGTH_LONG).show();



            String UniqueID = allDetails.get(SessionManager.KEY_STAFF_ID) + "_" + String.valueOf(System.currentTimeMillis());
            if(inventoryDBHandler.onAdd_Inventory03T(UniqueID, currentDate, allDetails.get(SessionManager.KEY_LMD_ID), allDetails.get(SessionManager.KEY_PRODUCT_ID), allDetails.get(SessionManager.KEY_PRODUCT_NAME), etCount.getText().toString(), "FOD", "0", "", "", "no")){

                if(invoiceDBHandler.onAdd_LMDInvoiceValueT(allDetails.get(SessionManager.KEY_LMD_ID), allDetails.get(SessionManager.KEY_PRODUCT_ID), etCount.getText().toString(), currentDate, "FOD", myFormat.format(invoicePrice), myFormat.format(invoiceQty), myFormat.format(invoiceValue), LastFODCount, LastFODDate, String.valueOf(DeliverySinceLastCount))){


                    //TODO---> RESTOCK CALCULATOR.
                    RestockModule.RestockBlock restock = new RestockModule.RestockBlock(StockCount.this);
                    Double restockValue = restock.CalcRestockValue(invoiceValue);

                    Toast.makeText(StockCount.this, "Restock Value: " + myFormat.format(restockValue), Toast.LENGTH_LONG).show();

                    if(restockDBHandler.onAdd_RestockT(allDetails.get(SessionManager.KEY_LMD_ID), allDetails.get(SessionManager.KEY_PRODUCT_ID), myFormat.format(restockValue), allDetails.get(SessionManager.KEY_LMD_ID)+allDetails.get(SessionManager.KEY_PRODUCT_ID),
                            etCount.getText().toString(), currentDate)){

                        new AlertDialog.Builder(StockCount.this)
                                .setTitle("Stock Count")
                                .setMessage("Product Count Saved, Do you want to count another product for this LMD ?")
                                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        session.CLEAR_COUNT_DETAILS();
                                        etCount.setText("");
                                        etConfirmCount.setText("");
                                        recreate();
                                    }
                                })
                                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        session.CLEAR_COUNT_DETAILS();

                                        //No longer clear LMD since we are moving on.
                                        /*session.CLEAR_LMD_DETAILS();*/

                                        startActivity(new Intent(StockCount.this, View_Receivable.class));
                                    }
                                }).show();


                    }else{
                        Toast.makeText(StockCount.this, "Error in saving Restock Value", Toast.LENGTH_LONG).show();
                    }


                }else{
                    Toast.makeText(StockCount.this, "Error in inserting LMDInvoiceValueT", Toast.LENGTH_LONG).show();

                }

            }else{
                Toast.makeText(StockCount.this, "Error in inserting Inventory03T", Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(StockCount.this, "Kindly Confirm Details Again", Toast.LENGTH_LONG).show();
            etCount.setText("");
            etConfirmCount.setText("");
        }
    }
}

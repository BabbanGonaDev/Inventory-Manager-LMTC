package com.bgenterprise.bglmtcinventory;
/**
 * The activity that handles the entry of the stock count for each product.
 */

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bgenterprise.bglmtcinventory.InventoryDbContract.HoldingCostT;
import com.bgenterprise.bglmtcinventory.InventoryDbContract.Inventory03T;
import com.bgenterprise.bglmtcinventory.InvoiceDbContract.LMDInvoiceValueT;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
        String firstinput = etCount.getText().toString().replaceAll("[^0-9]", "");
        String secondInput = etConfirmCount.getText().toString().replaceFirst("[^0-9]", "");
        if(etCount.getText().toString().equals(etConfirmCount.getText().toString()) && !etCount.getText().toString().isEmpty() && !tvProductName.getText().toString().isEmpty()
                && !tvLMDName.getText().toString().isEmpty()){
            Log.d("HERE", "its equal");
            //Calculate the last phy count and its date and deliveries since then. (Calculate it before entering into the database.)
            //Hence it would be the most recent entry with that LMDID and product ID. just get the date and count.

            HashMap<String, String> lastCountDetails = invoiceDBHandler.LastCountDetails(allDetails.get(SessionManager.KEY_LMD_ID), allDetails.get(SessionManager.KEY_PRODUCT_ID));

            String LastFODCount = lastCountDetails.get("FODPhysicalCount");
            String LastFODDate = lastCountDetails.get("TxnDate");
            int lastCount = Integer.valueOf(LastFODCount);
            Log.d("lastCount", "" + lastCount);

            //Get the total Ins and Outs since the lastFODdate.
            int DeliverySinceLastCount = inventoryDBHandler.DeliveriesSinceLastCount(allDetails.get(SessionManager.KEY_LMD_ID), allDetails.get(SessionManager.KEY_PRODUCT_ID), LastFODDate);

            DecimalFormat myFormat = new DecimalFormat("#########.###");

            InvoiceModule.PriceBlock priceBlock = new InvoiceModule.PriceBlock(StockCount.this);
            Double invoicePrice = priceBlock.GetThePrice();

            InvoiceModule.InvoiceBlock invoiceBlock = new InvoiceModule.InvoiceBlock(StockCount.this);
            Double invoiceQty = invoiceBlock.calculateInvoiceQuantity(Integer.parseInt(etCount.getText().toString()), allDetails.get(SessionManager.KEY_LMD_ID), allDetails.get(SessionManager.KEY_PRODUCT_ID));
            Double invoiceQtyForRestock = invoiceBlock.calcInvQtyForRestock(Integer.parseInt(etCount.getText().toString()), allDetails.get(SessionManager.KEY_LMD_ID), allDetails.get(SessionManager.KEY_PRODUCT_ID));

            Double invoiceValue = invoicePrice * invoiceQty;
            Log.d("CHECK", "Invoice Value: " + invoiceValue.toString());

            Toast.makeText(StockCount.this,"The Invoice Value for " + tvProductName.getText().toString() + " is: " + myFormat.format(invoiceValue), Toast.LENGTH_LONG).show();

//            get the LMD and Product details from Session Manager
            HashMap<String,String> val = session.getAllDetails();
            String ItemId = val.get(SessionManager.KEY_PRODUCT_ID);
            String LmdId = val.get(SessionManager.KEY_LMD_ID);
            String staff_ID = val.get(SessionManager.KEY_STAFF_ID);

//            update LMDInvoiceValue table with holding cost entry
//            updateInvoiceT(ItemId, LmdId);

            /**
             * Ensure that a duplicate stock count of the same product for the same lmd doesn't already exist for today.
             */
            if(invoiceDBHandler.preventProductCountTwice(allDetails.get(SessionManager.KEY_LMD_ID), allDetails.get(SessionManager.KEY_PRODUCT_ID))){

                String UniqueIDForInventoryT = allDetails.get(SessionManager.KEY_STAFF_ID) + "_" + String.valueOf(System.currentTimeMillis()) + "_FOD";
                String UniqueIDForInvoice = allDetails.get(SessionManager.KEY_STAFF_ID) + "_"+ String.valueOf(System.currentTimeMillis()) + "_INVOICE";
                String UniqueIDForRestock = allDetails.get(SessionManager.KEY_STAFF_ID) + "_"+ String.valueOf(System.currentTimeMillis()) + "_RESTOCK";

                if (inventoryDBHandler.onAdd_Inventory03T(UniqueIDForInventoryT, currentDate, allDetails.get(SessionManager.KEY_LMD_ID),
                        allDetails.get(SessionManager.KEY_PRODUCT_ID), allDetails.get(SessionManager.KEY_PRODUCT_NAME), etCount.getText().toString(),
                        "FOD", "0", "", "", "no", staff_ID)) {

                    if (invoiceDBHandler.onAdd_LMDInvoiceValueT(UniqueIDForInvoice, allDetails.get(SessionManager.KEY_LMD_ID), allDetails.get(SessionManager.KEY_PRODUCT_ID),
                            etCount.getText().toString(), currentDate, "FOD", myFormat.format(invoicePrice), myFormat.format(invoiceQty),
                            myFormat.format(invoiceValue), LastFODCount, LastFODDate, String.valueOf(DeliverySinceLastCount), staff_ID, "no")) {

                        RestockModule.RestockBlock restock = new RestockModule.RestockBlock(StockCount.this);
                        Double restockValue = restock.CalcRestockValue(invoiceQtyForRestock, ItemId, LmdId);

                        Toast.makeText(StockCount.this, "Restock Value: " + myFormat.format(restockValue), Toast.LENGTH_LONG).show();

                        //Not sure why this was put here. Hence am removing it. So that restock is calculated for every Stock Count. - Rehoboth (05/07/19)
                        /*if (lastCount <= restockValue) {
                            if (restockDBHandler.onAdd_RestockT(UniqueIDForRestock, allDetails.get(SessionManager.KEY_LMD_ID), allDetails.get(SessionManager.KEY_PRODUCT_ID), myFormat.format(restockValue), allDetails.get(SessionManager.KEY_LMD_ID) + allDetails.get(SessionManager.KEY_PRODUCT_ID),
                                    etCount.getText().toString(), currentDate, allDetails.get(SessionManager.KEY_STAFF_ID), "no")) {
                            } else {
                                Toast.makeText(StockCount.this, "Error in saving Restock Value", Toast.LENGTH_LONG).show();
                            }
                        }*/

                        //Hence now insert the restocks row into the db.
                        if (!restockDBHandler.onAdd_RestockT(UniqueIDForRestock, allDetails.get(SessionManager.KEY_LMD_ID), allDetails.get(SessionManager.KEY_PRODUCT_ID), myFormat.format(restockValue), allDetails.get(SessionManager.KEY_LMD_ID) + allDetails.get(SessionManager.KEY_PRODUCT_ID),
                                etCount.getText().toString(), currentDate, allDetails.get(SessionManager.KEY_STAFF_ID), "no")) {
                            Toast.makeText(StockCount.this, "Error in saving Restock Value", Toast.LENGTH_LONG).show();
                        }

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
                    } else {
                        Toast.makeText(StockCount.this, "Error in inserting LMDInvoiceValueT", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(StockCount.this, "Error in inserting Inventory03T", Toast.LENGTH_LONG).show();
                }

            }else{
                new AlertDialog.Builder(StockCount.this)
                        .setTitle("Duplicate Count")
                        .setMessage("A Physical Count for " + allDetails.get(SessionManager.KEY_LMD_NAME) + " of " + allDetails.get(SessionManager.KEY_PRODUCT_NAME) + " for today already exists.")
                        .setPositiveButton("Ok, Got it.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                session.CLEAR_COUNT_DETAILS();
                                etCount.setText("");
                                etConfirmCount.setText("");
                                recreate();
                            }
                        }).show();
            }


        }else{
            Toast.makeText(StockCount.this, "Kindly Confirm Details Again", Toast.LENGTH_LONG).show();
            etCount.setText("");
            etConfirmCount.setText("");
        }
    }

    public void updateInvoiceT(String ItemId, String LmdId) {
        Map<String, String> map;
        ArrayList<Map<String, String>> wordList = new ArrayList<>();
        String dateAfterZero = null;
        String lastZeroDate = null;
        String hc = "0.00";
        double holdingCost;
        String lastDateWithHC = null;
        Cursor cursor = null;
        SQLiteDatabase database = inventoryDBHandler.getWritableDatabase();
        SQLiteDatabase database1 = invoiceDBHandler.getWritableDatabase();

//        getting the holding cost
        try {
            cursor = database.rawQuery("SELECT " + HoldingCostT.COLUMN_HOLDING_COST + " FROM " + HoldingCostT.TABLE_NAME +
                    " WHERE " + HoldingCostT.COLUMN_ITEM_ID + " =\"" + ItemId + "\"", null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                hc = cursor.getString(0);
                cursor.moveToNext();
            }
            cursor.close();

            Log.d("hc", "" + hc);
            holdingCost = Double.valueOf(hc);
            Log.d("HC ", "" + holdingCost);
            Log.d("ItemID", "" + ItemId);
            Log.d("LMDid", "" + LmdId);

//        getting the last occurrence of zero balance
            try {
                cursor = database.rawQuery("SELECT " + Inventory03T.COLUMN_TXN_DATE + " FROM " + Inventory03T.TABLE_NAME + " WHERE " +
                        Inventory03T.COLUMN_UNIT + " = 0 AND " + Inventory03T.COLUMN_LMD_ID + "='" + LmdId + "'"
                        + " AND " + Inventory03T.COLUMN_ITEM_ID + "='" + ItemId + "' AND " + Inventory03T.COLUMN_TYPE + " = 'FOD'" + " ORDER BY " +
                        Inventory03T.COLUMN_TXN_DATE + " DESC LIMIT 1", null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    lastZeroDate = cursor.getString(0);
                    cursor.moveToNext();
                }
                cursor.close();
            } catch (Exception e) {
                Log.d("try-lastZdate", "" + e);
            }

            Log.d("lastZdate", "" + lastZeroDate);

//        getting the next date after the lastZeroDate
            try {
                Cursor cursor3;
                cursor3 = database.rawQuery("SELECT " + Inventory03T.COLUMN_TXN_DATE + " FROM " + Inventory03T.TABLE_NAME + " WHERE " +
                        Inventory03T.COLUMN_TXN_DATE + "> " + "'" + lastZeroDate + "' AND " + Inventory03T.COLUMN_LMD_ID + "= '" + LmdId + "'"
                        + " AND " + Inventory03T.COLUMN_ITEM_ID + "='" + ItemId + "' AND " + Inventory03T.COLUMN_TYPE + " = 'FOD'" +
                        " ORDER BY " + Inventory03T.COLUMN_TXN_DATE + " ASC LIMIT 1", null);
                cursor3.moveToFirst();
                while (!cursor3.isAfterLast()) {
                    dateAfterZero = cursor3.getString(0);
                    cursor3.moveToNext();
                }
                cursor3.close();

                Log.d("DateAfterZero", "" + dateAfterZero);

//        Converting the dateAFterZero to yyyy/mm/dd format
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format_ = new SimpleDateFormat("yyyy-MM-dd");
                Date dateAfterZero_ = format_.parse(dateAfterZero);
                Log.d("date1", "" + dateAfterZero_);

                try {
                    //        getting all instances of dates after dateAfterZero
                    cursor = database.rawQuery("SELECT " + Inventory03T.COLUMN_TXN_DATE + ", " + Inventory03T.COLUMN_UNIT + " FROM " +
                            Inventory03T.TABLE_NAME + " WHERE " + Inventory03T.COLUMN_TXN_DATE + ">" + "'" + dateAfterZero + "'" + " AND " +
                            Inventory03T.COLUMN_LMD_ID + "='" + LmdId + "'" + " AND " + Inventory03T.COLUMN_ITEM_ID + "='"
                            + ItemId + "' AND " + Inventory03T.COLUMN_TYPE + " = 'FOD' ORDER BY " + Inventory03T.COLUMN_TXN_DATE, null);

//        looping through the table to calculate invoice values
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        map = new HashMap<>();
                        map.put("date", cursor.getString(0));
                        map.put("balance", cursor.getString(1));

                        Log.d("date,bal", "" + map.get("date") + " " + map.get("balance"));
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = format.parse(map.get("date"));

//                checking for difference in date between stocking date and current date and whether it is <= 14
                        long diffInMillies = Math.abs(date.getTime() - dateAfterZero_.getTime());
                        Log.d("today", "" + date);
                        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                        Log.d("diff", "" + diff);

                        if (diff <= 14) {

                        } else {
                            Date today = new Date();
                            SimpleDateFormat formatter_ = new SimpleDateFormat("yyyy-MM-dd");
                            String today_string = formatter_.format(today);
                            try {
//                          getting the count of entries with holding cost in the InvoiceValueT satisfying the WHERE conditions

                                Cursor cursor1;
                                cursor1 = database1.rawQuery("SELECT * FROM " + LMDInvoiceValueT.TABLE_NAME +
                                        " WHERE " + LMDInvoiceValueT.COLUMN_HOLDING_COST + "= 'Yes' AND " + LMDInvoiceValueT.COLUMN_LMD_ID + "='" +
                                        LmdId + "'" + " AND " + LMDInvoiceValueT.COLUMN_ITEM_ID + "='" + ItemId + "' AND " +
                                        LMDInvoiceValueT.COLUMN_TXN_DATE + " BETWEEN '" + dateAfterZero + "' AND '" + today_string + "'", null);

                                Log.d("count", "" + cursor1.getCount());

                                int count = cursor1.getCount();
                                cursor1.close();
                                if (count == 0) {

//                        checking for difference in date between stocking date and current date
                                    Date date1 = format.parse(map.get("date"));
                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                    String today_string_ = formatter.format(date1);
                                    long diffInMillies_ = Math.abs(date1.getTime() - dateAfterZero_.getTime());
                                    long diff_ = TimeUnit.DAYS.convert(diffInMillies_, TimeUnit.MILLISECONDS);
                                    long total_diff = diff_ - 14;
                                    Log.d("total_diff", "" + total_diff);
                                    double invoiceVal = Integer.valueOf(Objects.requireNonNull(map.get("balance"))) * total_diff * holdingCost;

//                        inserting invoice value details into InvoiceValueT
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put(LMDInvoiceValueT.COLUMN_LMD_ID, LmdId);
                                    contentValues.put(LMDInvoiceValueT.COLUMN_ITEM_ID, ItemId);
                                    contentValues.put(LMDInvoiceValueT.COLUMN_TXN_DATE, String.valueOf(today_string));
                                    contentValues.put(LMDInvoiceValueT.COLUMN_INVOICE_VALUE, String.valueOf(invoiceVal));
                                    contentValues.put(LMDInvoiceValueT.COLUMN_HOLDING_COST, "Yes");

                                    database1.insert(LMDInvoiceValueT.TABLE_NAME, null, contentValues);

                                } else {
                                    Calendar c = Calendar.getInstance();
                                    Date today__ = new Date();
                                    SimpleDateFormat formatter__ = new SimpleDateFormat("yyyy-MM-dd");

                                    c.setTime(today__);
                                    c.add(Calendar.DAY_OF_MONTH, 1);
                                    String tomorrow = formatter__.format(c.getTime());
                                    Log.d("tomorrow", "" + tomorrow);
                                    String today_string__ = formatter__.format(today__);
                                    try {
                                        Cursor cursor2;
                                        cursor2 = database1.rawQuery("SELECT " + LMDInvoiceValueT.COLUMN_TXN_DATE + " FROM " + LMDInvoiceValueT.TABLE_NAME
                                                + " WHERE " + LMDInvoiceValueT.COLUMN_TXN_DATE + " < '" + tomorrow + "' AND " + LMDInvoiceValueT.COLUMN_HOLDING_COST +
                                                "= 'Yes' AND " + LMDInvoiceValueT.COLUMN_LMD_ID + "='" + LmdId + "'" + " AND " + LMDInvoiceValueT.COLUMN_ITEM_ID +
                                                "='" + ItemId + "' ORDER BY " + LMDInvoiceValueT.COLUMN_TXN_DATE + " DESC LIMIT 1", null);
                                        cursor2.moveToFirst();
                                        while (!cursor2.isAfterLast()) {
                                            lastDateWithHC = cursor2.getString(0);
                                            cursor2.moveToNext();
                                        }
                                        Log.d("lastDatewithHC", "" + lastDateWithHC);
                                        cursor2.close();
                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
                                        Date lastDateWithHC_ = format2.parse(lastDateWithHC);
                                        Date today_ = new Date();
                                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                        String today_str = formatter.format(today_);
                                        long diffInMillies2 = Math.abs(today_.getTime() - lastDateWithHC_.getTime());
                                        long diff2 = TimeUnit.DAYS.convert(diffInMillies2, TimeUnit.MILLISECONDS);
                                        Log.d("diff2", "" + diff2);
                                        double invoiceVal2 = Integer.valueOf(Objects.requireNonNull(map.get("balance"))) * diff2 * holdingCost;

                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put(LMDInvoiceValueT.COLUMN_LMD_ID, LmdId);
                                        contentValues.put(LMDInvoiceValueT.COLUMN_ITEM_ID, ItemId);
                                        contentValues.put(LMDInvoiceValueT.COLUMN_TXN_DATE, String.valueOf(today_str));
                                        contentValues.put(LMDInvoiceValueT.COLUMN_INVOICE_VALUE, String.valueOf(invoiceVal2));
                                        contentValues.put(LMDInvoiceValueT.COLUMN_HOLDING_COST, "Yes");

                                        database1.insert(LMDInvoiceValueT.TABLE_NAME, null, contentValues);
                                    } catch (Exception e) {
                                        Log.d("try5", "" + e);
                                    }
                                }
                            } catch (Exception e) {
                                Log.d("try4", "" + e);
                            }
                        }
                        cursor.moveToNext();
                    }
                    cursor.close();
                } catch (Exception e) {
                    Log.d("try3", "" + e);
                }

            } catch (Exception e) {
                Log.d("try2", "" + e);
            }

        } catch (Exception e) {
            Log.d("try1", "" + e);
        }

    }
}

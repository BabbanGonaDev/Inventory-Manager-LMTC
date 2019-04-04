package com.bgenterprise.bglmtcinventory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bgenterprise.bglmtcinventory.InvoiceDbContract.PriceGroupT;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Database handler class for the Invoice Database.
 */

public class InvoiceDBHandler extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "invoices.db";
    private static final int DATABASE_VERSION = 1;

    Context context;

    public InvoiceDBHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i2){
        super.onUpgrade(database, i, i2);
    }

    //Insert into the Invoice Database.
    public boolean onAdd_LMDInvoiceValueT(String LMDID, String ItemID, String FODPhysicalCount, String TxnDate, String Type, String UnitPrice,
                         String InvoiceQty, String InvoiceValue, String LastFODCount, String LastFODDate, String DeliverySinceLastCount){

        try{
            SQLiteDatabase db = getWritableDatabase();
            String insertQ = "INSERT INTO LMDInvoiceValueT (LMDID, ItemID, FODPhysicalCount, TxnDate, Type, UnitPrice, InvoiceQty, InvoiceValue, LastFODCount, LastFODdate, DeliverySinceLastCount) VALUES (" +
                    "'" + LMDID + "','" + ItemID + "','" + FODPhysicalCount + "','" + TxnDate + "','" + Type + "','" + UnitPrice + "','" + InvoiceQty + "','" + InvoiceValue+ "','" + LastFODCount + "','"+ LastFODDate +"','"+ DeliverySinceLastCount + "')";
            Log.d("CHECK", "Insert into LMDInvoiceValueT Query: " + insertQ);
            db.execSQL(insertQ);
            db.close();
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //Get the price group for an LMD from the PriceGroupT table.
    public String getPriceGroup(String lmdID){
        String group;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT PGName FROM PriceGroupT WHERE LMDID = '" + lmdID + "'", null);
        cursor.moveToFirst();

        if(cursor.getCount() < 1){
            return "";
        }

        group = cursor.getString(cursor.getColumnIndex("PGName"));

        cursor.close();
        db.close();
        return group;
    }

    //Get the number of times, a price change has been recorded for a specific item in a price group.
    public Integer getPriceChangeCount(String pgName, String itemID){
        int count;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM PriceT WHERE PriceGroup = '" + pgName + "' AND ItemID = '" + itemID + "'",null);
        cursor.moveToFirst();

        count = cursor.getCount();

        cursor.close();
        db.close();
        return count;
    }

    //Get the most recent price change for an Item in a priceGroup.
    public Double getSinglePrice(String PG, String Itemid){
        Double ItemPrice;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM PriceT WHERE PriceGroup = '" + PG + "' AND ItemID = '" + Itemid + "' ORDER BY ChngDate DESC LIMIT 1", null);
        cursor.moveToFirst();

        ItemPrice = cursor.getDouble(cursor.getColumnIndex("Price"));

        cursor.close();
        db.close();
        return ItemPrice;
    }

    //Get the 2nd to the most recent price change in the Price table.
    public Double getFormerPrice(String PG, String Itemid){
        List<Double> formerPrice = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM PriceT WHERE PriceGroup = '" + PG + "' AND ItemID = '" + Itemid + "' ORDER BY ChngDate DESC LIMIT 2", null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            formerPrice.add(cursor.getDouble(cursor.getColumnIndex("Price")));
            cursor.moveToNext();
        }

        cursor.close();
        db.close();
        return formerPrice.get(1);
    }

    //Get the latest price change date for an Item.
    public String getLatestDate(String PG, String Itemid){
        String date;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM PriceT WHERE PriceGroup = '" + PG + "' AND ItemID = '" + Itemid + "' ORDER BY ChngDate DESC LIMIT 1", null);
        cursor.moveToFirst();

        date = cursor.getString(cursor.getColumnIndex("ChngDate"));

        cursor.close();
        db.close();
        return date;
    }

    //Get the sum of total Invoices for an LMD and Item.
    public Integer getTotalInvoices(String LMDid, String item_id){
        int totalInvoices = 0;

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(InvoiceValue) FROM LMDInvoiceValueT WHERE LMDID = '" + LMDid + "' AND ItemID = '" + item_id + "'", null);
        c.moveToFirst();

        totalInvoices = c.getInt(c.getColumnIndex("SUM(InvoiceValue)"));

        c.close();
        db.close();
        return totalInvoices;
    }

    //Get total LMD's Invoices.
    public Double getLMDsInvoices(String lmdid){
        double amount = 0.0;
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(InvoiceValue) FROM LMDInvoiceValueT WHERE LMDID = '" + lmdid + "'", null);
        c.moveToFirst();

        if(c.getCount() < 1){
            return amount;
        }

        amount = c.getDouble(c.getColumnIndex("SUM(InvoiceValue)"));


        c.close();
        db.close();
        return amount;
    }

    //Get a list of all the Invoices.
    public List<Invoices> getAllInvoices(){
        List<Invoices> invoices = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT *, COUNT(LMDID), SUM(InvoiceValue) FROM LMDInvoiceValueT GROUP BY TxnDate, LMDID ORDER BY TxnDate DESC", null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            invoices.add(new Invoices(cursor.getString(cursor.getColumnIndex("LMDID")), cursor.getString(cursor.getColumnIndex("TxnDate")),
                    cursor.getString(cursor.getColumnIndex("COUNT(LMDID)")), cursor.getString(cursor.getColumnIndex("SUM(InvoiceValue)"))));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return invoices;
    }

    //Get a list of all the Invoices based on LMD.
    public List<Invoices> getAllInvoices(String lmdid){
        List<Invoices> invoices = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT LMDID, TxnDate, COUNT(LMDID), SUM(InvoiceValue) FROM LMDInvoiceValueT WHERE LMDID = '"+ lmdid +"' GROUP BY TxnDate, LMDID ORDER BY TxnDate DESC", null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            invoices.add(new Invoices(cursor.getString(cursor.getColumnIndex("LMDID")), cursor.getString(cursor.getColumnIndex("TxnDate")),
                    cursor.getString(cursor.getColumnIndex("COUNT(LMDID)")), cursor.getString(cursor.getColumnIndex("SUM(InvoiceValue)"))));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return invoices;
    }

    //Get a list of the specific invoices attributed to an LMD on a specific date.
    public List<Invoices> getSpecificLMDInvoices(String lmd_id, String txn_date){
        List<Invoices> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM LMDInvoiceValueT WHERE LMDID = '" + lmd_id + "' AND TxnDate = '" + txn_date + "'", null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            list.add(new Invoices(cursor.getString(cursor.getColumnIndex("ItemID")), cursor.getString(cursor.getColumnIndex("FODPhysicalCount")),
                    cursor.getString(cursor.getColumnIndex("UnitPrice")), cursor.getString(cursor.getColumnIndex("InvoiceQty")), cursor.getString(cursor.getColumnIndex("InvoiceValue"))));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return list;
    }


    //Gets the 2nd to the most recent invoice entry for the LMD and Item for the restock calc..
    public String getFormerInvDate(String LMDID, String ItemID){

        List<String> formerDate = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM LMDInvoiceValueT WHERE LMDID = '"+ LMDID + "' AND ItemID = '"+ ItemID + "' ORDER BY TxnDate DESC LIMIT 2", null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()){
            formerDate.add(cursor.getString(cursor.getColumnIndex("TxnDate")));
            cursor.moveToNext();
        }

        cursor.close();
        db.close();

        //Try to return the second to most recent date and if there isn't any, return 0000.

        try{

            return formerDate.get(1);

        }catch(Exception e){

            return "0000-00-00";
        }

    }

    public Double getInvoiceSum(String lmd_id){
        double sum = 0.00;

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(InvoiceValue) FROM LMDInvoiceValueT WHERE LMDID = '" + lmd_id + "'", null);
        cursor.moveToFirst();

        if(cursor.getCount() < 1){
            return sum;
        }

        do{
            sum = cursor.getDouble(cursor.getColumnIndex("SUM(InvoiceValue)"));
            cursor.moveToNext();
        }while(!cursor.isAfterLast());

        cursor.close();
        db.close();

        return sum;
    }

    public Double latestInvoice(String lmd_id){
        double latest = 0.00;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        String today = dateFormat.format(date);

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(InvoiceValue) FROM LMDInvoiceValueT WHERE LMDID = '" + lmd_id + "' AND TxnDate = '"+ today +"'", null);
        cursor.moveToFirst();

        if(cursor.getCount() < 1){
            return latest;
        }

        do{
            latest = cursor.getDouble(cursor.getColumnIndex("SUM(InvoiceValue)"));
            cursor.moveToNext();
        }while(!cursor.isAfterLast());

        cursor.close();
        db.close();

        return latest;
    }

    public HashMap<String,String> LastCountDetails(String lmdID, String item){

        HashMap<String, String> lastCount = null;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM LMDInvoiceValueT WHERE LMDID = '"+ lmdID + "' AND ItemID = '"+ item + "' ORDER BY TxnDate DESC LIMIT 1", null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            lastCount = new HashMap<>();
            lastCount.put("FODPhysicalCount", cursor.getString(cursor.getColumnIndex("FODPhysicalCount")));
            lastCount.put("TxnDate", cursor.getString(cursor.getColumnIndex("TxnDate")));
            cursor.moveToNext();
        }

        cursor.close();
        db.close();

        return lastCount;
    }

    public HashMap<String, String> getInvoiceDetails(String LMDID, String TxnDate, String ItemID){
        HashMap<String, String> invoiceDetails = null;
        SQLiteDatabase db = getWritableDatabase();

        String query = "SELECT * FROM LMDInvoiceValueT WHERE LMDID = '"+ LMDID +"' AND ItemID = '"+ ItemID +"' AND TxnDate = '"+ TxnDate +"'";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while(!c.isAfterLast()){
            invoiceDetails = new HashMap<>();
            invoiceDetails.put("LastFODCount", c.getString(c.getColumnIndex("LastFODCount")));
            invoiceDetails.put("LastFODDate", c.getString(c.getColumnIndex("LastFODdate")));
            invoiceDetails.put("DeliverySinceLastCount", c.getString(c.getColumnIndex("DeliverySinceLastCount")));
            invoiceDetails.put("CurrentCount", c.getString(c.getColumnIndex("FODPhysicalCount")));
            invoiceDetails.put("CurrentInvoice", c.getString(c.getColumnIndex("InvoiceValue")));
            c.moveToNext();
        }

        c.close();
        db.close();
        return invoiceDetails;
    }

    void updatePriceGroupT(JSONArray jsonArray) {
        SQLiteDatabase db = getWritableDatabase();
        JSONObject jsonObject;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                jsonObject = jsonArray.getJSONObject(i);
                int check = 0;
                Cursor cursor = db.rawQuery("SELECT COUNT(" + PriceGroupT.COLUMN_LMD_ID + ") from " + PriceGroupT.TABLE_NAME +
                        " WHERE " + PriceGroupT.COLUMN_PG_NAME + " =\"" + jsonObject.get("PGName") + "\"", null);
                cursor.moveToFirst();
                if (!cursor.isAfterLast()) {
                    check = cursor.getInt(0);
                }
                cursor.close();
                ContentValues contentValues = new ContentValues();
                if (check == 0) {
                    contentValues.put(PriceGroupT.COLUMN_LMD_ID, jsonObject.getString("LMDID"));
                    contentValues.put(PriceGroupT.COLUMN_PG_NAME, jsonObject.getString("PGName"));

                    db.insert(PriceGroupT.TABLE_NAME, null, contentValues);
                } else {
                    contentValues.put(PriceGroupT.COLUMN_LMD_ID, jsonObject.getString("LMDID"));
                    contentValues.put(PriceGroupT.COLUMN_PG_NAME, jsonObject.getString("PGName"));
                    String where = PriceGroupT.COLUMN_LMD_ID + "=?";
                    String[] whereArgs = new String[]{String.valueOf(jsonObject.getString("LMDID"))};
                    db.update(PriceGroupT.TABLE_NAME, contentValues, where, whereArgs);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

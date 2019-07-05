package com.bgenterprise.bglmtcinventory;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bgenterprise.bglmtcinventory.InvoiceDbContract.LMDInvoiceValueT;
import com.bgenterprise.bglmtcinventory.InvoiceDbContract.LeadTimeT;
import com.bgenterprise.bglmtcinventory.InvoiceDbContract.PriceGroupT;
import com.bgenterprise.bglmtcinventory.InvoiceDbContract.PriceT;
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
import java.util.Map;

/**
 * Database handler class for the Invoice Database.
 */

public class InvoiceDBHandler extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "invoices.db";
    private static final int DATABASE_VERSION = 1;

    //Context context;

    InvoiceDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //this.context = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i2){
        super.onUpgrade(database, i, i2);
    }

    //Insert into the Invoice Database.
    boolean onAdd_LMDInvoiceValueT(String UniqueID, String LMDID, String ItemID, String FODPhysicalCount, String TxnDate, String Type, String UnitPrice,
                                   String InvoiceQty, String InvoiceValue, String LastFODCount, String LastFODDate, String DeliverySinceLastCount,
                                   String Staff_ID, String SyncStatus) {

        try{
            SQLiteDatabase db = getWritableDatabase();
            String insertQ = "INSERT INTO LMDInvoiceValueT (UniqueID, LMDID, ItemID, FODPhysicalCount, TxnDate, Type, UnitPrice, InvoiceQty, InvoiceValue, LastFODCount," +
                    " LastFODdate, DeliverySinceLastCount, Staff_ID, SyncStatus) VALUES ('"+ UniqueID +"','" + LMDID + "','" + ItemID + "','" + FODPhysicalCount + "','" + TxnDate
                    + "','" + Type + "','" + UnitPrice + "','" + InvoiceQty + "','" + InvoiceValue + "','" + LastFODCount + "','" + LastFODDate +
                    "','" + DeliverySinceLastCount + "','" + Staff_ID + "','" + SyncStatus + "')";
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
    String getPriceGroup(String lmdID) {
        /**Selects the LMD's price-group. If none is found, its supposed to select a random one. (note there are some test pricegroups in the database of some people.**/

        String group;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT PGName FROM PriceGroupT WHERE LMDID = '" + lmdID + "'", null);
        cursor.moveToFirst();

        if(cursor.getCount() < 1){
           //LMD Not found in any PG. Hence select one random PG.
            Cursor mCur = db.rawQuery("SELECT PGName FROM PriceGroupT ORDER BY PGName ASC", null);
            cursor.moveToFirst();

            return mCur.getString(mCur.getColumnIndex("PGName"));
        }

        group = cursor.getString(cursor.getColumnIndex("PGName"));

        cursor.close();
        db.close();
        return group;
    }

    //Get the number of times, a price change has been recorded for a specific item in a price group.
    Integer getPriceChangeCount(String pgName, String itemID) {
        /**
         * Selects the count of how many times the price group and that item exists in the db. Thus signifying the amount of price changes.
         */
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
    Double getSinglePrice(String PG, String Itemid) {
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
    Double getFormerPrice(String PG, String Itemid) {
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
    String getLatestDate(String PG, String Itemid) {
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
    Integer getTotalInvoices(String LMDid, String item_id) {
        int totalInvoices;

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(InvoiceQty) FROM LMDInvoiceValueT WHERE LMDID = '" + LMDid + "' AND ItemID = '" + item_id + "'", null);
        c.moveToFirst();

        if(c.getCount() < 1){
            return 0;
        }

        totalInvoices = c.getInt(0);

        c.close();
        db.close();
        return totalInvoices;
    }

    //Get last count date.
    String getLastCountDate(String LMDid){
        String lastDate;

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM LMDInvoiceValueT WHERE LMDID = '" + LMDid + "' ORDER BY TxnDate DESC", null);
        c.moveToFirst();

        if(c.getCount() < 1){
            return "N/A";
        }

        lastDate = c.getString(c.getColumnIndex("TxnDate"));
        c.close();
        db.close();
        return lastDate;
    }

    //Get last invoice amount.
    Integer getLastInvoiceAmount(String LMDid){
        int iAmount;

        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT SUM(InvoiceValue) FROM LMDInvoiceValueT WHERE LMDID = '" + LMDid +"' GROUP BY TxnDate ORDER BY TxnDate DESC", null);
        c.moveToFirst();

        if(c.getCount() < 1){
            return 0;
        }

        iAmount = c.getInt(0);

        c.close();
        db.close();
        return iAmount;
    }

//    Integer getTotalInvoicesCount(String LMDid, String item_id) {
//        int totalInvoices;
//
//        SQLiteDatabase db = getWritableDatabase();
//        Cursor c = db.rawQuery("SELECT COUNT(InvoiceValue) FROM LMDInvoiceValueT WHERE LMDID = '" + LMDid + "' AND ItemID = '" + item_id + "'", null);
//        c.moveToFirst();
//
//        totalInvoices = c.getInt(0);
//
//        c.close();
//        db.close();
//        return totalInvoices;
//    }

    //Get total LMD's Invoices.
    Double getLMDsInvoices(String lmdid) {
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
    List<Invoices> getAllInvoices() {
        try{
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
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }


    }

    //Get a list of all the Invoices based on LMD.
    List<Invoices> getAllInvoices(String lmdid) {
        try{
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
        }catch(Exception e){
            return null;
        }

    }

    //Get a list of the specific invoices attributed to an LMD on a specific date.
    List<Invoices> getSpecificLMDInvoices(String lmd_id, String txn_date) {
        try{
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
        }catch(Exception e){
            return null;
        }

    }


    //Gets the 2nd to the most recent invoice entry for the LMD and Item for the restock calc..
    String getFormerInvDate(String LMDID, String ItemID) {

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

    Double getInvoiceSum(String lmd_id) {
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

    Double latestInvoice(String lmd_id) {
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

    HashMap<String, String> LastCountDetails(String lmdID, String item) {

        HashMap<String, String> lastCount = null;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM LMDInvoiceValueT WHERE LMDID = '"+ lmdID + "' AND ItemID = '"+ item + "' ORDER BY TxnDate DESC LIMIT 1", null);
        cursor.moveToFirst();

        if(cursor.getCount() < 1){
            lastCount = new HashMap<>();
            lastCount.put("FODPhysicalCount", "0");
            lastCount.put("TxnDate", "1987-01-01");
        }

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

    int getLeadTime(String lmdID, String itemID) {
        int leadTime = 5;
        SQLiteDatabase db = getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT " + LeadTimeT.COLUMN_LEAD_TIME + " FROM " + LeadTimeT.TABLE_NAME + " WHERE " + LeadTimeT.COLUMN_LMD_ID +
                    "=\"" + lmdID + "\" AND " + LeadTimeT.COLUMN_ITEM_ID + "='" + itemID+"'", null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                leadTime = Integer.valueOf(cursor.getString(0));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.d("getleadtimeExcep", "" + e);
        }

        db.close();
        return leadTime;
    }

    HashMap<String, String> getInvoiceDetails(String LMDID, String TxnDate, String ItemID) {
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
                String replaceQ = "REPLACE INTO PriceGroupT (LMDID, PGName) VALUES " +
                        "('"+ jsonObject.getString("LMDID") + "','" + jsonObject.getString("PGName") +"')";
                db.execSQL(replaceQ);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        db.close();
    }

    void updatePriceT(JSONArray jsonArray) {
        /**
         * TODO -> This function updates the priceT when syncing, whereas we do a price change count for the Invoice calculation.
         * Hence check with Tobi whether to remove or leave. And in the db, its ID that's Unique.
         *
         */

        SQLiteDatabase db = getWritableDatabase();
        JSONObject jsonObject;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                jsonObject = jsonArray.getJSONObject(i);
                int check = 0;
                Cursor cursor = db.rawQuery("SELECT COUNT(" + PriceT.COLUMN_ITEM_ID + ") FROM " + PriceT.TABLE_NAME + " WHERE " +
                        PriceT.COLUMN_PRICE_GROUP + " =\"" + jsonObject.get("PriceGroup") + "\" AND " + PriceT.COLUMN_ITEM_ID +
                        " = \"" + jsonObject.get("ItemID") + "\"", null);
                cursor.moveToFirst();
                if (!cursor.isAfterLast()) {
                    check = cursor.getInt(0);
                }
                cursor.close();
                ContentValues contentValues = new ContentValues();
                if (check == 0) {
                    contentValues.put(PriceT.COLUMN_PRICE_GROUP, jsonObject.getString("PriceGroup"));
                    contentValues.put(PriceT.COLUMN_ITEM, jsonObject.getString("Item"));
                    contentValues.put(PriceT.COLUMN_ITEM_ID, jsonObject.getString("ItemID"));
                    contentValues.put(PriceT.COLUMN_PRICE, jsonObject.getString("Price"));
                    contentValues.put(PriceT.COLUMN_CHANGE_DATE, jsonObject.getString("ChngDate"));

                    db.insert(PriceT.TABLE_NAME, null, contentValues);
                } else {
                    contentValues.put(PriceT.COLUMN_PRICE, jsonObject.getString("Price"));
                    contentValues.put(PriceT.COLUMN_CHANGE_DATE, jsonObject.getString("ChngDate"));
                    String where = PriceT.COLUMN_ITEM_ID + " =? AND " + PriceT.COLUMN_PRICE_GROUP + " =?";
                    String[] whereArgs = new String[]{String.valueOf(jsonObject.getString("ItemID")),
                            String.valueOf(jsonObject.getString("PriceGroup"))};

                    db.update(PriceT.TABLE_NAME, contentValues, where, whereArgs);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        db.close();

    }

    void RefreshLMDInvoiceValueT(JSONArray jsonArray) {
        SQLiteDatabase db = getWritableDatabase();
        JSONObject jsonObject;
        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                int check = 0;
                jsonObject = jsonArray.getJSONObject(i);
                Cursor cursor;
                cursor = db.rawQuery("SELECT COUNT(" + LMDInvoiceValueT.COLUMN_ID + ") FROM " + LMDInvoiceValueT.TABLE_NAME + " WHERE UniqueID = '" + jsonObject.getString("UniqueID") + "'" , null);

                cursor.moveToFirst();
                if (!cursor.isAfterLast()) {
                    check = cursor.getInt(0);
                }
                cursor.close();
                ContentValues contentValues = new ContentValues();
                if (check == 0) {
                    contentValues.put("UniqueID", jsonObject.getString("UniqueID"));
                    contentValues.put(LMDInvoiceValueT.COLUMN_ITEM_ID, jsonObject.getString("ItemID"));
                    contentValues.put(LMDInvoiceValueT.COLUMN_LMD_ID, jsonObject.getString("LMDID"));
                    contentValues.put(LMDInvoiceValueT.COLUMN_FOD_PHYSICAL_COUNT, jsonObject.getString("FODPhysicalCount"));
                    contentValues.put(LMDInvoiceValueT.COLUMN_TXN_DATE, jsonObject.getString("TxnDate"));
                    contentValues.put(LMDInvoiceValueT.COLUMN_TYPE, jsonObject.getString("Type"));
                    contentValues.put(LMDInvoiceValueT.COLUMN_UNIT_PRICE, jsonObject.getString("UnitPrice"));
                    contentValues.put(LMDInvoiceValueT.COLUMN_INVOICE_QTY, jsonObject.getString("InvoiceQty"));
                    contentValues.put(LMDInvoiceValueT.COLUMN_INVOICE_VALUE, jsonObject.getString("InvoiceValue"));
                    contentValues.put(LMDInvoiceValueT.COLUMN_LAST_FOD_COUNT, jsonObject.getString("LastFODCount"));
                    contentValues.put(LMDInvoiceValueT.COLUMN_LAST_FOD_DATE, jsonObject.getString("LastFODdate"));
                    contentValues.put(LMDInvoiceValueT.COLUMN_DELIVERY_SINCE_LAST_COUNT, jsonObject.getString("DeliverySinceLastCount"));
                    contentValues.put(LMDInvoiceValueT.COLUMN_HOLDING_COST, jsonObject.getString("HoldingCost"));
                    contentValues.put(LMDInvoiceValueT.COLUMN_SYNC_DATE, jsonObject.getString("SyncDate"));
                    contentValues.put(LMDInvoiceValueT.COLUMN_SYNC_STATUS, jsonObject.getString("SyncStatus"));
                    contentValues.put(LMDInvoiceValueT.COLUMN_STAFF_ID, jsonObject.getString("Staff_ID"));

                    db.insert(LMDInvoiceValueT.TABLE_NAME, null, contentValues);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        db.close();
    }

    void updateLeadTimeT(JSONArray jsonArray) {
        SQLiteDatabase db = getWritableDatabase();
        JSONObject jsonObject;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                jsonObject = jsonArray.getJSONObject(i);
                int check = 0;
                Cursor cursor = db.rawQuery("SELECT COUNT(" + LeadTimeT.COLUMN_LMD_ID + ") FROM " + LeadTimeT.TABLE_NAME + " WHERE " +
                        LeadTimeT.COLUMN_LMD_ID + " =\"" + jsonObject.get("LmdID") + "\" AND " + LeadTimeT.COLUMN_ITEM_ID + "=\"" +
                        jsonObject.getString("ItemID") + "\"", null);
                cursor.moveToFirst();
                if (!cursor.isAfterLast()) {
                    check = cursor.getInt(0);
                }
                cursor.close();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format_ = new SimpleDateFormat("yyyy-MM-dd");
                Date today = new Date();
                String today_str = format_.format(today);
                ContentValues contentValues = new ContentValues();
                if (check == 0) {
                    contentValues.put(LeadTimeT.COLUMN_LMD_ID, jsonObject.getString("LmdID"));
                    contentValues.put(LeadTimeT.COLUMN_LEAD_TIME, jsonObject.getString("LeadTime"));
                    contentValues.put(LeadTimeT.COLUMN_SYNC_DATE, today_str);
                    contentValues.put(LeadTimeT.COLUMN_ITEM_ID, jsonObject.getString("ItemID"));

                    db.insert(LeadTimeT.TABLE_NAME, null, contentValues);
                } else {
                    contentValues.put(LeadTimeT.COLUMN_LEAD_TIME, jsonObject.getString("LeadTime"));
                    contentValues.put(LeadTimeT.COLUMN_SYNC_DATE, today_str);
                    String where = LeadTimeT.COLUMN_LMD_ID + " =? AND " + LeadTimeT.COLUMN_ITEM_ID + " =?";
                    String[] whereArgs = new String[]{String.valueOf(jsonObject.getString("LmdID")), String.valueOf(jsonObject.getString("ItemID"))};

                    db.update(LeadTimeT.TABLE_NAME, contentValues, where, whereArgs);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        db.close();
    }

    ArrayList<Map<String, String>> uploadLMDInvValueT() {
        Map<String, String> map;
        ArrayList<Map<String, String>> wordList = new ArrayList<>();
        try{
            Cursor cursor;
            SQLiteDatabase db = getWritableDatabase();
            cursor = db.rawQuery("SELECT * FROM LMDInvoiceValueT WHERE SyncStatus = 'no' OR 'No'", null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                map = new HashMap<>();
                map.put("ID", cursor.getString(cursor.getColumnIndex("ID")));
                map.put("UniqueID", cursor.getString(cursor.getColumnIndex("UniqueID")));
                map.put("LMDID", cursor.getString(cursor.getColumnIndex("LMDID")));
                map.put("ItemID", cursor.getString(cursor.getColumnIndex("ItemID")));
                map.put("FODPhysicalCount", cursor.getString(cursor.getColumnIndex("FODPhysicalCount")));
                map.put("TxnDate", cursor.getString(cursor.getColumnIndex("TxnDate")));
                map.put("Type", cursor.getString(cursor.getColumnIndex("Type")));
                map.put("UnitPrice", cursor.getString(cursor.getColumnIndex("UnitPrice")));
                map.put("InvoiceQty", cursor.getString(cursor.getColumnIndex("InvoiceQty")));
                map.put("InvoiceValue", cursor.getString(cursor.getColumnIndex("InvoiceValue")));
                map.put("LastFODCount", cursor.getString(cursor.getColumnIndex("LastFODCount")));
                map.put("LastFODdate", cursor.getString(cursor.getColumnIndex("LastFODdate")));
                map.put("DeliverySinceLastCount", cursor.getString(cursor.getColumnIndex("DeliverySinceLastCount")));
                map.put("HoldingCost", cursor.getString(cursor.getColumnIndex("HoldingCost")));
                map.put("Staff_ID", cursor.getString(cursor.getColumnIndex("Staff_ID")));

                wordList.add(map);
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return wordList;
    }

    void updateLMDInvValueTSyncStatus(JSONArray jsonArray) {
        JSONObject jsonObject;
        SQLiteDatabase db = getWritableDatabase();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                jsonObject = jsonArray.getJSONObject(i);
                db.execSQL("UPDATE LMDInvoiceValueT SET SyncStatus = '" + jsonObject.getString("SyncStatus") + "', SyncDate = '" + jsonObject.getString("SyncDate") + "'" +
                        " WHERE UniqueID = \"" + jsonObject.getString("UniqueID") + "\"");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("HERE", e + "");
            }
        }
        db.close();
    }

    boolean preventProductCountTwice(String LMDid, String itemId){
        //The Idea behind this function is to ensure that the specific product has not been counted today. Else, prevent data entry.
        String type = "FOD";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        String today = dateFormat.format(date);

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM LMDInvoiceValueT WHERE LMDID = '" + LMDid + "' AND ItemID = '" + itemId + "' AND Type = '" + type + "' AND TxnDate = '" + today +"'", null);
        cursor.moveToFirst();

        if(cursor.getCount() < 1){
            //Means a count for that product on that LMD has been done today.
            return true;
        }

        cursor.close();
        db.close();
        return false;
    }

    public boolean isLMDinPriceGroup(String lmdid){
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT PGName FROM PriceGroupT WHERE LMDID = '"+ lmdid +"'", null);
        if(c.getCount() < 1){
            return false;
        }
        c.close();
        db.close();
        return true;
    }



}
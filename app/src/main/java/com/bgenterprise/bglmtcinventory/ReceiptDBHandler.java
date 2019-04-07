package com.bgenterprise.bglmtcinventory;

/**
 * Database handler class for Receipts database.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bgenterprise.bglmtcinventory.InvoiceDbContract.receipt_table;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReceiptDBHandler extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "receipts.db";
    private static final int DATABASE_VERSION = 1;

    Context context;

    public ReceiptDBHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i2){
        super.onUpgrade(database, i, i2);
    }

    public boolean onAdd(String receipt_id, String lmd_name, String lmd_id, String lmd_hub, String amount,
                         String moneycollectedby, String staff_id, String date, String sync_status, String appVersion){
        //Inserts entries into the Receipt table.

        try{
            SQLiteDatabase db = getWritableDatabase();
            String insertQ = "INSERT INTO receipt_table (receipt_id, lmd_name, lmd_id, lmd_hub, amount, moneycollectedby, staff_id, date, sync_status, appVersion) VALUES " +
                    "('" + receipt_id + "','" + lmd_name + "','" + lmd_id + "','" + lmd_hub + "','" + amount + "','" + moneycollectedby + "','" + staff_id + "','" + date + "','" + sync_status + "','" + appVersion + "')";
            db.execSQL(insertQ);
            db.close();
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public List<Receipts> getAllReceipts(){
        //Gets a list of all the receipts existing in the database.
        List<Receipts> receipts = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor =  db.rawQuery("SELECT lmd_name, lmd_id, COUNT(lmd_id) FROM receipt_table GROUP BY lmd_id", null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            receipts.add(new Receipts(cursor.getString(cursor.getColumnIndex("lmd_name")), cursor.getString(cursor.getColumnIndex("lmd_id")), cursor.getString(cursor.getColumnIndex("COUNT(lmd_id)"))));
            cursor.moveToNext();
        }

        cursor.close();
        db.close();
        return receipts;
    }

    public List<Receipts> getLMDReceipts(String lmd_id){
        //Gets a list of all the receipts in the database for a specific LMD.
        List<Receipts> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM receipt_table WHERE lmd_id = '" + lmd_id + "'", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            list.add(new Receipts(cursor.getString(cursor.getColumnIndex("receipt_id")), cursor.getString(cursor.getColumnIndex("amount")), cursor.getString(cursor.getColumnIndex("moneycollectedby")),
                    cursor.getString(cursor.getColumnIndex("date")), cursor.getString(cursor.getColumnIndex("sync_status"))));
            cursor.moveToNext();
        }

        cursor.close();
        db.close();
        return list;
    }

    public Double getSumReceipts(String lmd_id){
        double sum = 0.00;

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(amount) FROM receipt_table WHERE lmd_id = '" + lmd_id + "'", null);
        cursor.moveToFirst();

        if(cursor.getCount() < 1){
            return sum;
        }

        do{
            sum = cursor.getDouble(cursor.getColumnIndex("SUM(amount)"));
            cursor.moveToNext();
        }while(!cursor.isAfterLast());

        cursor.close();
        db.close();

        return sum;
    }

    ArrayList<Map<String, String>> uploadReceiptT() {
        Map<String, String> map;
        ArrayList<Map<String, String>> wordList = new ArrayList<>();
        Cursor cursor;
        SQLiteDatabase db = getWritableDatabase();
        cursor = db.rawQuery("SELECT " + receipt_table.COLUMN_RECEIPT_ID + "," + receipt_table.COLUMN_LMD_NAME + "," + receipt_table.COLUMN_LMD_ID
                + "," + receipt_table.COLUMN_LMD_HUB + "," + receipt_table.COLUMN_AMOUNT + "," + receipt_table.COLUMN_MONEY_COLLECTED_BY + "," +
                receipt_table.COLUMN_STAFF_ID + "," + receipt_table.COLUMN_DATE + "," + receipt_table.COLUMN_APP_VERSION
                + " FROM " + receipt_table.TABLE_NAME + " WHERE " + receipt_table.COLUMN_SYNC_STATUS + "='no'", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            map = new HashMap<>();
            map.put("receipt_id", cursor.getString(0));
            map.put("lmd_name", cursor.getString(1));
            map.put("lmd_id", cursor.getString(2));
            map.put("lmd_hub", cursor.getString(3));
            map.put("amount", cursor.getString(4));
            map.put("moneycollectedby", cursor.getString(5));
            map.put("staff_id", cursor.getString(6));
            map.put("date", cursor.getString(7));
            map.put("appVersion", cursor.getString(8));

            wordList.add(map);
            cursor.moveToNext();
        }
        cursor.close();
        return wordList;
    }

    void updateReceiptTableSyncStatus(JSONArray jsonArray) {
        JSONObject jsonObject;
        SQLiteDatabase db = getWritableDatabase();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                jsonObject = jsonArray.getJSONObject(i);
                db.execSQL("UPDATE " + receipt_table.TABLE_NAME + " SET " + receipt_table.COLUMN_SYNC_STATUS + " = '" + jsonObject.getString("SyncStatus")
                        + "', " + receipt_table.COLUMN_SYNC_DATE + " = \"" + jsonObject.getString("SyncDate") + "\" WHERE " +
                        receipt_table.COLUMN_RECEIPT_ID + " = \"" + jsonObject.getString("receipt_id") + "\"");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("HERE", e + "");
            }
        }
    }
}

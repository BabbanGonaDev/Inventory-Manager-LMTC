package com.bgenterprise.bglmtcinventory;

/**
 * Database handler class for Receipts database.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

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
}

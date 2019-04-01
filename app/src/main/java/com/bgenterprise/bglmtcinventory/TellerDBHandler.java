package com.bgenterprise.bglmtcinventory;

/**
 * The database handler class that handles all functions of the Teller Database.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.sql.SQLInput;
import java.util.ArrayList;
import java.util.List;

public class TellerDBHandler extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "tellers.db";
    private static final int DATABASE_VERSION = 1;

    Context context;

    public TellerDBHandler(Context context) {
        //Constructor.
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i2) {
        super.onUpgrade(database, i, i2);
    }

    public boolean onAdd(String teller_id, String teller_amount, String teller_bank, String receipt_id, String receipt_amount,
                         String teller_date, String sync_status, String app_version) {

        //Inserts entries into the teller table.
        try {
            SQLiteDatabase db = getWritableDatabase();
            String insertQ = "INSERT INTO teller_table (teller_id, teller_amount, teller_bank, receipt_id, receipt_amount, teller_date, sync_status, app_version) VALUES " +
                    "('" + teller_id + "','" + teller_amount + "','" + teller_bank + "','" + receipt_id + "','" + receipt_amount + "','" + teller_date + "','" + sync_status + "','" + app_version + "')";
            db.execSQL(insertQ);
            db.close();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public List<Tellers> getAllTellers() {
        //Gets a list of all the tellers existing in the Teller table.
        List<Tellers> tellers = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT teller_id, teller_amount, teller_bank, COUNT(teller_id), teller_date FROM teller_table GROUP BY teller_id", null);
        if (cursor.moveToFirst()) {
            do {
                tellers.add(new Tellers(cursor.getString(cursor.getColumnIndex("teller_id")), cursor.getString(cursor.getColumnIndex("teller_amount")), cursor.getString(cursor.getColumnIndex("teller_bank")),
                        cursor.getString(cursor.getColumnIndex("COUNT(teller_id)")), cursor.getString(cursor.getColumnIndex("teller_date"))));
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }

        cursor.close();
        db.close();
        return tellers;
    }

    public List<Tellers> getTellerReceipts(String teller_id) {
        //Gets specific list of all receipts under a teller.
        List<Tellers> TellerReceipts = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM teller_table WHERE teller_id = '" + teller_id + "'", null);

        if (cursor.moveToFirst()) {
            do {
                TellerReceipts.add(new Tellers(cursor.getString(cursor.getColumnIndex("receipt_id")), cursor.getString(cursor.getColumnIndex("receipt_amount"))));
                cursor.moveToNext();
            } while (!cursor.isAfterLast());

        }

        cursor.close();
        db.close();
        return TellerReceipts;
    }

    public boolean checkDuplicateTellerReceipt(String receipt_id, String teller_id) {

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM teller_table WHERE receipt_id = '" + receipt_id + "' AND teller_id = '" + teller_id + "'", null);
        cursor.moveToFirst();

        if (cursor.getCount() >= 1) {
            return true;
        } else if (cursor.getCount() == 0) {
            return false;
        }

        cursor.close();
        db.close();
        return true;
    }

    public boolean checkTotalTellerAmount(String tellerID, Double tellerAmount) {
        double current_total;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(receipt_amount) FROM teller_table WHERE teller_id = '" + tellerID + "'", null);
        cursor.moveToFirst();
        do {
            current_total = cursor.getDouble(cursor.getColumnIndex("SUM(receipt_amount)"));
        } while (!cursor.isAfterLast());


        if (current_total == tellerAmount) {
            return true;
        } else {
            return false;
        }
    }

    public Integer getTotalReceiptAmount(String Teller_ID){

        Integer amount;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(receipt_amount) FROM teller_table WHERE teller_id = '" + Teller_ID + "'", null);
        cursor.moveToFirst();
        do{
            amount = cursor.getInt(cursor.getColumnIndex("SUM(receipt_amount)"));
            cursor.moveToNext();
        }while (!cursor.isAfterLast());

        cursor.close();
        db.close();

        return Integer.valueOf(amount);
    }

}

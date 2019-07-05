package com.bgenterprise.bglmtcinventory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bgenterprise.bglmtcinventory.LmdDbContract.LMDT;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LMD_DBHandler extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "lmd.db";
    private static final int DATABASE_VERSION = 1;
    //Context context;


    public LMD_DBHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //this.context = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i2){
        super.onUpgrade(database, i, i2);
    }

    public List<LMD> getLMTCLMDs(String LMTCID){
        //Gets a list of all the LMDs assigned to that LMTC.
        List<LMD> lmdList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM LMDT WHERE LMTC_ID = '"+ LMTCID + "'", null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            lmdList.add(new LMD(cursor.getString(cursor.getColumnIndex("LMD_ID")), cursor.getString(cursor.getColumnIndex("LMD_Name")), cursor.getString(cursor.getColumnIndex("LMTC_ID"))));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return lmdList;
    }

    void updateLMDT(JSONArray jsonArray) {
        SQLiteDatabase db = getWritableDatabase();

        //Truncate the table here before beginning insertion.
        //This works because we are sending the entire json array to the function.

        String TruncQ = "DELETE FROM " + LMDT.TABLE_NAME;
        db.execSQL(TruncQ);
        JSONObject jsonObject;

        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                jsonObject = jsonArray.getJSONObject(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put(LMDT.COLUMN_LMD_ID, jsonObject.getString("LMD_ID"));
                contentValues.put(LMDT.COLUMN_LMD_NAME, jsonObject.getString("LMD_Name"));
                contentValues.put(LMDT.COLUMN_LMTC_ID, jsonObject.getString("LMTC_ID"));

                db.insert(LMDT.TABLE_NAME, null, contentValues);
                db.close();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    void updateLMDTold(JSONArray jsonArray) {
        SQLiteDatabase db = getWritableDatabase();
        JSONObject jsonObject;
        for (int i = 0; i < jsonArray.length(); i++) {
            int check = 0;
            try {
                jsonObject = jsonArray.getJSONObject(i);
                Cursor cursor;
                cursor = db.rawQuery("SELECT COUNT(" + LMDT.COLUMN_LMD_ID + ") FROM " + LMDT.TABLE_NAME + " WHERE " + LMDT.COLUMN_LMD_ID +
                        " = \"" + jsonObject.getString("LMD_ID") + "\"", null);
                cursor.moveToFirst();
                if (!cursor.isAfterLast()) {
                    check = cursor.getInt(0);
                }
                cursor.close();
                ContentValues contentValues = new ContentValues();
                if (check == 0) {
                    contentValues.put(LMDT.COLUMN_LMD_ID, jsonObject.getString("LMD_ID"));
                    contentValues.put(LMDT.COLUMN_LMD_NAME, jsonObject.getString("LMD_Name"));
                    contentValues.put(LMDT.COLUMN_LMTC_ID, jsonObject.getString("LMTC_ID"));

                    db.insert(LMDT.TABLE_NAME, null, contentValues);
                } else {
                    contentValues.put(LMDT.COLUMN_LMD_ID, jsonObject.getString("LMD_ID"));
                    contentValues.put(LMDT.COLUMN_LMD_NAME, jsonObject.getString("LMD_Name"));
                    contentValues.put(LMDT.COLUMN_LMTC_ID, jsonObject.getString("LMTC_ID"));
                    String where = LMDT.COLUMN_LMD_ID + " =?";
                    String[] whereArgs = new String[]{String.valueOf(jsonObject.getString("LMD_ID"))};

                    db.update(LMDT.TABLE_NAME, contentValues, where, whereArgs);
                }

                db.close();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public boolean isLMTCInDB(String lmtcid){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM LMDT WHERE LMTC_ID = '"+ lmtcid +"'", null);
        if(cursor.getCount() < 1){
            return false;
        }
        cursor.close();
        db.close();
        return true;
    }
}

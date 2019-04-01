package com.bgenterprise.bglmtcinventory;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class RestockDBHandler extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "restocks.db";
    private static final int DATABASE_VERSION = 1;

    Context context;

    public RestockDBHandler(Context context){
        //Class constructor.
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i2){
        super.onUpgrade(database, i, i2);
    }

    public boolean onAdd_RestockT(String LMDID, String ItemID, String RestockValue, String LMDKey, String Count, String RequestDate){

        //Insert entries into the RestockT table.
        try{
            SQLiteDatabase db = getWritableDatabase();
            String insertQ = "INSERT INTO RestockT(LMDID, ItemID, RestockValue, LMDKey, Count, RequestDate) VALUES ('"+ LMDID +"','"+ ItemID +"','"+ RestockValue +"','"+ LMDKey +"','"+ Count +"','"+ RequestDate +"')";

            db.execSQL(insertQ);
            db.close();
            return true;

        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public List<Restocks> getAllRestocks(){
        //Gets a list of all the restocks existing in the restockT table.
        List<Restocks> restocks = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT LMDID, ItemID FROM RestockT ORDER BY RequestDate DESC", null);
        if(cursor.moveToFirst()){
            do{
                restocks.add(new Restocks(cursor.getString(cursor.getColumnIndex("LMDID")), cursor.getString(cursor.getColumnIndex("ItemID"))));
                cursor.moveToNext();
            }while(!cursor.isAfterLast());
        }

        cursor.close();
        db.close();
        return restocks;
    }



}

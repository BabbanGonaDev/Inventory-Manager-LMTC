package com.bgenterprise.bglmtcinventory;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;


import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.HashMap;

public class InvContentProvider extends ContentProvider {

    private static final String TABLE_NAME = "Inventory03T";

    public static final String AUTHORITY = "com.bgenterprise.bglmtcinventory";
    private static final UriMatcher uriMatch;

    //URL provided to all other applications, giving only access to one table.
    static final String URL = "content://" + AUTHORITY + "/" + TABLE_NAME;

    private static final int theInventory = 1;
    private static final int theInventory_ID = 2;
    private static HashMap<String, String> InventoryProjectionMap;

    InventoryDBHandler dbHelper;

    @Override
    public int delete(Uri uri, String where, String[] whereArgs){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatch.match(uri)){
            case theInventory:
                //This part will delete all records in the table, if I add the delete statement here.
                break;
            case theInventory_ID:
                //We are specifying which row to delete using the UniqueID.
                where = where + "UniqueID = " + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        int count = db.delete(TABLE_NAME, where, whereArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri){
        switch (uriMatch.match(uri)){
            case theInventory:
                return Inventory.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues){
        if(uriMatch.match(uri) != theInventory){
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        //First check what type of LMD ID is being passed.
        String passedLMD = initialValues.getAsString("LMDID");
        if(passedLMD.startsWith("T")){
            LMDMapDBHandler lmdMapDBHandler = new LMDMapDBHandler(getContext());


            //Check the LMDMap and put inside the content values.
            Log.d("CHECK", "Incoming Staff ID: " + passedLMD);
            Log.d("CHECK", "Replacing with: " + lmdMapDBHandler.getLMDID(passedLMD));

            //Insert it.
            initialValues.put("LMDID", lmdMapDBHandler.getLMDID(passedLMD));
        }

        ContentValues values;
        if(initialValues != null){
            values = new ContentValues(initialValues);
        }else{
            values = new ContentValues();
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(TABLE_NAME, Inventory.COLUMN_UNIQUEID, values);
        if(rowId > 0){
            Uri invUri = ContentUris.withAppendedId(Inventory.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(invUri, null);
            return invUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public boolean onCreate(){
        dbHelper = new InventoryDBHandler(getContext());
        return true;

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_NAME);
        queryBuilder.setProjectionMap(InventoryProjectionMap);

        switch (uriMatch.match(uri)){
            case theInventory:
                break;
            case theInventory_ID:
                selection = selection + "UniqueID = " + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        switch (uriMatch.match(uri)){
            case theInventory:
                count = db.update(TABLE_NAME, values, where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        uriMatch = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatch.addURI(AUTHORITY, TABLE_NAME, theInventory);
        uriMatch.addURI(AUTHORITY, TABLE_NAME + "/#", theInventory_ID);

        InventoryProjectionMap = new HashMap<String, String>();
        InventoryProjectionMap.put(Inventory.COLUMN_ITEMID, Inventory.COLUMN_ITEMID);
        InventoryProjectionMap.put(Inventory.COLUMN_ITEMNAME, Inventory.COLUMN_ITEMNAME);
        InventoryProjectionMap.put(Inventory.COLUMN_LMDID, Inventory.COLUMN_LMDID);
        InventoryProjectionMap.put(Inventory.COLUMN_NOTES, Inventory.COLUMN_NOTES);
        InventoryProjectionMap.put(Inventory.COLUMN_STAFFID, Inventory.COLUMN_STAFFID);
        InventoryProjectionMap.put(Inventory.COLUMN_SYNCDATE, Inventory.COLUMN_SYNCDATE);
        InventoryProjectionMap.put(Inventory.COLUMN_SYNCSTATUS, Inventory.COLUMN_SYNCSTATUS);
        InventoryProjectionMap.put(Inventory.COLUMN_TXNDATE, Inventory.COLUMN_TXNDATE);
        InventoryProjectionMap.put(Inventory.COLUMN_TYPE, Inventory.COLUMN_TYPE);
        InventoryProjectionMap.put(Inventory.COLUMN_UNIQUEID, Inventory.COLUMN_UNIQUEID);
        InventoryProjectionMap.put(Inventory.COLUMN_UNIT, Inventory.COLUMN_UNIT);
        InventoryProjectionMap.put(Inventory.COLUMN_UNITPRICE, Inventory.COLUMN_UNITPRICE);

    }
}

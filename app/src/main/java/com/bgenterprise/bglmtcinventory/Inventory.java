package com.bgenterprise.bglmtcinventory;

import android.net.Uri;
import android.provider.BaseColumns;

public final class Inventory implements BaseColumns {

    private Inventory() {

    }

    public static final Uri CONTENT_URI = Uri.parse("content://" + InvContentProvider.AUTHORITY + "/Inventory03T");

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.jwei512.Inventory03T";

    //Names for the columns in the Inventory03T table.

    public static final String COLUMN_UNIQUEID = "UniqueID";
    public static final String COLUMN_TXNDATE = "TxnDate";
    public static final String COLUMN_LMDID = "LMDID";
    public static final String COLUMN_ITEMID = "ItemID";
    public static final String COLUMN_ITEMNAME = "ItemName";
    public static final String COLUMN_UNIT = "Unit";
    public static final String COLUMN_TYPE = "Type";
    public static final String COLUMN_UNITPRICE = "UnitPrice";
    public static final String COLUMN_NOTES = "Notes";
    public static final String COLUMN_SYNCDATE = "SyncDate";
    public static final String COLUMN_SYNCSTATUS = "SyncStatus";
    public static final String COLUMN_STAFFID = "Staff_ID";




}

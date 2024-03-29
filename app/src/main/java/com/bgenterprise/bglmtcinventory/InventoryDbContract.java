package com.bgenterprise.bglmtcinventory;

import android.provider.BaseColumns;

public class InventoryDbContract {

    public static abstract class Inventory03T implements BaseColumns{
        public static final String TABLE_NAME = "Inventory03T";
        public static final String COLUMN_ID = "ID";
        public static final String COLUMN_UNIQUE_ID = "UniqueID";
        public static final String COLUMN_TXN_DATE = "TxnDate";
        public static final String COLUMN_LMD_ID = "LMDID";
        public static final String COLUMN_ITEM_ID = "ItemID";
        public static final String COLUMN_ITEM_NAME = "ItemName";
        public static final String COLUMN_UNIT = "Unit";
        public static final String COLUMN_TYPE = "Type";
        public static final String COLUMN_UNIT_PRICE = "UnitPrice";
        public static final String COLUMN_NOTES = "Notes";
        public static final String COLUMN_SYNC_DATE = "SyncDate";
        public static final String COLUMN_SYNC_STATUS = "SyncStatus";
        public static final String COLUMN_STAFF_ID = "Staff_ID";

    }

    public static abstract class HoldingCostT implements BaseColumns{
        public static final String TABLE_NAME = "HoldingCostT";
        public static final String COLUMN_ID = "ID";
        public static final String COLUMN_ITEM_ID = "ItemID";
        public static final String COLUMN_HOLDING_COST = "HoldingCost";
        public static final String COLUMN_LAST_CHANGE_DATE = "LastDateChange";

    }
}

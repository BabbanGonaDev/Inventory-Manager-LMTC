package com.bgenterprise.bglmtcinventory;

import android.provider.BaseColumns;

public class InvoiceDbContract {

    public static abstract class LMDInvoiceValueT implements BaseColumns{
        public static final String TABLE_NAME = "LMDInvoiceValueT";
        public static final String COLUMN_ID = "ID";
        public static final String COLUMN_LMD_ID = "LMDID";
        public static final String COLUMN_ITEM_ID = "ItemID";
        public static final String COLUMN_FOD_PHYSICAL_COUNT = "FODPhysicalCount";
        public static final String COLUMN_INVOICE_QTY = "InvoiceQty";
        public static final String COLUMN_TXN_DATE = "TxnDate";
        public static final String COLUMN_TYPE = "Type";
        public static final String COLUMN_UNIT_PRICE = "UnitPrice";
        public static final String COLUMN_INVOICE_VALUE = "InvoiceValue";
        public static final String COLUMN_LAST_FOD_COUNT = "LastFODCount";
        public static final String COLUMN_LAST_FOD_DATE = "LastFODdate";
        public static final String COLUMN_DELIVERY_SINCE_LAST_COUNT = "DeliverySinceLastCount";
        public static final String COLUMN_HOLDING_COST = "HoldingCost";
        public static final String COLUMN_SYNC_DATE = "SyncDate";
        public static final String COLUMN_SYNC_STATUS = "SyncStatus";
        public static final String COLUMN_STAFF_ID = "Staff_ID";

    }

    public static abstract class PriceGroupT implements BaseColumns{
        public static final String TABLE_NAME = "PriceGroupT";
        public static final String COLUMN_LMD_ID = "LMDID";
        public static final String COLUMN_PG_NAME = "PGName";

    }

    public static abstract class PriceT implements BaseColumns{
        public static final String TABLE_NAME = "PriceT";
        public static final String COLUMN_PRICE_GROUP = "PriceGroup";
        public static final String COLUMN_ITEM = "Item";
        public static final String COLUMN_ITEM_ID = "ItemID";
        public static final String COLUMN_PRICE = "Price";
        public static final String COLUMN_CHANGE_DATE = "ChngDate";

    }

    public static abstract class receipt_table implements BaseColumns {
        public static final String TABLE_NAME = "receipt_table";
        public static final String COLUMN_RECEIPT_ID = "receipt_id";
        public static final String COLUMN_LMD_NAME = "lmd_name";
        public static final String COLUMN_LMD_ID = "lmd_id";
        public static final String COLUMN_LMD_HUB = "lmd_hub";
        public static final String COLUMN_AMOUNT = "amount";
        public static final String COLUMN_MONEY_COLLECTED_BY = "moneycollectedby";
        public static final String COLUMN_STAFF_ID = "staff_id";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_SYNC_STATUS = "SyncStatus";
        public static final String COLUMN_APP_VERSION = "appVersion";
        public static final String COLUMN_SYNC_DATE = "SyncDate";
    }

    public static abstract class RestockT implements BaseColumns {
        public static final String TABLE_NAME = "RestockT";
        public static final String COLUMN_LMD_ID = "LMDID";
        public static final String COLUMN_ITEM_ID = "ItemID";
        public static final String COLUMN_RESTOCK_VALUE = "RestockValue";
        public static final String COLUMN_LMD_KEY = "LMDKey";
        public static final String COLUMN_COUNT = "Count";
        public static final String COLUMN_REQUEST_DATE = "RequestDate";
        public static final String COLUMN_SYNC_STATUS = "SyncStatus";
        public static final String COLUMN_SYNC_DATE = "SyncDate";
        public static final String COLUMN_STAFF_ID = "Staff_ID";

    }

    public static abstract class teller_table implements BaseColumns {
        public static final String TABLE_NAME = "teller_table";
        public static final String COLUMN_TELLER_ID = "teller_id";
        public static final String COLUMN_TELLER_AMOUNT = "teller_amount";
        public static final String COLUMN_TELLER_BANK = "teller_bank";
        public static final String COLUMN_RECEIPT_ID = "receipt_id";
        public static final String COLUMN_RECEIPT_AMOUNT = "receipt_amount";
        public static final String COLUMN_TELLER_DATE = "teller_date";
        public static final String COLUMN_SYNC_STATUS = "SyncStatus";
        public static final String COLUMN_APP_VERSION = "app_version";
        public static final String COLUMN_SYNC_DATE = "SyncDate";
        public static final String COLUMN_STAFF_ID = "Staff_ID";

    }


}

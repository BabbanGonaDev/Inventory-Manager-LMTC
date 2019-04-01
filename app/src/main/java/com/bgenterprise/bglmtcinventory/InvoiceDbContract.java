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

}

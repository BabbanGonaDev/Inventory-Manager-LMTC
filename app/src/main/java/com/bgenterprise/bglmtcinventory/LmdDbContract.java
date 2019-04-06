package com.bgenterprise.bglmtcinventory;

import android.provider.BaseColumns;

public class LmdDbContract {
    public static abstract class LMDT implements BaseColumns {
        public static final String TABLE_NAME = "LMDT";
        public static final String COLUMN_LMD_ID = "LMD_ID";
        public static final String COLUMN_LMD_NAME = "LMD_Name";
        public static final String COLUMN_LMTC_ID = "LMTC_ID";
    }
}

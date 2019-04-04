package com.bgenterprise.bglmtcinventory;

/**
 * A session management class used for temporarily storing information in the shared preferences to be used by other activities and classes.
 */

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    //Shared Preferences
    SharedPreferences prefs;

    //Editor for the shared preferences
    SharedPreferences.Editor editor;

    //Context
    Context _context;

    //Shared pref mode
    int PRIVATE_MODE = 0;

    //Pref file Name
    private static final String PREF_NAME = "LMTC Inventory Manager";

    public static final String KEY_STAFF_NAME = "staff_name";
    public static final String KEY_STAFF_ID = "staff_id";

    public static final String KEY_LMD_ID = "lmd_id";
    public static final String KEY_LMD_NAME = "lmd_name";
    public static final String KEY_LMD_HUB = "lmd_hub";

    public static final String KEY_RECEIPT_ID = "receipt_id";

    public static final String KEY_TEAM_MEMBER_NAME = "team_member_name";
    public static final String KEY_TEAM_MEMBER_ID = "team_member_id";


    public static final String KEY_PRODUCT_ID = "product_id";
    public static final String KEY_PRODUCT_NAME = "product_name";

    public static final String KEY_APP_VERSION = "app_version";

    public static final String KEY_INVOICE = "invoice";
    public static final String KEY_TOTAL_INVOICE = "total_invoice";
    public static final String KEY_INVOICE_PAID = "invoice_paid";
    public static final String KEY_INVOICE_DUE = "invoice_due";
    public static final String KEY_INVOICE_ID = "invoice_id";

    public static final String KEY_TELLER_ID = "teller_id";
    public static final String KEY_TELLER_AMOUNT = "teller_amount";
    public static final String KEY_TELLER_BANK = "teller_bank";
    public static final String KEY_TELLER_DATE = "teller_date";

    public static final String KEY_DATE_1 = "date_1";


    //Constructor
    public SessionManager(Context context){
        this._context = context;
        prefs = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = prefs.edit();
    }

    public void CREATE_LOGIN_SESSION(String staff_name, String staff_id, String app_version){
        editor.putString(KEY_STAFF_NAME, staff_name);
        editor.putString(KEY_STAFF_ID, staff_id);
        editor.putString(KEY_APP_VERSION, app_version);
        editor.commit();
    }

    public void CREATE_LMD_SESSION(String lmd_name, String lmd_id, String lmd_hub){
        editor.putString(KEY_LMD_NAME, lmd_name);
        editor.putString(KEY_LMD_ID, lmd_id);
        editor.putString(KEY_LMD_HUB, lmd_hub);
        editor.commit();
    }

    public void CREATE_RECEIPT_SESSION(String receipt_id){
        editor.putString(KEY_RECEIPT_ID, receipt_id);
        editor.commit();
    }

    public void CREATE_TEAM_MEMBER_SESSION(String team_member_name, String team_member_id){
        editor.putString(KEY_TEAM_MEMBER_NAME, team_member_name);
        editor.putString(KEY_TEAM_MEMBER_ID, team_member_id);
        editor.commit();
    }

    public void CREATE_DATE(String date){
        editor.putString(KEY_DATE_1, date);
        editor.commit();
    }

    public void CREATE_PRODUCT_SESSION(String product_name, String product_id){
        editor.putString(KEY_PRODUCT_NAME, product_name);
        editor.putString(KEY_PRODUCT_ID, product_id);
        editor.commit();
    }


    public void CREATE_TELLER_SESSION(String teller_id, String teller_amount, String teller_bank, String teller_date){
        editor.putString(KEY_TELLER_ID, teller_id);
        editor.putString(KEY_TELLER_AMOUNT, teller_amount);
        editor.putString(KEY_TELLER_BANK, teller_bank);
        editor.putString(KEY_TELLER_DATE, teller_date);
        editor.commit();
    }

    public HashMap<String, String> getLoginDetails(){
        HashMap<String, String> login = new HashMap<>();

        login.put(KEY_STAFF_NAME, prefs.getString(KEY_STAFF_NAME, ""));
        login.put(KEY_STAFF_ID, prefs.getString(KEY_STAFF_ID, ""));
        return login;

    }

    public HashMap<String, String> getAllDetails(){
        HashMap<String, String> all = new HashMap<>();

        all.put(KEY_STAFF_NAME, prefs.getString(KEY_STAFF_NAME, ""));
        all.put(KEY_STAFF_ID, prefs.getString(KEY_STAFF_ID, ""));
        all.put(KEY_APP_VERSION, prefs.getString(KEY_APP_VERSION, ""));
        all.put(KEY_RECEIPT_ID, prefs.getString(KEY_RECEIPT_ID, ""));
        all.put(KEY_DATE_1, prefs.getString(KEY_DATE_1, ""));
        all.put(KEY_LMD_HUB, prefs.getString(KEY_LMD_HUB, ""));
        all.put(KEY_LMD_NAME, prefs.getString(KEY_LMD_NAME, ""));
        all.put(KEY_LMD_ID, prefs.getString(KEY_LMD_ID, ""));
        all.put(KEY_PRODUCT_NAME, prefs.getString(KEY_PRODUCT_NAME, ""));
        all.put(KEY_PRODUCT_ID, prefs.getString(KEY_PRODUCT_ID, ""));
        all.put(KEY_TEAM_MEMBER_ID, prefs.getString(KEY_TEAM_MEMBER_ID, ""));
        all.put(KEY_TEAM_MEMBER_NAME, prefs.getString(KEY_TEAM_MEMBER_NAME, ""));
        all.put(KEY_TELLER_ID, prefs.getString(KEY_TELLER_ID, ""));
        all.put(KEY_TELLER_BANK, prefs.getString(KEY_TELLER_BANK, ""));
        all.put(KEY_TELLER_AMOUNT, prefs.getString(KEY_TELLER_AMOUNT, ""));
        all.put(KEY_TELLER_DATE, prefs.getString(KEY_TELLER_DATE, ""));

        return all;
    }

    public void CLEAR_RECEIPT_DETAILS(){
        editor.remove(KEY_TEAM_MEMBER_NAME);
        editor.remove(KEY_TEAM_MEMBER_ID);
        editor.remove(KEY_DATE_1);
        editor.remove(KEY_RECEIPT_ID);
        editor.commit();
    }

    public void CLEAR_TELLER_RECEIPT_DETAILS(){
        editor.remove(KEY_RECEIPT_ID);
        editor.commit();
    }

    public void CLEAR_LMD_DETAILS(){
        editor.remove(KEY_LMD_NAME);
        editor.remove(KEY_LMD_ID);
        editor.remove(KEY_LMD_HUB);
        editor.commit();
    }

    public void CLEAR_COUNT_DETAILS(){
        editor.remove(KEY_PRODUCT_NAME);
        editor.remove(KEY_PRODUCT_ID);
        editor.commit();
    }

    public void CLEAR_TELLER_DETAILS(){
        editor.remove(KEY_TELLER_ID);
        editor.remove(KEY_TELLER_BANK);
        editor.remove(KEY_TELLER_AMOUNT);
        editor.remove(KEY_TELLER_DATE);
        editor.commit();
    }

    public void CLEAR_ALL_DETAILS(){
        editor.clear();
        editor.commit();
    }



}

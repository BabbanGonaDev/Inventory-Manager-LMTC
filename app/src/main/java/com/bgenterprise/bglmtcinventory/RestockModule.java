package com.bgenterprise.bglmtcinventory;

import android.content.Context;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * This is the module class that handle every calculation pertaining to restocking.
 */

public class RestockModule {

    public static class RestockBlock{

        Context context;
        SessionManager session;

        public RestockBlock(Context context){
            this.context = context;
        }

        public Long GetLastInvDateDifference(){
            //This function gets and returns the last invoice date and the difference between the days for the LMD and specific Item ID before today.

            HashMap<String, String> allDetails;
            session = new SessionManager(context);
            allDetails = session.getAllDetails();
            long diffInDays = 0;

            InvoiceDBHandler invoiceHandler = new InvoiceDBHandler(context);

            String lastDate = invoiceHandler.getFormerInvDate(allDetails.get(SessionManager.KEY_LMD_ID), allDetails.get(SessionManager.KEY_PRODUCT_ID));

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = new Date();
            String presentDate = dateFormat.format(date);

            Date date1, date2 = null;
            try {
                date1 = dateFormat.parse(presentDate);
                date2 = dateFormat.parse(lastDate);

                long diff = date1.getTime() - date2.getTime();
                Log.d("CHECK", "Time difference: " + String.valueOf(diff));

                diffInDays = diff / (24 * 60 * 60 * 1000);

                Log.d("CHECK", "Difference in days: " + String.valueOf(diffInDays));

            }catch(Exception e){
                e.printStackTrace();
            }

            return diffInDays;

        }

        public Double CalcSalesRate(Double invoiceValue){
            //This function calculates the sales rate by dividing the invoice value by the number of days.

            long dateDiff = GetLastInvDateDifference();
            if (dateDiff == 0) {
                dateDiff = 1;
            }
            double salesRate;
            salesRate = invoiceValue / dateDiff;
            Log.d("CHECK SalesRate", "" + salesRate);

            return salesRate;
        }

        public Double CalcRestockValue(Double invoiceQty, String itemID, String lmdID) {
            //This function calculates and returns the final Restock value.
            InvoiceDBHandler invoiceHandler = new InvoiceDBHandler(context);
            int leadTime = invoiceHandler.getLeadTime(itemID, lmdID);
            double SalesRate = CalcSalesRate(invoiceQty);

            double restockValue = SalesRate * leadTime;

            return restockValue;
        }
    }
}

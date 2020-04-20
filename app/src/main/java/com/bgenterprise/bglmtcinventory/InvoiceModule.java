package com.bgenterprise.bglmtcinventory;

/**
 * Module class that handles all calculations pertaining to generating the invoice.
 */

import android.content.Context;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.HashMap;

public class InvoiceModule {

    public static class PriceBlock{
        // The price block handles getting the Invoice price for the Item.
        Context context;
        SessionManager session;

        public PriceBlock(Context context){
            this.context = context;
        }

        public Double GetThePrice(){
            //Return price as 1;
            //This function gets the required price for the LMD to be used to calculate the Invoice.

            return 1.0;

        }
    }


    public static class InvoiceBlock{
        //The Invoice block handles the final calculation of the invoice.
        Context context;
        SessionManager session;

        public InvoiceBlock(Context context){
            this.context = context;
        }

        public Integer getTotalLMLDelivery(String LMD_ID, String ItemID){

            //Returns the total LML Deliveries of an item for a specific LMD.
            InventoryDBHandler db = new InventoryDBHandler(context);
            int totalDeliveries = db.getLMLTotalDeliveries(LMD_ID, ItemID);

            Log.d("CHECK", "Total LML Delivery: " + String.valueOf(totalDeliveries));
            return totalDeliveries;
        }

        public Integer getTotalInvoices(String LMD_ID, String Item_ID){

            //Gets the total amount of invoices generated for an Item for a specific LMD.
            InvoiceDBHandler db = new InvoiceDBHandler(context);
            int totalInvoices = db.getTotalInvoices(LMD_ID, Item_ID);

            Log.d("CHECK", "Total Invoices: " + totalInvoices);

            return totalInvoices;
        }


        public Double calculateInvoiceQuantity(Integer prod_count, String lmdid, String itemid){
            InvoiceDBHandler db = new InvoiceDBHandler(context);

            //Calculate and returns the final Invoice Quantity.
            DecimalFormat myFormat = new DecimalFormat("#########.###");

            Double delivery = Double.valueOf(getTotalLMLDelivery(lmdid, itemid));
            Log.d("CHECK", "Double value of delivery: " + myFormat.format(delivery));

            Double invoice = Double.valueOf(getTotalInvoices(lmdid, itemid));
            Log.d("CHECK", "Double value of invoice: " + myFormat.format(invoice));

            Double count = Double.valueOf(prod_count);
            Log.d("CHECK", "Double value of count: " + myFormat.format(count));

            InventoryDBHandler inventoryDBHandler = new InventoryDBHandler(context);
            Double idCount = (double) inventoryDBHandler.getTotalIdQty(lmdid, itemid);
            Log.d("CHECK", "Double value of IDcount: " + myFormat.format(idCount));

            //Hence InvQty = All deliveries - All invoice amounts - All ID out - current count = Amount that was sold.
            Double invoiceQty = delivery - invoice - idCount - count;

            Log.d("CHECK", "Final InvoiceQty: " + myFormat.format(invoiceQty));

            return invoiceQty;
        }

        /*public Double calcInvQtyForRestock(Integer prod_count, String lmdid, String itemid) {
            InvoiceDBHandler db = new InvoiceDBHandler(context);

            DecimalFormat myFormat = new DecimalFormat("#########.###");

            Double delivery = Double.valueOf(getTotalLMLDelivery(lmdid, itemid));

            Double invoice = Double.valueOf(getTotalInvoices(lmdid, itemid));

            Double count = Double.valueOf(prod_count);

            InventoryDBHandler inventoryDBHandler = new InventoryDBHandler(context);
            Double idCount = (double) inventoryDBHandler.getTotalIdQtyForRestock(lmdid, itemid);
            Log.d("CHECK", "Double value of IDcountForRestock: " + myFormat.format(idCount));

            Double invoiceQty = delivery - invoice - idCount - count;

            Log.d("CHECK", "Final InvoiceQty: " + myFormat.format(invoiceQty));

            return invoiceQty;
        }*/


    }
}

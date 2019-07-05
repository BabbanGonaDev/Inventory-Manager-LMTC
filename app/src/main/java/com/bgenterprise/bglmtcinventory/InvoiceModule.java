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

        public String GetLMDPriceGroup(String LMDID){
            //Get the Price Group for the specific LMD.

            InvoiceDBHandler db = new InvoiceDBHandler(context);
            String pgName = db.getPriceGroup(LMDID);
            Log.d("CHECK", "PG Group Name: " + pgName);
            return pgName;
        }

        public Integer GetPriceChangeCount(){
            //This function gets the number of times there has been a price change for a specific price group.
            //Pull LMD ID and Item ID from the Session Manager.

            HashMap<String, String> allDetails;
            session = new SessionManager(context);
            allDetails = session.getAllDetails();

            InvoiceDBHandler db = new InvoiceDBHandler(context);

            String pgName = db.getPriceGroup(allDetails.get(SessionManager.KEY_LMD_ID));
            Log.d("CHECK", "PG Name: " + pgName);

            Log.d("CHECK", "PriceChangeCount: " + db.getPriceChangeCount(pgName, allDetails.get(SessionManager.KEY_PRODUCT_ID)).toString());

            return db.getPriceChangeCount(pgName, allDetails.get(SessionManager.KEY_PRODUCT_ID));
        }

        public Double GetThePrice(){
            //This function gets the required price for the LMD to be used to calculate the Invoice.

            HashMap<String, String> allDetails;
            session = new SessionManager(context);
            allDetails = session.getAllDetails();

            InvoiceDBHandler dbHandler = new InvoiceDBHandler(context);

            InventoryDBHandler db = new InventoryDBHandler(context);

            int priceEntryCount = GetPriceChangeCount();
            double ItemPrice = 0.0;

            if(priceEntryCount == 1){
                Log.d("CHECK", "Price Entry: 1");
                //Get the single price and use it
                ItemPrice = dbHandler.getSinglePrice(GetLMDPriceGroup(allDetails.get(SessionManager.KEY_LMD_ID)), allDetails.get(SessionManager.KEY_PRODUCT_ID));
                Log.d("CHECK", "Price Entry is 1, so selecting the only price is: " + dbHandler.getSinglePrice(GetLMDPriceGroup(allDetails.get(SessionManager.KEY_LMD_ID)), allDetails.get(SessionManager.KEY_PRODUCT_ID)).toString());
            }else if(priceEntryCount > 1){
                //Now time to move forward to compare dates.

                Log.d("CHECK", "Price entry is > 1");

                //1. First get the latest date.
                String latestDate = dbHandler.getLatestDate(GetLMDPriceGroup(allDetails.get(SessionManager.KEY_LMD_ID)), allDetails.get(SessionManager.KEY_PRODUCT_ID));
                Log.d("CHECK", "Latest Date for >1: " + latestDate);

                //2. Second get the total number of Type: "FOD" entries in the Inventory03T table between this latest price entry date and today.
                int No_Of_FODEntries = db.getNumberOf_FODEntries(allDetails.get(SessionManager.KEY_LMD_ID), allDetails.get(SessionManager.KEY_PRODUCT_ID), latestDate);
                Log.d("CHECK", "No of entries b/w dates in >1: " + db.getNumberOf_FODEntries(allDetails.get(SessionManager.KEY_LMD_ID), allDetails.get(SessionManager.KEY_PRODUCT_ID), latestDate).toString());

                //3. Execute the getting of the prices using the number of entries and if else statement.
                if (No_Of_FODEntries == 1){

                    ItemPrice = dbHandler.getFormerPrice(GetLMDPriceGroup(allDetails.get(SessionManager.KEY_LMD_ID)), allDetails.get(SessionManager.KEY_PRODUCT_ID));
                    Log.d("CHECK", "No of entries is == 1 Hence price is: " + dbHandler.getFormerPrice(GetLMDPriceGroup(allDetails.get(SessionManager.KEY_LMD_ID)), allDetails.get(SessionManager.KEY_PRODUCT_ID)).toString());

                }else if (No_Of_FODEntries >= 2){

                    ItemPrice = dbHandler.getSinglePrice(GetLMDPriceGroup(allDetails.get(SessionManager.KEY_LMD_ID)), allDetails.get(SessionManager.KEY_PRODUCT_ID));
                    Log.d("CHECK", "No of entries is >= 2 Hence price is: " + dbHandler.getFormerPrice(GetLMDPriceGroup(allDetails.get(SessionManager.KEY_LMD_ID)), allDetails.get(SessionManager.KEY_PRODUCT_ID)).toString());
                }


            }

            Log.d("CHECK", "Final Item Price: " + String.valueOf(ItemPrice));
            //4. return the gotten price. One variable used by everyone.
            return ItemPrice;
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

            Log.d("CHECK", "Total Invoices: " + String.valueOf(totalInvoices));

            return totalInvoices;
        }

//        public Integer getTotalInvoicesCount(String LMD_ID, String Item_ID) {
//
//            //Gets the total amount of invoices generated for an Item for a specific LMD.
//            InvoiceDBHandler db = new InvoiceDBHandler(context);
//            int totalInvoices = db.getTotalInvoicesCount(LMD_ID, Item_ID);
//
//            Log.d("CHECK", "Total Invoices: " + String.valueOf(totalInvoices));
//
//            return totalInvoices;
//        }

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

        public Double calcInvQtyForRestock(Integer prod_count, String lmdid, String itemid) {
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
        }


    }
}

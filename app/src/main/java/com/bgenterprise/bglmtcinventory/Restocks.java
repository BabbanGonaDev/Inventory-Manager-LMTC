package com.bgenterprise.bglmtcinventory;

public class Restocks {

    /**
     * Restock class that contains all constructors pertaining to the restocks database.
     */

    private String LMDID;
    private String ItemID;
    private String RestockValue;
    private String LMDKey;
    private String Count;
    private String RequestDate;


    public Restocks(String LMDID, String itemID) {
        this.LMDID = LMDID;
        this.ItemID = itemID;
    }

    public Restocks(String itemID, String restockValue, String count, String requestDate) {
        this.ItemID = itemID;
        this.RestockValue = restockValue;
        this.Count = count;
        this.RequestDate = requestDate;
    }

    public String getLMDID() {
        return LMDID;
    }

    public String getItemID() {
        return ItemID;
    }

    public String getRestockValue() {
        return RestockValue;
    }

    public String getCount() {
        return Count;
    }

    public String getRequestDate() {
        return RequestDate;
    }
}

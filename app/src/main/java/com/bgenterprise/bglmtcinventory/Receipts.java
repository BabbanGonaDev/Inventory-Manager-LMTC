package com.bgenterprise.bglmtcinventory;

/**
 * Receipts class that contains all the constructors required for the Receipts table.
 */

public class Receipts {

    private String lmdName;
    private String lmdID;
    private String receiptCount;

    private String receiptID;
    private String receiptAmount;
    private String moneycollectedby;
    private String receiptDate;
    private String receiptSyncStatus;

    //For the First Recycler that shows all the receipts.
    public Receipts(String lmdName, String lmdID, String receiptCount) {
        this.lmdName = lmdName;
        this.lmdID = lmdID;
        this.receiptCount = receiptCount;
    }

    //For the second recycler that shows based on the LMD
    public Receipts(String receiptID, String receiptAmount, String moneycollectedby, String receiptDate, String receiptSyncStatus) {
        this.receiptID = receiptID;
        this.receiptAmount = receiptAmount;
        this.moneycollectedby = moneycollectedby;
        this.receiptDate = receiptDate;
        this.receiptSyncStatus = receiptSyncStatus;
    }

    public String getLmdName() {
        return lmdName;
    }

    public String getLmdID() {
        return lmdID;
    }

    public String getReceiptCount() {
        return receiptCount;
    }

    public String getReceiptID() {
        return receiptID;
    }

    public String getReceiptAmount() {
        return receiptAmount;
    }

    public String getMoneycollectedby() {
        return moneycollectedby;
    }

    public String getReceiptDate() {
        return receiptDate;
    }

    public String getReceiptSyncStatus() {
        return receiptSyncStatus;
    }
}

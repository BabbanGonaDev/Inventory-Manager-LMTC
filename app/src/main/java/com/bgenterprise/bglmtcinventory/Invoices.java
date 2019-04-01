package com.bgenterprise.bglmtcinventory;

public class Invoices {
    /**
     * Invoice class that contains all constructors pertaining to the invoice database.
     */

    private String LMDID;
    private String ItemID;
    private String FODPhysicalCount;
    private String TxnDate;
    private String Type;
    private String UnitPrice;
    private String InvoiceQty;
    private String InvoiceValue;
    private String invoiceCount;
    private String invoiceAmount;


    //First Recycler that shows all Invoices.
    public Invoices(String LMDID, String txnDate, String invoiceCount, String invoiceAmount) {
        this.LMDID = LMDID;
        this.TxnDate = txnDate;
        this.invoiceCount = invoiceCount;
        this.invoiceAmount = invoiceAmount;
    }

    //Second Recycler that shows from onclick of first one.
    public Invoices(String itemID, String FODPhysicalCount, String unitPrice, String invoiceQty, String invoiceValue) {
        this.ItemID = itemID;
        this.FODPhysicalCount = FODPhysicalCount;
        this.UnitPrice = unitPrice;
        this.InvoiceQty = invoiceQty;
        this.InvoiceValue = invoiceValue;
    }

    public String getLMDID() {
        return LMDID;
    }

    public String getItemID() {
        return ItemID;
    }

    public String getFODPhysicalCount() {
        return FODPhysicalCount;
    }

    public String getTxnDate() {
        return TxnDate;
    }

    public String getType() {
        return Type;
    }

    public String getUnitPrice() {
        return UnitPrice;
    }

    public String getInvoiceQty() {
        return InvoiceQty;
    }

    public String getInvoiceValue() {
        return InvoiceValue;
    }

    public String getInvoiceCount() {
        return invoiceCount;
    }

    public String getInvoiceAmount(){ return invoiceAmount; }
}

package com.bgenterprise.bglmtcinventory;

/**
 *  A teller class that handles all constructors to be used with the Teller table.
 */

public class Tellers {

    private String teller_id;
    private String teller_amount;
    private String teller_bank;
    private String receipt_id;
    private String receipt_amount;
    private String receipt_date;
    private String receipt_count;
    private String teller_date;

    //For the Tellers.
    public Tellers(String teller_id, String teller_amount, String teller_bank, String receipt_count, String teller_date) {
        this.teller_id = teller_id;
        this.teller_amount = teller_amount;
        this.teller_bank = teller_bank;
        this.receipt_count = receipt_count;
        this.teller_date = teller_date;
    }


    //For details of the tellers.
    public Tellers(String receipt_id, String receipt_amount) {
        this.receipt_id = receipt_id;
        this.receipt_amount = receipt_amount;
    }

    public String getTeller_id() {
        return teller_id;
    }

    public String getTeller_amount() {
        return teller_amount;
    }

    public String getTeller_bank() {
        return teller_bank;
    }

    public String getReceipt_id() {
        return receipt_id;
    }

    public String getReceipt_amount() {
        return receipt_amount;
    }

    public String getReceipt_date() {
        return receipt_date;
    }

    public String getReceipt_count() {
        return receipt_count;
    }

    public String getTeller_date() {
        return teller_date;
    }
}

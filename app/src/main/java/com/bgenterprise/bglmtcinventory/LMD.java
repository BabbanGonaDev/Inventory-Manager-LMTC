package com.bgenterprise.bglmtcinventory;

public class LMD {

    private String LMDID;
    private String LMDName;
    private String LMTC_ID;

    public LMD(String LMDID, String LMDName, String LMTC_ID) {
        this.LMDID = LMDID;
        this.LMDName = LMDName;
        this.LMTC_ID = LMTC_ID;
    }

    public String getLMDID() {
        return LMDID;
    }

    public String getLMDName() {
        return LMDName;
    }

    public String getLMTC_ID() {
        return LMTC_ID;
    }
}

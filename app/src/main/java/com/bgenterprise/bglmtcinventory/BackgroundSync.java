package com.bgenterprise.bglmtcinventory;

import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.widget.Toast;

public class BackgroundSync extends JobService {
    SyncModule.SyncUpInventory03T syncUpInventory03T;
    SyncModule.SyncUpLMDInvValueT syncUpLMDInvValueT;
    SyncModule.SyncUpReceiptTable syncUpReceiptTable;
    SyncModule.SyncUpRestockT syncUpRestockT;
    SyncModule.SyncUpTellerT syncUpTellerT;
    SyncModule.updateLMDT updateLMDT;
    SyncModule.SyncDownPriceT syncDownPriceT;
    SyncModule.SyncDownPriceGroupT syncDownPriceGroupT;


    @SuppressLint("StaticFieldLeak")

    @Override
    public boolean onStartJob(final JobParameters params) {
        Toast.makeText(getApplicationContext(), "LMTC Auto Sync Engaged...", Toast.LENGTH_LONG).show();

        syncUpInventory03T = new SyncModule.SyncUpInventory03T(getApplicationContext()){
            @Override
            protected void onPostExecute(String s) {
                jobFinished(params, true);
            }
        };
        syncUpInventory03T.execute();

        syncUpLMDInvValueT = new SyncModule.SyncUpLMDInvValueT(getApplicationContext()){
            @Override
            protected void onPostExecute(String s) {
                jobFinished(params, true);
            }
        };
        syncUpLMDInvValueT.execute();

        syncUpReceiptTable = new SyncModule.SyncUpReceiptTable(getApplicationContext()){
            @Override
            protected void onPostExecute(String s) {
                jobFinished(params, true);
            }
        };
        syncUpReceiptTable.execute();

        syncUpRestockT = new SyncModule.SyncUpRestockT(getApplicationContext()){
            @Override
            protected void onPostExecute(String s) {
                jobFinished(params, true);
            }
        };
        syncUpRestockT.execute();

        syncUpTellerT = new SyncModule.SyncUpTellerT(getApplicationContext()){
            @Override
            protected void onPostExecute(String s) {
                jobFinished(params, true);
            }
        };

        updateLMDT = new SyncModule.updateLMDT(getApplicationContext()){
            @Override
            protected void onPostExecute(String s) {
                jobFinished(params, true);
            }
        };
        updateLMDT.execute();

        syncDownPriceGroupT = new SyncModule.SyncDownPriceGroupT(getApplicationContext()){
            @Override
            protected void onPostExecute(String s) {
                jobFinished(params, true);
            }
        };
        syncDownPriceGroupT.execute();

        syncDownPriceT = new SyncModule.SyncDownPriceT(getApplicationContext()){
            @Override
            protected void onPostExecute(String s) {
                jobFinished(params, true);
            }
        };
        syncDownPriceT.execute();



        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}

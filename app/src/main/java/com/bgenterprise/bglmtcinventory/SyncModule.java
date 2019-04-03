package com.bgenterprise.bglmtcinventory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SyncModule {

    static String InternetLink = "http://f5e8e9e5.ngrok.io/qrcode";

        public static class SyncDownInventory03T extends AsyncTask<String, String, String>{
            //This function syncs down the Inventory03T for the respective LMD.

            @SuppressLint("StaticFieldLeak")
            private Context mCtx;

            public SyncDownInventory03T(Context context){
                this.mCtx = context;
            }

            public String GetLastDateOfSync(){
                InventoryDBHandler db = new InventoryDBHandler(mCtx);
                return db.GetLastLMLSyncDate();
            }

            @Override
            protected String doInBackground(String... strings) {
                //This is the staffID of the FOD that we will use to link the LMDs and the FODs.
                String staffID = strings[0];
                String lastLMLSyncDate = GetLastDateOfSync();

                if(!staffID.isEmpty()) {
                    //Create a ArrayList
                    ArrayList<HashMap<String, String>> wordList;
                    wordList = new ArrayList<HashMap<String, String>>();

                    //Create a HashMap of the needed variables.
                    HashMap<String, String> map = new HashMap<>();
                    map.put("staff_id", staffID);
                    map.put("last_date", lastLMLSyncDate);
                    wordList.add(map);

                    //Convert the wordList to JSON.
                    Gson gson = new GsonBuilder().create();
                    String MyJSONParams = gson.toJson(wordList);


                    //Create AsyncHttpClient Object.
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams params = new RequestParams();

                    params.put("SyncDown03TJSON", MyJSONParams);
                    Log.d("CHECK", "Parameters posted online: " + MyJSONParams);
                    client.post(InternetLink + "/lmtc_fetch_inventory03t.php", params, new AsyncHttpResponseHandler(){
                        @Override
                        public void onSuccess(String response) {
                            try{
                                JSONArray arr = new JSONArray(response);
                                for(int i = 0; i < arr.length(); i++){
                                    JSONObject obj = (JSONObject)arr.get(i);
                                    InventoryDBHandler db = new InventoryDBHandler(mCtx);
                                    Log.d("CHECK", obj.getString("LMDID") + " " + obj.getString("ItemName") + " " + obj.getString("Unit"));
                                    db.onAdd_Inventory03T(obj.getString("UniqueID"), obj.getString("TxnDate"), obj.getString("LMDID"), obj.getString("ItemID"),
                                            obj.getString("ItemName"), obj.getString("Unit"), obj.getString("Type"), obj.getString("UnitPrice"),
                                            obj.getString("Notes"), obj.getString("SyncDate"), "no");
                                }
                                //TODO ---> Toast Success Message.
                            }catch(JSONException e){
                                //TODO---> Toast JSON error message.
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Throwable error, String content) {
                            if(statusCode == 404){
                                Log.d("CHECK", "Error Code: 404");
                            }else if(statusCode == 500){
                                Log.d("CHECK", "Error Code: 500");
                            }else{
                                Log.d("CHECK", "Not Sure, Are you connected to Internet ?");
                            }
                        }
                    });

                }else{
                   //TODO ---> Toast staff ID empty.
                    Log.d("CHECK", "Staff ID is empty.");
                }
                return null;
            }
        }

        public static class SyncUpInventory03T extends AsyncTask<String, String, String>{

            @SuppressLint("StaticFieldLeak")
            private Context context;
            SessionManager session;

            public SyncUpInventory03T(Context context) {
                this.context = context;
            }

            @Override
            protected String doInBackground(String... strings) {
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                Log.d("CHECK", "Entered the doInBackground");

                params.put("SyncUp03TJSON", getInventory03TJSONSqlite());
                Log.d("CHECK","Params has gotten here");

                client.post(InternetLink + "/lmtc_insert_inventory03t.php",params, new AsyncHttpResponseHandler(){
                    @Override
                    public void onSuccess(String response) {
                        try{
                            JSONArray arr = new JSONArray(response);
                            for(int i = 0; i < arr.length(); i++) {
                                JSONObject obj = (JSONObject) arr.get(i);
                                Log.d("CHECK", "Reply: " + obj.getString("UniqueID") + " " + obj.getString("SyncStatus"));
                                //Update sync status.
                                InventoryDBHandler db = new InventoryDBHandler(context);
                                db.updateSyncStatus(obj.getString("UniqueID"), obj.getString("SyncStatus"));
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Throwable error, String content) {
                        if(statusCode == 404){

                        }else if(statusCode == 500){

                        }else{

                        }
                    }
                });
                return null;
            }

            public String getInventory03TJSONSqlite(){
                InventoryDBHandler db = new InventoryDBHandler(context);
                return db.getInventory03TRecords();
            }
        }

        public static class RefreshInventory03T extends AsyncTask<String, String, String>{
            @SuppressLint("StaticFieldLeak")
            private Context context;
            InventoryDBHandler db;

            public RefreshInventory03T(Context context){
                this.context = context;
            }

            @Override
            protected String doInBackground(String... strings) {
                 db = new InventoryDBHandler(context);
                 if(db.emptyInventory03T()){
                     //Now download everything.

                     //Create AsyncHttpClient Object.
                     AsyncHttpClient client = new AsyncHttpClient();
                     RequestParams params = new RequestParams();

                     params.put("Refresh03TJSON", "");
                     //Log.d("CHECK", "Parameters posted online: " + MyJSONParams);
                     client.post(InternetLink + "/lmtc_refresh_inventory03t.php", params, new AsyncHttpResponseHandler(){
                         @Override
                         public void onSuccess(String response) {
                             try{
                                 JSONArray arr = new JSONArray(response);
                                 for(int i = 0; i < arr.length(); i++){
                                     JSONObject obj = (JSONObject)arr.get(i);

                                     Log.d("CHECK", obj.getString("LMDID") + " " + obj.getString("ItemName") + " " + obj.getString("Unit"));
                                     db.onAdd_Inventory03T(obj.getString("UniqueID"), obj.getString("TxnDate"), obj.getString("LMDID"), obj.getString("ItemID"),
                                             obj.getString("ItemName"), obj.getString("Unit"), obj.getString("Type"), obj.getString("UnitPrice"),
                                             obj.getString("Notes"), obj.getString("SyncDate"), "no");
                                 }
                                 //TODO ---> Toast Success Message.
                             }catch(JSONException e){
                                 //TODO---> Toast JSON error message.
                                 e.printStackTrace();
                             }
                         }

                         @Override
                         public void onFailure(int statusCode, Throwable error, String content) {
                             if(statusCode == 404){
                                 Log.d("CHECK", "Error Code: 404");
                             }else if(statusCode == 500){
                                 Log.d("CHECK", "Error Code: 500");
                             }else{
                                 Log.d("CHECK", "Not Sure, Are you connected to Internet ?");
                             }
                         }
                     });

                 }else{
                     //Unable to empty the old database.
                 }
                return null;
            }
        }

    }
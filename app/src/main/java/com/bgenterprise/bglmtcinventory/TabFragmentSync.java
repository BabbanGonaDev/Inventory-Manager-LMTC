package com.bgenterprise.bglmtcinventory;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

import static com.bgenterprise.bglmtcinventory.SyncModule.SyncDownPriceGroupT;

//import static com.bgenterprise.bglmtcinventory.SyncModule.RefreshInventory03T;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabFragmentSync extends Fragment {
    View view;
    SessionManager session;
    //    Button btnSyncDownInventory03T, btnSyncUpInventory03T, btnRefreshInventory03T, btnSyncDownPgroupT, btnSyncDownPriceT,
//            btnRefreshLMDInvValueT, btnUpdateLMDT, btnUploadLmdInvValT, btnUploadReceiptT, btnUploadRestockT, btnUploadTellerT,
//            btnUpdateHoldCostT;
    Button btnUploadData, btnDownloadData, btnRefresh, btnRefresh03T;

    public TabFragmentSync() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_tab_fragment_sync, container, false);
        session = new SessionManager(getActivity());

        btnUploadData = view.findViewById(R.id.btn_upload_data);
        btnDownloadData = view.findViewById(R.id.btn_download_data);
        btnRefresh = view.findViewById(R.id.btn_refresh_invoiceT);
        btnRefresh03T = view.findViewById(R.id.btn_refresh_inventory03T);


        btnDownloadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] countDown = {0};
                HashMap<String, String> val = session.getAllDetails();
                String staff_id = val.get(SessionManager.KEY_STAFF_ID);
                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Syncing down records, please wait");
                progressDialog.show();
                progressDialog.setCancelable(false);


                @SuppressLint("StaticFieldLeak") SyncModule.SyncDownPriceGroupT syncDownPriceGroupT = new SyncDownPriceGroupT(getActivity().getApplicationContext()) {
                    @Override
                    protected void onPostExecute(@NonNull String s) {
                        Log.d("s", "" + s);
                        Toast.makeText(getActivity(), "PriceGroupT Sync Down: " +s, Toast.LENGTH_LONG).show();
                        if (s.trim().equalsIgnoreCase("done")) {
                            countDown[0]++;
                            Log.d("countdownPGT", String.valueOf(countDown[0]));
                        }
                    }
                };
                syncDownPriceGroupT.execute(staff_id);

                @SuppressLint("StaticFieldLeak") SyncModule.SyncDownInventory03T syncDownInventory03T = new SyncModule.SyncDownInventory03T(getActivity().getApplicationContext()) {
                    @Override
                    protected void onPostExecute(String s) {
                        Log.d("s", "" + s);
                        Toast.makeText(getActivity(), "Inventory03T Sync Down: " + s, Toast.LENGTH_LONG).show();
                        if (s.trim().equalsIgnoreCase("done")) {
                            countDown[0]++;
                            Log.d("countdownInventory03T", String.valueOf(countDown[0]));
                        }
                    }
                };
                syncDownInventory03T.execute(staff_id);

                @SuppressLint("StaticFieldLeak") SyncModule.SyncDownPriceT syncDownPriceT = new SyncModule.SyncDownPriceT(getActivity().getApplicationContext()) {
                    @Override
                    protected void onPostExecute(String s) {
                        Log.d("s", "" + s);
                        Toast.makeText(getActivity(), "SyncDown PriceT: " + s, Toast.LENGTH_LONG).show();
                        if (s.trim().equalsIgnoreCase("done")) {
                            countDown[0]++;
                            Log.d("countdownPriceT", String.valueOf(countDown[0]));
                        }
                    }
                };
                syncDownPriceT.execute(staff_id);

                @SuppressLint("StaticFieldLeak") SyncModule.SyncDownHoldingCostT syncDownHoldingCostT = new SyncModule.SyncDownHoldingCostT(getActivity().getApplicationContext()) {
                    @Override
                    protected void onPostExecute(String s) {
                        Log.d("s", "" + s);
                        Toast.makeText(getActivity(), "Sync Down Holding CostT: " + s, Toast.LENGTH_LONG).show();
                        if (s.trim().equalsIgnoreCase("done")) {
                            countDown[0]++;
                            Log.d("countdownHcT", String.valueOf(countDown[0]));
                        }
                    }
                };
                syncDownHoldingCostT.execute();

                @SuppressLint("StaticFieldLeak") SyncModule.SyncDownLeadTimeT syncDownLeadTimeT = new SyncModule.SyncDownLeadTimeT((getActivity()).getApplicationContext()) {
                    @Override
                    protected void onPostExecute(String s) {
                        Log.d("s", "" + s);
                        Toast.makeText(getActivity(), "LeadTimeT Sync Down: " + s, Toast.LENGTH_LONG).show();
                        if (s.trim().equalsIgnoreCase("done")) {
                            countDown[0]++;
                            Log.d("countdownLeadT", String.valueOf(countDown[0]));
                        }
                    }
                };
                syncDownLeadTimeT.execute(staff_id);

                @SuppressLint("StaticFieldLeak") SyncModule.updateLMDT sync5 = new SyncModule.updateLMDT((getActivity()).getApplicationContext()) {
                    @Override
                    protected void onPostExecute(String s) {
                        Log.d("s", "" + s);
                        Toast.makeText(getActivity(), "Update LMDT Response: " + s, Toast.LENGTH_LONG).show();
                        if (s.trim().equalsIgnoreCase("done")) {
                            countDown[0]++;
                            Log.d("countdownLMDT", String.valueOf(countDown[0]));
                        }
                        Log.d("Countdown", "" + countDown[0]);
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }

                        /*if (countDown[0] == 6) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Sync down functions successful", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Sync down function failed, " +
                                    "please check your internet connection", Toast.LENGTH_LONG).show();
                        }*/
                    }
                };
                sync5.execute(staff_id);

                updateLMDMap();

            }
        });



        btnUploadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Syncing up records, please wait");
                progressDialog.show();
                progressDialog.setCancelable(false);
                final int[] countUp = {0};
                CheckInternetPermission();
                @SuppressLint("StaticFieldLeak") SyncModule.SyncUpInventory03T syncUpInventory03T = new SyncModule.SyncUpInventory03T(Objects.requireNonNull(getActivity()).getApplicationContext()) {
                    @Override
                    protected void onPostExecute(String s) {
                        Log.d("s", "" + s);
                        Toast.makeText(getActivity(), "Sync Up Inventory03T: " + s, Toast.LENGTH_LONG).show();
                        if (s.trim().equalsIgnoreCase("done")) {
                            countUp[0]++;
                            Log.d("countUp", String.valueOf(countUp[0]));
                        }
                    }
                };
                syncUpInventory03T.execute();

                @SuppressLint("StaticFieldLeak") SyncModule.SyncUpLMDInvValueT syncUpLMDInvValueT = new SyncModule.SyncUpLMDInvValueT(Objects.requireNonNull(getActivity()).getApplicationContext()) {
                    @Override
                    protected void onPostExecute(String s) {
                        Log.d("s", "" + s);
                        Toast.makeText(getActivity(), "Sync Up LMDInvoiceValueT: " + s, Toast.LENGTH_LONG).show();
                        if (s.trim().equalsIgnoreCase("done")) {
                            countUp[0]++;
                            Log.d("countUp", String.valueOf(countUp[0]));
                        }
                    }
                };
                syncUpLMDInvValueT.execute();

                @SuppressLint("StaticFieldLeak") SyncModule.SyncUpReceiptTable syncUpReceiptTable = new SyncModule.SyncUpReceiptTable(Objects.requireNonNull(getActivity()).getApplicationContext()) {
                    @Override
                    protected void onPostExecute(String s) {
                        Log.d("s", "" + s);
                        Toast.makeText(getActivity(), "Sync up Receipt Table: " + s, Toast.LENGTH_LONG).show();
                        if (s.trim().equalsIgnoreCase("done")) {
                            countUp[0]++;
                            Log.d("countUp", String.valueOf(countUp[0]));
                        }
                    }
                };
                syncUpReceiptTable.execute();

                @SuppressLint("StaticFieldLeak") SyncModule.SyncUpRestockT syncUpRestockT = new SyncModule.SyncUpRestockT(Objects.requireNonNull(getActivity()).getApplicationContext()) {
                    @Override
                    protected void onPostExecute(String s) {
                        Log.d("s", "" + s);
                        Toast.makeText(getActivity(), "Sync Up Restock T: " + s, Toast.LENGTH_LONG).show();
                        if (s.trim().equalsIgnoreCase("done")) {
                            countUp[0]++;
                            Log.d("countUp", String.valueOf(countUp[0]));
                        }
                    }
                };
                syncUpRestockT.execute();

                @SuppressLint("StaticFieldLeak") SyncModule.SyncUpTellerT syncUpTellerT = new SyncModule.SyncUpTellerT(Objects.requireNonNull(getActivity()).getApplicationContext()) {
                    @Override
                    protected void onPostExecute(String s) {
                        Log.d("s", "" + s);
                        Toast.makeText(getActivity(), "Sync Up Teller T: " + s, Toast.LENGTH_LONG).show();
                        if (s.trim().equalsIgnoreCase("done")) {
                            countUp[0]++;
                            Log.d("countUp", String.valueOf(countUp[0]));
                        }

                        Log.d("CountUp", "" + countUp[0]);
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }

                        /*if (countUp[0] == 5) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Sync up functions successful", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Sync up function failed, " +
                                    "please check your internet connection", Toast.LENGTH_LONG).show();
                        }*/
                    }
                };
                syncUpTellerT.execute();


            }
        });

        btnRefresh03T.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialAlertDialogBuilder(getActivity(), R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
                        .setTitle("Confirm Action !!!")
                        .setIcon(R.drawable.warning_sign)
                        .setMessage("This action will destroy and then rebuild Inventory03T, Are you sure you want to do so ?")
                        .setPositiveButton("YES, Rebuild Inventory03T", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                                progressDialog.setMessage("Refreshing Inventory03T Table, Kindly wait....");
                                progressDialog.show();

                                @SuppressLint("StaticFieldLeak") SyncModule.RefreshInventory03T refreshInventory03T = new SyncModule.RefreshInventory03T(Objects.requireNonNull(getActivity()).getApplicationContext()){
                                    @Override
                                    protected void onPostExecute(String s){
                                        Toast.makeText(getActivity(), "Refresh Inv03T: " + s, Toast.LENGTH_LONG).show();
                                        if(progressDialog.isShowing()){
                                            progressDialog.dismiss();
                                        }
                                    }
                                };
                                refreshInventory03T.execute();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
            }
        });


        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new MaterialAlertDialogBuilder(getActivity(), R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
                        .setTitle("Confirm Action !!!")
                        .setMessage("This action will destroy and then rebuild Invoice ValueT, Are you sure you want to do so ?")
                        .setIcon(R.drawable.warning_sign)
                        .setPositiveButton("YES, Rebuild LMD InvoiceValueT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                HashMap<String, String> val = session.getAllDetails();
                                String staff_id = val.get(SessionManager.KEY_STAFF_ID);
                                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                                progressDialog.setMessage("Refreshing Table, Kindly wait");
                                progressDialog.show();

                                @SuppressLint("StaticFieldLeak") SyncModule.RefreshLMDInvoiceValueT refreshLMDInvoiceValueT = new SyncModule.RefreshLMDInvoiceValueT((getActivity()).getApplicationContext()) {
                                    @Override
                                    protected void onPostExecute(String s) {
                                        Log.d("s", "" + s);
                                        Toast.makeText(getActivity(), "Refresh LMDInvoiceValueT: " + s, Toast.LENGTH_LONG).show();
                                        if (s.trim().equalsIgnoreCase("done")) {
                                            Log.d("countdownLMDInvValueT", "done");
                                        }

                                        if (progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }
                                    }
                                };
                                refreshLMDInvoiceValueT.execute(staff_id);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
            }
        });

        return view;
    }

    public void CheckInternetPermission() {
        try {
            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.INTERNET}, 23);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateLMDMap(){
        final LMDMapDBHandler lmdMap = new LMDMapDBHandler(getActivity());

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("lastPriceUpdate", lmdMap.lastLMDMapSyncDate());
        client.post("http://apps.babbangona.com/qrcode/lmdmap_update.php", params, new AsyncHttpResponseHandler(){

            @Override
            public void onSuccess(String response) {
                try {
                    JSONArray arr = new JSONArray(response);
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = (JSONObject) arr.get(i);
                        lmdMap.LMDMapUpdate(obj.getString("lmdid"), obj.getString("lmdname"), obj.getString("login_id"), obj.getString("timestamp"));

                    }
                    Toast.makeText(getActivity(), "Update LMDMap Table: done" , Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    Log.d("CHECK", "Error caught in LMDMap update!");
                    Toast.makeText(getActivity(), "Error caught in LMDMap update!" , Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Throwable error, String content) {

                if (statusCode == 404) {
                    Log.d("CHECK", "Requested resource not found LMDMap");
                    Toast.makeText(getActivity(), "Requested resource not found LMDMap" , Toast.LENGTH_LONG).show();

                }else if (statusCode == 500) {
                    Log.d("CHECK", "Something went wrong at server side LMDMap");
                    Toast.makeText(getActivity(), "Something went wrong at server side LMDMap" , Toast.LENGTH_LONG).show();

                }else{
                    Log.d("CHECK", "Unexpected error occurred! [Most common Error: Device might not be connected LMDMap");
                    Toast.makeText(getActivity(), "Unexpected error occurred! [Most common Error: Device might not be connected LMDMap" , Toast.LENGTH_LONG).show();

                }
            }

        });
    }

}

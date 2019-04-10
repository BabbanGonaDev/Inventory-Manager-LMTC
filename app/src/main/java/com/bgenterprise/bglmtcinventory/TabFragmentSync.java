package com.bgenterprise.bglmtcinventory;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bgenterprise.bglmtcinventory.SyncModule.SyncDownPriceT;

import java.util.HashMap;
import java.util.Objects;

import static com.bgenterprise.bglmtcinventory.SyncModule.RefreshLMDInvoiceValueT;
import static com.bgenterprise.bglmtcinventory.SyncModule.SyncDownInventory03T;
import static com.bgenterprise.bglmtcinventory.SyncModule.SyncDownPriceGroupT;
import static com.bgenterprise.bglmtcinventory.SyncModule.SyncUpInventory03T;
import static com.bgenterprise.bglmtcinventory.SyncModule.updateLMDT;

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
    Button btnUploadData, btnDownloadData;

    public TabFragmentSync() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_tab_fragment_sync, container, false);
        session = new SessionManager(getActivity());
//        btnSyncDownInventory03T = view.findViewById(R.id.btnSyncDownInventory03T);
//        btnSyncUpInventory03T = view.findViewById(R.id.btnSyncUpInventory03T);
//        btnRefreshInventory03T = view.findViewById(R.id.btnRefreshInventory03T);
//        btnSyncDownPgroupT = view.findViewById(R.id.btnSyncDownPgroupT);
//        btnSyncDownPriceT = view.findViewById(R.id.btnSyncDownPriceT);
//        btnRefreshLMDInvValueT = view.findViewById(R.id.btnRefreshLMDIVT);
//        btnUpdateLMDT = view.findViewById(R.id.btnUpdateLMDT);
//        btnUploadLmdInvValT = view.findViewById(R.id.btnUploadLmdInvValueT);
//        btnUploadReceiptT = view.findViewById(R.id.btnUploadReceiptT);
//        btnUploadRestockT = view.findViewById(R.id.btnUploadRestockT);
//        btnUploadTellerT = view.findViewById(R.id.btnUploadTellerT);
//        btnUpdateHoldCostT = view.findViewById(R.id.btnUpdateHoldingCostT);
        btnUploadData = view.findViewById(R.id.btn_upload_data);
        btnDownloadData = view.findViewById(R.id.btn_download_data);


        btnDownloadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] countDown = {0};
                HashMap<String, String> val = session.getAllDetails();
                String staff_id = val.get(SessionManager.KEY_STAFF_ID);
                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Syncing down records, please wait");
                progressDialog.show();
                @SuppressLint("StaticFieldLeak") SyncDownPriceGroupT sync6 = new SyncDownPriceGroupT(getActivity().getApplicationContext()) {
                    @Override
                    protected void onPostExecute(@NonNull String s) {
                        Log.d("s", "" + s);
                        if (s.trim().equalsIgnoreCase("done")) {
                            countDown[0]++;
                        }
//                        else {
//                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), s, Toast.LENGTH_SHORT).show();
//                        }
                    }
                };
                sync6.execute(staff_id);

                @SuppressLint("StaticFieldLeak") SyncDownInventory03T syncDown = new SyncDownInventory03T(getActivity().getApplicationContext()) {
                    @Override
                    protected void onPostExecute(String s) {
                        Log.d("s", "" + s);
                        if (s.trim().equalsIgnoreCase("done")) {
                            countDown[0]++;
                        }
//                else {
//                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), s, Toast.LENGTH_SHORT).show();
//                }
                    }
                };
                syncDown.execute(staff_id);

                @SuppressLint("StaticFieldLeak") SyncDownPriceT sync2 = new SyncDownPriceT(getActivity().getApplicationContext()) {
                    @Override
                    protected void onPostExecute(String s) {
                        Log.d("s", "" + s);
                        if (s.trim().equalsIgnoreCase("done")) {
                            countDown[0]++;
                        }
//                        else {
//                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), s, Toast.LENGTH_SHORT).show();
//                        }
                    }
                };
                sync2.execute(staff_id);

                @SuppressLint("StaticFieldLeak") SyncModule.SyncDownHoldingCostT sync3 = new SyncModule.SyncDownHoldingCostT(getActivity().getApplicationContext()) {
                    @Override
                    protected void onPostExecute(String s) {
                        Log.d("s", "" + s);
                        if (s.trim().equalsIgnoreCase("done")) {
                            countDown[0]++;
                        }
//                        else {
//                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), s, Toast.LENGTH_SHORT).show();
//                        }
                    }
                };
                sync3.execute();

                @SuppressLint("StaticFieldLeak") RefreshLMDInvoiceValueT sync4 = new RefreshLMDInvoiceValueT((getActivity()).getApplicationContext()) {
                    @Override
                    protected void onPostExecute(String s) {
                        Log.d("s", "" + s);
                        if (s.trim().equalsIgnoreCase("done")) {
                            countDown[0]++;
                        }
//                        else {
//                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), s, Toast.LENGTH_SHORT).show();
//                        }
                    }
                };
                sync4.execute(staff_id);

                @SuppressLint("StaticFieldLeak") updateLMDT sync5 = new SyncModule.updateLMDT((getActivity()).getApplicationContext()) {
                    @Override
                    protected void onPostExecute(String s) {
                        Log.d("s", "" + s);
                        if (s.trim().equalsIgnoreCase("done")) {
                            countDown[0]++;
                        }
//                        else {
//                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), s, Toast.LENGTH_SHORT).show();
//                        }
                        Log.d("Countdown", "" + countDown[0]);
                        if (countDown[0] == 6) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Sync successful", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Sync failed, " +
                                    "please check your internet connection", Toast.LENGTH_LONG).show();
                        }
                    }
                };
                sync5.execute(staff_id);



            }
        });

        btnUploadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Syncing up records, please wait");
                progressDialog.show();
                final int[] countUp = {0};
                CheckInternetPermission();
                @SuppressLint("StaticFieldLeak") SyncUpInventory03T syncUp = new SyncUpInventory03T(Objects.requireNonNull(getActivity()).getApplicationContext()) {
                    @Override
                    protected void onPostExecute(String s) {
                        Log.d("s", "" + s);
                        if (s.trim().equalsIgnoreCase("done")) {
                            countUp[0]++;
                            Log.d("countUp", String.valueOf(countUp[0]));
                        }
//                        else {
//                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), s, Toast.LENGTH_SHORT).show();
//                        }
                    }
                };
                syncUp.execute();

                @SuppressLint("StaticFieldLeak") SyncModule.SyncUpLMDInvValueT sync = new SyncModule.SyncUpLMDInvValueT(Objects.requireNonNull(getActivity()).getApplicationContext()) {
                    @Override
                    protected void onPostExecute(String s) {
                        Log.d("s", "" + s);
                        if (s.trim().equalsIgnoreCase("done")) {
                            countUp[0]++;
                            Log.d("countUp", String.valueOf(countUp[0]));
                        }
//                        else {
//                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), s, Toast.LENGTH_SHORT).show();
//                        }
                    }
                };
                sync.execute();

                @SuppressLint("StaticFieldLeak") SyncModule.SyncUpReceiptTable sync1 = new SyncModule.SyncUpReceiptTable(Objects.requireNonNull(getActivity()).getApplicationContext()) {
                    @Override
                    protected void onPostExecute(String s) {
                        Log.d("s", "" + s);
                        if (s.trim().equalsIgnoreCase("done")) {
                            countUp[0]++;
                            Log.d("countUp", String.valueOf(countUp[0]));
                        }
//                        else {
//                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), s, Toast.LENGTH_SHORT).show();
//                        }
                    }
                };
                sync1.execute();

                @SuppressLint("StaticFieldLeak") SyncModule.SyncUpRestockT sync2 = new SyncModule.SyncUpRestockT(Objects.requireNonNull(getActivity()).getApplicationContext()) {
                    @Override
                    protected void onPostExecute(String s) {
                        Log.d("s", "" + s);
                        if (s.trim().equalsIgnoreCase("done")) {
                            countUp[0]++;
                            Log.d("countUp", String.valueOf(countUp[0]));
                        }
//                        else {
//                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), s, Toast.LENGTH_SHORT).show();
//                        }
                    }
                };
                sync2.execute();

                @SuppressLint("StaticFieldLeak") SyncModule.SyncUpTellerT sync3 = new SyncModule.SyncUpTellerT(Objects.requireNonNull(getActivity()).getApplicationContext()) {
                    @Override
                    protected void onPostExecute(String s) {
                        Log.d("s", "" + s);
                        if (s.trim().equalsIgnoreCase("done")) {
                            countUp[0]++;
                            Log.d("countUp", String.valueOf(countUp[0]));
                        }
//                        else {
//                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), s, Toast.LENGTH_SHORT).show();
//                        }

                        Log.d("CountUp", "" + countUp[0]);
                        if (countUp[0] == 5) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Sync successful", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Sync failed, " +
                                    "please check your internet connection", Toast.LENGTH_LONG).show();
                        }
                    }
                };
                sync3.execute();


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

}

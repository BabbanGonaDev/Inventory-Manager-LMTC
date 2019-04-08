package com.bgenterprise.bglmtcinventory;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

import static com.bgenterprise.bglmtcinventory.SyncModule.RefreshInventory03T;
import static com.bgenterprise.bglmtcinventory.SyncModule.RefreshLMDInvoiceValueT;
import static com.bgenterprise.bglmtcinventory.SyncModule.SyncDownInventory03T;
import static com.bgenterprise.bglmtcinventory.SyncModule.SyncDownPriceGroupT;
import static com.bgenterprise.bglmtcinventory.SyncModule.SyncUpInventory03T;
import static com.bgenterprise.bglmtcinventory.SyncModule.updateLMDT;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabFragmentSync extends Fragment {
    View view;
    SessionManager session;
    Button btnSyncDownInventory03T, btnSyncUpInventory03T, btnRefreshInventory03T, btnSyncDownPgroupT, btnSyncDownPriceT,
            btnRefreshLMDInvValueT, btnUpdateLMDT, btnUploadLmdInvValT, btnUploadReceiptT, btnUploadRestockT, btnUploadTellerT;

    public TabFragmentSync() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_tab_fragment_sync, container, false);
        session = new SessionManager(getActivity());
        btnSyncDownInventory03T = view.findViewById(R.id.btnSyncDownInventory03T);
        btnSyncUpInventory03T = view.findViewById(R.id.btnSyncUpInventory03T);
        btnRefreshInventory03T = view.findViewById(R.id.btnRefreshInventory03T);
        btnSyncDownPgroupT = view.findViewById(R.id.btnSyncDownPgroupT);
        btnSyncDownPriceT = view.findViewById(R.id.btnSyncDownPriceT);
        btnRefreshLMDInvValueT = view.findViewById(R.id.btnRefreshLMDIVT);
        btnUpdateLMDT = view.findViewById(R.id.btnUpdateLMDT);
        btnUploadLmdInvValT = view.findViewById(R.id.btnUploadLmdInvValueT);
        btnUploadReceiptT = view.findViewById(R.id.btnUploadReceiptT);
        btnUploadRestockT = view.findViewById(R.id.btnUploadRestockT);
        btnUploadTellerT = view.findViewById(R.id.btnUploadTellerT);

        btnSyncDownInventory03T.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SyncDownLMLInventory03T(v);
            }
        });

        btnSyncUpInventory03T.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SyncUpLMTCInventory03T(v);
            }
        });

        btnRefreshInventory03T.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RefreshInventory03T(v);
            }
        });

        btnSyncDownPgroupT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> val = session.getAllDetails();
                String staff_id = val.get(SessionManager.KEY_STAFF_ID);
                @SuppressLint("StaticFieldLeak") SyncDownPriceGroupT sync = new SyncDownPriceGroupT(Objects.requireNonNull(getActivity()).getApplicationContext()) {
                    @Override
                    protected void onPostExecute(String s) {
                        Log.d("s", "" + s);
                        if (s.trim().equalsIgnoreCase("done")) {
                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Sync successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                sync.execute(staff_id);
            }
        });

        btnSyncDownPriceT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> val = session.getAllDetails();
                String staff_id = val.get(SessionManager.KEY_STAFF_ID);
                @SuppressLint("StaticFieldLeak") SyncDownPriceT sync = new SyncDownPriceT(Objects.requireNonNull(getActivity()).getApplicationContext()) {
                    @Override
                    protected void onPostExecute(String s) {
                        Log.d("s", "" + s);
                        if (s.trim().equalsIgnoreCase("done")) {
                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Sync successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                sync.execute(staff_id);
            }
        });

        btnRefreshLMDInvValueT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> val = session.getAllDetails();
                String staff_id = val.get(SessionManager.KEY_STAFF_ID);
                @SuppressLint("StaticFieldLeak") RefreshLMDInvoiceValueT sync = new RefreshLMDInvoiceValueT(Objects.requireNonNull(getActivity()).getApplicationContext()) {
                    @Override
                    protected void onPostExecute(String s) {
                        Log.d("s", "" + s);
                        if (s.trim().equalsIgnoreCase("done")) {
                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Sync successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                sync.execute(staff_id);
            }
        });

        btnUpdateLMDT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> val = session.getAllDetails();
                String staff_id = val.get(SessionManager.KEY_STAFF_ID);
                @SuppressLint("StaticFieldLeak") updateLMDT sync = new SyncModule.updateLMDT(Objects.requireNonNull(getActivity()).getApplicationContext()) {
                    @Override
                    protected void onPostExecute(String s) {
                        Log.d("s", "" + s);
                        if (s.trim().equalsIgnoreCase("done")) {
                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Sync successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                sync.execute(staff_id);
            }
        });

        btnUploadLmdInvValT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("StaticFieldLeak") SyncModule.SyncUpLMDInvValueT sync = new SyncModule.SyncUpLMDInvValueT(Objects.requireNonNull(getActivity()).getApplicationContext()) {
                    @Override
                    protected void onPostExecute(String s) {
                        if (s.trim().equalsIgnoreCase("done")) {
                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Sync successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                sync.execute();
            }
        });

        btnUploadReceiptT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("StaticFieldLeak") SyncModule.SyncUpReceiptTable sync = new SyncModule.SyncUpReceiptTable(Objects.requireNonNull(getActivity()).getApplicationContext()) {
                    @Override
                    protected void onPostExecute(String s) {
                        if (s.trim().equalsIgnoreCase("done")) {
                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Sync successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                sync.execute();
            }
        });

        btnUploadRestockT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("StaticFieldLeak") SyncModule.SyncUpRestockT sync = new SyncModule.SyncUpRestockT(Objects.requireNonNull(getActivity()).getApplicationContext()) {
                    @Override
                    protected void onPostExecute(String s) {
                        if (s.trim().equalsIgnoreCase("done")) {
                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Sync successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                sync.execute();
            }
        });

        btnUploadTellerT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("StaticFieldLeak") SyncModule.SyncUpTellerT sync = new SyncModule.SyncUpTellerT(Objects.requireNonNull(getActivity()).getApplicationContext()) {
                    @Override
                    protected void onPostExecute(String s) {
                        if (s.trim().equalsIgnoreCase("done")) {
                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Sync successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                sync.execute();
            }
        });

        return view;
    }

    public void SyncDownLMLInventory03T(View view){
        CheckInternetPermission();
        HashMap<String,String> user = session.getAllDetails();
        SyncDownInventory03T syncDown = new SyncDownInventory03T(getActivity());
        syncDown.execute(user.get(SessionManager.KEY_STAFF_ID));
    }

    public void SyncUpLMTCInventory03T(View view){
        CheckInternetPermission();
        SyncUpInventory03T syncUp = new SyncUpInventory03T(getActivity());
        syncUp.execute();
    }

    public void RefreshInventory03T(View view){
        CheckInternetPermission();
        SyncUpInventory03T syncUp = new SyncUpInventory03T(getActivity());
        syncUp.execute();

        RefreshInventory03T refresh = new RefreshInventory03T(getActivity());
        refresh.execute();
    }

    public void CheckInternetPermission(){
        try {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.INTERNET}, 23);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

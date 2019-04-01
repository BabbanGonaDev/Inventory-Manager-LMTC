package com.bgenterprise.bglmtcinventory;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class TabFragmentSync extends Fragment {
    View view;
    SessionManager session;
    Button btnSyncDownInventory03T, btnSyncUpInventory03T, btnRefreshInventory03T;

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

        return view;
    }

    public void SyncDownLMLInventory03T(View view){
        CheckInternetPermission();
        HashMap<String,String> user = session.getAllDetails();
        SyncModule.SyncDownInventory03T syncDown = new SyncModule.SyncDownInventory03T(getActivity());
        syncDown.execute(user.get(SessionManager.KEY_STAFF_ID));
    }

    public void SyncUpLMTCInventory03T(View view){
        CheckInternetPermission();
        SyncModule.SyncUpInventory03T syncUp = new SyncModule.SyncUpInventory03T(getActivity());
        syncUp.execute();
    }

    public void RefreshInventory03T(View view){
        CheckInternetPermission();
        SyncModule.SyncUpInventory03T syncUp = new SyncModule.SyncUpInventory03T(getActivity());
        syncUp.execute();

        SyncModule.RefreshInventory03T refresh = new SyncModule.RefreshInventory03T(getActivity());
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

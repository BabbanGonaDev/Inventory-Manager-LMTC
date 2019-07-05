package com.bgenterprise.bglmtcinventory;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.HashMap;

/**
 * Fragment that holds all the buttons for the StockManagement section of the app in the Navigation drawer.
 */
public class FragmentStockManagement extends Fragment {
    Button btnCount, btnRestock;
    View view;
    SharedPreferences QRPrefs;
    SessionManager session;
    HashMap<String, String> allDetails;

    public FragmentStockManagement() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_stock_management, container, false);
        btnCount = view.findViewById(R.id.btnCount);
        btnRestock = view.findViewById(R.id.btnRestock);
        QRPrefs = getActivity().getSharedPreferences("QRPreferences", Context.MODE_PRIVATE);

        session = new SessionManager(getActivity());
        allDetails = session.getAllDetails();


        btnCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LMD_DBHandler lmd_dbHandler = new LMD_DBHandler(getActivity());
                if(!lmd_dbHandler.isLMTCInDB(allDetails.get(SessionManager.KEY_STAFF_ID))){
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Download Application Data")
                            .setMessage("Kindly download Application data (in Sync/Export tab) before counting.")
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                   dialog.dismiss();
                                }
                            }).show();
                }else {
                    QRPrefs.edit().putString("required", "LMD_Count").commit();
                    QRPrefs.edit().putString("scanner_title", "Scan LMD to Count").commit();
                    startActivity(new Intent(getActivity(), ScannerActivity.class));
                }

            }
        });
        return view;
    }


}

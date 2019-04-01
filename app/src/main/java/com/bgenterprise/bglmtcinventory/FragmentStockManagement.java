package com.bgenterprise.bglmtcinventory;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Fragment that holds all the buttons for the StockManagement section of the app in the Navigation drawer.
 */
public class FragmentStockManagement extends Fragment {
    Button btnCount, btnRestock;
    View view;
    SharedPreferences QRPrefs;

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

        btnCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRPrefs.edit().putString("required", "LMD_Count").commit();
                QRPrefs.edit().putString("scanner_title", "Scan LMD to Count").commit();
                startActivity(new Intent(getActivity(), ScannerActivity.class));

            }
        });
        return view;
    }

}

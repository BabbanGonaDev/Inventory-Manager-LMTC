package com.bgenterprise.bglmtcinventory;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabFragmentExport extends Fragment {
    View view;
    Button exportAllDB;

    public TabFragmentExport() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_tab_fragment_export, container, false);
        exportAllDB = view.findViewById(R.id.btn_exp_DB);

        exportAllDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                InventoryDBHandler inventoryDBHandler = new InventoryDBHandler(getActivity().getApplicationContext());
//                SQLiteDatabase db = inventoryDBHandler.getWritableDatabase();
//                Cursor c = null;
                String directory_path = Environment.getExternalStorageDirectory().getPath();
                Log.d("directory", "" + directory_path);
                SQLiteToExcel sqliteToExcel;
                final int[] count = {0};

                sqliteToExcel = new SQLiteToExcel(getActivity().getApplicationContext(), "inventory.db", directory_path + "/Exports");
                sqliteToExcel.exportAllTables("inventoryDB.xls", new SQLiteToExcel.ExportListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onCompleted(String filePath) {
                        count[0]++;
                        Log.d("count", "" + count[0]);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("e", "" + e);
                    }
                });

                sqliteToExcel = new SQLiteToExcel(getActivity().getApplicationContext(), "invoices.db", directory_path + "/Exports");
                sqliteToExcel.exportAllTables("invoicesDB.xls", new SQLiteToExcel.ExportListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onCompleted(String filePath) {
                        count[0]++;
                        Log.d("count", "" + count[0]);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("e", "" + e);
                    }
                });

                sqliteToExcel = new SQLiteToExcel(getActivity().getApplicationContext(), "lmd.db", directory_path + "/Exports");
                sqliteToExcel.exportAllTables("LmdDB.xls", new SQLiteToExcel.ExportListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onCompleted(String filePath) {
                        count[0]++;
                        Log.d("count", "" + count[0]);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("e", "" + e);
                    }
                });

                sqliteToExcel = new SQLiteToExcel(getActivity().getApplicationContext(), "receipts.db", directory_path + "/Exports");
                sqliteToExcel.exportAllTables("receiptsDB.xls", new SQLiteToExcel.ExportListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onCompleted(String filePath) {
                        count[0]++;
                        Log.d("count", "" + count[0]);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("e", "" + e);
                    }
                });

                sqliteToExcel = new SQLiteToExcel(getActivity().getApplicationContext(), "restocks.db", directory_path + "/Exports");
                sqliteToExcel.exportAllTables("restocksDB.xls", new SQLiteToExcel.ExportListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onCompleted(String filePath) {
                        count[0]++;
                        Log.d("count", "" + count[0]);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("e", "" + e);
                    }
                });

                sqliteToExcel = new SQLiteToExcel(getActivity().getApplicationContext(), "tellers.db", directory_path + "/Exports");
                sqliteToExcel.exportAllTables("tellersDB.xls", new SQLiteToExcel.ExportListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onCompleted(String filePath) {
                        count[0]++;
                        Log.d("count", "final" + count[0]);
                        Toast.makeText(getActivity().getApplicationContext(), "Exported Successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("e", "" + e);
                    }
                });

            }
        });

        return view;
    }


}

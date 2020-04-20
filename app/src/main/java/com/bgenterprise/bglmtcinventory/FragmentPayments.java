package com.bgenterprise.bglmtcinventory;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;


/**
 * Fragment that holds all the buttons for the payments section of the app.
 */

public class FragmentPayments extends Fragment {
    View view;
    Button btnInvoices, btnReceivables, btnReceipts, btnTellers;

    public FragmentPayments() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_payments, container, false);
        btnInvoices = view.findViewById(R.id.btnInvoices);
        btnReceivables = view.findViewById(R.id.btnReceivables);
        btnReceipts = view.findViewById(R.id.btnReceipts);
        btnTellers = view.findViewById(R.id.btnTellers);

        /*btnReceipts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ReceiptsPage.class));
            }
        });*/

        btnInvoices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), InvoicesPage.class));
            }
        });

        /*btnTellers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), TellerPage.class));
            }
        });

        btnReceivables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), View_Receivable.class));
            }
        });*/


        return view;
    }

}

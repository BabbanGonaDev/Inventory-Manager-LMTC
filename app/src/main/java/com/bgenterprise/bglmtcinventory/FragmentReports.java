package com.bgenterprise.bglmtcinventory;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment that holds all the buttons for the Reports section of the app in the Navigation drawer.
 */

public class FragmentReports extends Fragment {
    View view;

    public FragmentReports() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_reports, container, false);

        startActivity(new Intent(getActivity(), ReportsPage.class));

        return view;
    }

}

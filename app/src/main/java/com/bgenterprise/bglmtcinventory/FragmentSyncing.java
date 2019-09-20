package com.bgenterprise.bglmtcinventory;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;


/**
 * Fragment subclass that holds all the buttons for the syncing section of the app.
 */
public class FragmentSyncing extends Fragment {


    public FragmentSyncing() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        startActivity(new Intent(getActivity(), DataActivity.class));
        return inflater.inflate(R.layout.fragment_syncing, container, false);


    }

}

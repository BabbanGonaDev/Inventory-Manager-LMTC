package com.bgenterprise.bglmtcinventory;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentWelcome extends Fragment {
    /*FloatingActionButton fab_welcome;*/
    SpeedDialView speedDialView;
    View view;

    public FragmentWelcome() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_welcome, container, false);
        /*fab_welcome = view.findViewById(R.id.floating_action_button);*/

        speedDialView = view.findViewById(R.id.speedDial);
        speedDialView.inflate(R.menu.speeddial_menu);
        speedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {
                switch (speedDialView.getId()) {
                    case R.id.dialCount:

                        return false;
                    case R.id.dialReceipt:
                        startActivity(new Intent(getActivity(), ReceiptsPage.class));
                        return false;
                    case R.id.dialTeller:
                        startActivity(new Intent(getActivity(), NewTeller.class));
                        return false;
                    default:
                        return false;
                }
            }
        });
        return view;
    }

}

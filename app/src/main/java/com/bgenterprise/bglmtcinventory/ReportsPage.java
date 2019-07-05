package com.bgenterprise.bglmtcinventory;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReportsPage extends AppCompatActivity {

    private RecyclerView rvLMD;
    private List<LMD> lmdList;
    private LMDAdapter lmdAdapter;
    SessionManager session;
    HashMap<String, String> allDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports_page);

        rvLMD = findViewById(R.id.rvLMD);
        lmdList =  new ArrayList<>();
        session = new SessionManager(ReportsPage.this);
        allDetails = session.getAllDetails();
        LMD_DBHandler lmdDbHandler = new LMD_DBHandler(ReportsPage.this);

        Toolbar searchLMDToolbar = (Toolbar) findViewById(R.id.lmdToolbar);
        setSupportActionBar(searchLMDToolbar);

        lmdList = lmdDbHandler.getLMTCLMDs(allDetails.get(SessionManager.KEY_STAFF_ID));

        lmdAdapter = new LMDAdapter(getApplicationContext(), lmdList, new LMDAdapter.OnItemClickListener() {
            @Override
            public void onClick(LMD lmd) {
                Log.d("CHECK", "Onclick pass to View_reports.");
                Intent intent = new Intent(ReportsPage.this, View_Reports.class);
                intent.putExtra("LMDID", lmd.getLMDID());
                intent.putExtra("LMDNAME", lmd.getLMDName());
                startActivity(intent);
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvLMD.setLayoutManager(layoutManager);
        rvLMD.setItemAnimator(new DefaultItemAnimator());
        rvLMD.setAdapter(lmdAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu);

        MenuItem search = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return super.onOptionsItemSelected(item);
    }

    private void search(SearchView searchView){

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                lmdAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(ReportsPage.this, Operations.class));
    }
}

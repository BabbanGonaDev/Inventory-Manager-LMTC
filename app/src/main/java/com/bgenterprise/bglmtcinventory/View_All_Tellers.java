package com.bgenterprise.bglmtcinventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity that displays all tellers in the database.
 */
public class View_All_Tellers extends AppCompatActivity {
    private RecyclerView recyclerTeller;
    private List<Tellers> tellersList;
    private TellersAdapter tellersAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__all__tellers);
        recyclerTeller = findViewById(R.id.recyclerTeller);

        tellersList = new ArrayList<>();

        TellerDBHandler dbHandler = new TellerDBHandler(View_All_Tellers.this);
        tellersList = dbHandler.getAllTellers();

        System.out.print(tellersList);

        tellersAdapter = new TellersAdapter(getApplicationContext(), tellersList, new TellersAdapter.OnItemClickListener() {
            @Override
            public void onClick(Tellers tellers) {
                //Place the on click function here.

                Intent intent = new Intent(View_All_Tellers.this, View_Teller_Receipts.class);
                intent.putExtra("tellerID", tellers.getTeller_id());
                intent.putExtra("tellerBank", tellers.getTeller_bank());
                intent.putExtra("tellerAmount", tellers.getTeller_amount());
                intent.putExtra("tellerDate", tellers.getTeller_date());
                startActivity(intent);

            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerTeller.setLayoutManager(layoutManager);
        recyclerTeller.setItemAnimator(new DefaultItemAnimator());
        recyclerTeller.setAdapter(tellersAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu, menu);

//        MenuItem search = menu.findItem(R.id.app_bar_search);
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
//        search(searchView);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

//    private void search(SearchView searchView){
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                tellersAdapter.getFilter().filter(newText);
//                return true;
//            }
//        });
//    }
}

package com.bgenterprise.bglmtcinventory;

/**
 * Activity that displays all receipts in the database.
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class View_All_Receipts extends AppCompatActivity {

    private RecyclerView ReceiptRecycler;
    private List<Receipts> receiptsList;
    private AllReceiptsAdapter allReceiptsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__all__receipts);
        ReceiptRecycler = findViewById(R.id.ReceiptRecycler);

        Toolbar searchToolbar = findViewById(R.id.SearchToolbar);
        setSupportActionBar(searchToolbar);

        receiptsList = new ArrayList<>();

        ReceiptDBHandler receiptDBHandler = new ReceiptDBHandler(View_All_Receipts.this);
        receiptsList = receiptDBHandler.getAllReceipts();

        allReceiptsAdapter = new AllReceiptsAdapter(getApplicationContext(), receiptsList, new AllReceiptsAdapter.OnItemClickListener() {
            @Override
            public void onClick(Receipts receipts) {
                Intent intent = new Intent(View_All_Receipts.this, View_LMD_Receipts.class);
                intent.putExtra("lmd_id", receipts.getLmdID());
                intent.putExtra("lmd_name", receipts.getLmdName());
                startActivity(intent);
            }
        });

        RecyclerView.LayoutManager mLayoutmanager = new LinearLayoutManager(getApplicationContext());
        ReceiptRecycler.setLayoutManager(mLayoutmanager);
        ReceiptRecycler.setItemAnimator(new DefaultItemAnimator());
        ReceiptRecycler.setAdapter(allReceiptsAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.search_menu, menu);

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
                allReceiptsAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }
}

package com.enginious.snowblossom;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.enginious.snowblossom.adapters.TransactionsAdapter;
import com.enginious.snowblossom.models.Transaction;

import java.util.ArrayList;
import java.util.List;


public class TransactionsActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.activity_transactions_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");


        //ButterKnife.bind(this);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_activity_transactions);

        List<Transaction> transactionsList = new ArrayList<>();
        TransactionsAdapter mAdapter;

        mAdapter = new TransactionsAdapter(transactionsList,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        transactionsList.add(new Transaction());
        transactionsList.add(new Transaction());
        transactionsList.add(new Transaction());
        transactionsList.add(new Transaction());
        transactionsList.add(new Transaction());
        transactionsList.add(new Transaction());

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}

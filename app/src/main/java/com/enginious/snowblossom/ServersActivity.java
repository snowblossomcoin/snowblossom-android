package com.enginious.snowblossom;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.enginious.snowblossom.adapters.ServersAdapter;
import com.enginious.snowblossom.adapters.TransactionsAdapter;
import com.enginious.snowblossom.models.Server;
import com.enginious.snowblossom.models.Transaction;

import java.util.ArrayList;
import java.util.List;

public class ServersActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servers);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.activity_servers_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        recyclerView = (RecyclerView)findViewById(R.id.recycler_activity_servers);

        List<Server> serverList = new ArrayList<>();
        ServersAdapter mAdapter;

        mAdapter = new ServersAdapter(serverList,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        serverList.add(new Server("node.snowblossom.cluelessperson.com","",true));
        serverList.add(new Server("node.snowblossom.cluelessperson.com","",false));
        serverList.add(new Server("node.snowblossom.cluelessperson.com","",false));
        serverList.add(new Server("node.snowblossom.cluelessperson.com","",false));
        serverList.add(new Server("node.snowblossom.cluelessperson.com","",false));
        serverList.add(new Server("node.snowblossom.cluelessperson.com","",false));
        serverList.add(new Server("node.snowblossom.cluelessperson.com","",false));
        serverList.add(new Server("node.snowblossom.cluelessperson.com","",false));
        serverList.add(new Server("node.snowblossom.cluelessperson.com","",false));
        serverList.add(new Server("node.snowblossom.cluelessperson.com","",false));

        mAdapter.notifyDataSetChanged();


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}

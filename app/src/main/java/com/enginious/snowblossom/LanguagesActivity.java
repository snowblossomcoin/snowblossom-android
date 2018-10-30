package com.enginious.snowblossom;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.enginious.snowblossom.adapters.LanguagesAdapter;
import com.enginious.snowblossom.adapters.ServersAdapter;
import com.enginious.snowblossom.models.Server;

import java.util.ArrayList;
import java.util.List;

public class LanguagesActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_languages);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.activity_languages_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        recyclerView = (RecyclerView)findViewById(R.id.recycler_activity_languages);

        List<String> langs = new ArrayList<>();
        LanguagesAdapter mAdapter;

        mAdapter = new LanguagesAdapter(langs,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);


        langs.add("");
        langs.add("");
        langs.add("");
        langs.add("");
        langs.add("");
        langs.add("");
        langs.add("");
        langs.add("");
        langs.add("");


        mAdapter.notifyDataSetChanged();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}

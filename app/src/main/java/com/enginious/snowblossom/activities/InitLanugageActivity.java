package com.enginious.snowblossom.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;

import com.enginious.snowblossom.R;
import com.enginious.snowblossom.SelectServerActivity;
import com.enginious.snowblossom.adapters.LanguagesAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InitLanugageActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_lanugage);






        prefs = getSharedPreferences("configs",MODE_PRIVATE);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_activity_initlanguages);

        List<String> langs = new ArrayList<>();
        LanguagesAdapter mAdapter;

        mAdapter = new LanguagesAdapter(langs,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);


        langs.add("English");
        langs.add("Español");




        mAdapter.notifyDataSetChanged();

        mAdapter.setOnItemClickListener(new LanguagesAdapter.OnItemClickListener() {
            @Override
            public void setOnItemClickListener(View view, int position) {
                if(position == 0){

                    changeLanguage("en");

                }else if(position == 1){

                    changeLanguage("es");

                }
            }
        });

    }
    public void changeLanguage(String lang ){
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        prefs.edit().putString("locale",lang).apply();

//        Intent intent = new Intent(InitLanugageActivity.this,SelectServerActivity.class);
//        startActivity(intent);
//        Intent i = getBaseContext().getPackageManager()
//                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(i);

        Intent i = new Intent(InitLanugageActivity.this.getBaseContext(), SelectServerActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(i);


    }
}

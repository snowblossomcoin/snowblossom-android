package com.enginious.snowblossom.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.enginious.snowblossom.LanguagesActivity;
import com.enginious.snowblossom.R;
import com.enginious.snowblossom.ServersActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.linear_layout_server_activity)
    LinearLayout serverLayout;
    @BindView(R.id.linear_layout_language_activity)
    LinearLayout languageLayout;
    @BindView(R.id.linear_layout_wallet_activity)
    LinearLayout walletLayout;
    @BindView(R.id.linear_layout_about_activity)
    LinearLayout aboutLayout;

    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        prefs = getSharedPreferences("configs",MODE_PRIVATE);

        int net = prefs.getInt("net",0);

        if(net == 2) {

            setTheme(R.style.AppThemeTest);
        }

        setContentView(R.layout.activity_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_settings));
        ButterKnife.bind(this);
        serverLayout.setOnClickListener(this);
        languageLayout.setOnClickListener(this);
        walletLayout.setOnClickListener(this);
        aboutLayout.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.linear_layout_server_activity){

            Intent intent = new Intent(this,ServersActivity.class);
            startActivity(intent);

        }else if(view.getId() == R.id.linear_layout_language_activity){

            Intent intent = new Intent(this,LanguagesActivity.class);
            startActivity(intent);

        }else if(view.getId() == R.id.linear_layout_wallet_activity){
            Intent intent = new Intent(this,WalletActivity.class);
            startActivity(intent);
        }else if(view.getId() == R.id.linear_layout_about_activity){
            Intent intent = new Intent(this,AboutActivity.class);
            startActivity(intent);
        }

    }
}

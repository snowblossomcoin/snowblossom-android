package com.enginious.snowblossom.activities;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");
        ButterKnife.bind(this);
        serverLayout.setOnClickListener(this);
        languageLayout.setOnClickListener(this);
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

        }

    }
}

package com.enginious.snowblossom.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.enginious.snowblossom.R;
import com.enginious.snowblossom.WalletHelper;

import java.util.ArrayList;
import java.util.List;

import snowblossom.client.SnowBlossomClient;
import snowblossom.proto.WalletDatabase;

public class SeedPreviewActivity extends AppCompatActivity {

    Button btncopy;
    Button btnNext;
    TextView Txtseed;


    SnowBlossomClient client;

    String seed = "";

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("configs",MODE_PRIVATE);

        int net = prefs.getInt("net",0);

        if(net == 2) {

            setTheme(R.style.AppThemeTest);
        }

        setContentView(R.layout.activity_seed_preview);
        btncopy = (Button)findViewById(R.id.button_copy_seedpreview);
        Txtseed = (TextView) findViewById(R.id.textView_seed_seedpreview);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Export Seed");

        client = WalletHelper.getClient();


        if(client != null) {
            WalletDatabase db = client.getPurse().getDB();

            List<String> seeds = new ArrayList<>();

            for (String sd : db.getSeedsMap().keySet()) {

                seeds.add(sd);


            }

            seed = seeds.get(0);

            Txtseed.setText(seeds.get(0));
        }

        Txtseed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("seed",seed);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(SeedPreviewActivity.this,"Seed Copied",Toast.LENGTH_SHORT).show();
            }
        });

        btncopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("seed",seed);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(SeedPreviewActivity.this,"Seed Copied",Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}

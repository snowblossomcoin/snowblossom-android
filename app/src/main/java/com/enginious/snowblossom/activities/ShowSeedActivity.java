package com.enginious.snowblossom.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.enginious.snowblossom.MainActivity;
import com.enginious.snowblossom.R;
import com.enginious.snowblossom.WalletHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import snowblossom.client.SeedReport;
import snowblossom.client.SnowBlossomClient;
import snowblossom.client.WalletUtil;
import snowblossom.proto.WalletDatabase;

public class ShowSeedActivity extends AppCompatActivity {


    Button btncopy;
    Button btnNext;
    TextView Txtseed;


    SnowBlossomClient client;

    String seed = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_seed);

        btncopy = (Button)findViewById(R.id.button_copy_seed);
        Txtseed = (TextView) findViewById(R.id.textView_seed_seed);

        btnNext = (Button)findViewById(R.id.button_next_seed);


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

                Toast.makeText(ShowSeedActivity.this,"Seed Copied",Toast.LENGTH_SHORT).show();
            }
        });

        btncopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("seed",seed);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(ShowSeedActivity.this,"Seed Copied",Toast.LENGTH_SHORT).show();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                // set the new task and clear flags
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

    }


}

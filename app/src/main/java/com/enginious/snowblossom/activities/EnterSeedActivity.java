package com.enginious.snowblossom.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.enginious.snowblossom.MainActivity;
import com.enginious.snowblossom.R;
import com.enginious.snowblossom.SelectServerActivity;

import snowblossom.client.SeedUtil;
import snowblossom.lib.ValidationException;

public class EnterSeedActivity extends AppCompatActivity {


    Button btnNext;
    EditText txtSeed;

    String seed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterseedactivity);

        btnNext = (Button)findViewById(R.id.button_next_enterseed);
        txtSeed = (EditText)findViewById(R.id.editText_enterseed);


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seed = txtSeed.getText().toString().trim();
                if(seed.length() < 1 || seed.equals("")){

                    Toast.makeText(EnterSeedActivity.this,"Seed Cannot be Empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    SeedUtil.checkSeed(seed);
                } catch (ValidationException e) {
                    Toast.makeText(EnterSeedActivity.this,"Invalid Seed",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return;
                }

                Intent i = new Intent(EnterSeedActivity.this, SelectServerActivity.class);

                i.putExtra("import",false);
                i.putExtra("is_seed",true);
                i.putExtra("seed",seed);
                EnterSeedActivity.this.startActivity(i);
            }
        });

    }

}

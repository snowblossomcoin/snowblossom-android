package com.enginious.snowblossom;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;
import com.enginious.snowblossom.activities.HomeActivity;

import java.util.TreeMap;

import snowblossom.client.SnowBlossomClient;
import snowblossom.lib.AddressSpecHash;
import snowblossom.lib.Globals;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;
    SharedPreferences prefs ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        prefs = getSharedPreferences("configs",MODE_PRIVATE);

        Boolean is_config = prefs.getBoolean("config", false);


        if(is_config){

            loadWallet();

        }else {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashActivity.this, SelectServerActivity.class);
                    startActivity(i);
                    // close this activity
                    finish();

                }
            }, SPLASH_TIME_OUT);
        }

    }

    @SuppressLint("StaticFieldLeak")
    private void loadWallet(){



            Boolean is_config = prefs.getBoolean("config",false);

            if(is_config) {

                String url = prefs.getString("url", null);
                String port = prefs.getString("port", null);

                String file_path  = prefs.getString("wallet_path",null);

                url = url.trim();
                url = url.replace(" ", "");

                port = port.trim();
                port = port.replace(" ", "");

                if (url.isEmpty() || port.isEmpty()) {

                    return;
                }
                if(file_path == null){
                    return;
                }
                int net = prefs.getInt("net", 0);
                Globals.addCryptoProviderAndroid();
                final TreeMap<String, String> configs = new TreeMap<>();
                configs.put("node_host", url);
                configs.put("node_port", port);
                if (net == 1) {
                    configs.put("network", "mainnet");
                } else {
                    configs.put("network", "testnet");
                }
                configs.put("wallet_path", file_path);

                try {
                    new AsyncTask<Void,Void,Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            try {

                                WalletHelper.InitClient(configs);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);

                            Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                            startActivity(i);
                            // close this activity
                            finish();

                        }
                    }.execute();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

    }


}

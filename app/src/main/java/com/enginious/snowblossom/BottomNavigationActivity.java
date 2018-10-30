package com.enginious.snowblossom;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.TreeMap;

import butterknife.ButterKnife;
import snowblossom.client.SnowBlossomClient;
import snowblossom.lib.AddressSpecHash;
import snowblossom.lib.Globals;

public class BottomNavigationActivity extends AppCompatActivity {



    SharedPreferences prefs;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment fragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new HomeFragment();
                    break;
                case R.id.navigation_send:
                    fragment = new SendFragment();
                    break;

                case R.id.navigation_receive:
                    fragment = new ReceiveFragment();
                    break;
                case R.id.navigation_settings:
                    fragment = new SettingsFragment();
                    break;

            }
            return loadFragment(fragment);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);
        ButterKnife.bind(this);

        prefs = getSharedPreferences("configs",MODE_PRIVATE);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        new Runnable(){
            @Override
            public void run() {
                loadWallet();
            }
        }.run();

    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    private boolean loadFragment(Fragment fragment){

        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,fragment)
                    .commit();
            return true;
        }
        return false;

    }
    @SuppressLint("StaticFieldLeak")
    void loadWallet(){

        final SnowBlossomClient client = WalletHelper.getClient();

        if (client!=null){
            try {
                AddressSpecHash ash = client.getPurse().getUnusedAddress(false, false);
                final String add = ash.toAddressString(client.getParams());

                loadFragment(new HomeFragment());


            }catch (Exception e){
                e.printStackTrace();
            }

        }else{

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



                   MaterialDialog.Builder builder =  new MaterialDialog.Builder(this)
                            .title("Please Wait")
                            .content("Loading Wallet")
                            .cancelable(false)
                            .titleColor(ContextCompat.getColor(this, R.color.colorPrimary))
                            .widgetColor(ContextCompat.getColor(this, R.color.PurpleLight))
                            .contentColor(ContextCompat.getColor(this, R.color.lightGray))
                            .progress(true, 0);

                   final MaterialDialog dialog = builder.build();
                   dialog.show();


                    new AsyncTask<Void,Void,Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            try {
                                final SnowBlossomClient clt = WalletHelper.InitClient(configs);
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                        // Stuff that updates the UI
                                        loadFragment(new HomeFragment());
                                        Toast.makeText(BottomNavigationActivity.this,"Connected",Toast.LENGTH_LONG).show();
                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            return null;
                        }


                    }.execute();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
}

package com.enginious.snowblossom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.enginious.snowblossom.adapters.ServersAdapter;
import com.enginious.snowblossom.adapters.TransactionsAdapter;
import com.enginious.snowblossom.models.Server;
import com.enginious.snowblossom.models.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import snowblossom.lib.Globals;

public class ServersActivity extends AppCompatActivity implements View.OnClickListener{



    SharedPreferences prefs;

    @BindView(R.id.btn_connect_server_setting)
    Button btnConnect;

    @BindView(R.id.et_url_server_setting)
    EditText etServer;

    @BindView(R.id.et_port_server_setting)
    EditText etPort;

    String url, port , path;

    int net;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servers);


        ButterKnife.bind(this);
        prefs = getSharedPreferences("configs",MODE_PRIVATE);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.activity_servers_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        btnConnect.setOnClickListener(this);


        url = prefs.getString("url",null);
        port = prefs.getString("port",null);
        path = prefs.getString("wallet_path",null);
        net = prefs.getInt("net",-1);


        etPort.setText(port);
        etServer.setText(url);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        SharedPreferences.Editor editor = prefs.edit();

        String n_url = etServer.getText().toString();
        String n_port = etPort.getText().toString();


        Globals.addCryptoProviderAndroid();
        TreeMap<String, String> configs = new TreeMap<>();
        configs.put("node_host", url);
        configs.put("node_port", port);
        if (net == 1) {
            configs.put("network", "mainnet");
        } else {
            configs.put("network", "testnet");
        }
        configs.put("wallet_path", path);


        try {
            WalletHelper.InitClient(configs);
            Log.d("error-check","checked");


            editor.putInt("net", net);
            editor.putBoolean("config",true);
            editor.putString("url", n_url);
            editor.putString("port", n_port);

            editor.apply();

            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage( getBaseContext().getPackageName() );
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);


        }catch (Exception e){
            e.printStackTrace();
        }




    }
}

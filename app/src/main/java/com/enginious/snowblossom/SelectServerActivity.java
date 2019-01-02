package com.enginious.snowblossom;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.enginious.snowblossom.activities.HomeActivity;
import com.google.protobuf.util.JsonFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import snowblossom.client.SnowBlossomClient;
import snowblossom.client.WalletUtil;
import snowblossom.lib.Globals;
import snowblossom.proto.WalletDatabase;

public class SelectServerActivity extends AppCompatActivity implements View.OnClickListener{


    @BindView(R.id.radio_net_server)
    RadioGroup radioGroup;

    @BindView(R.id.btn_connect_server)
    Button btnConnect;

    @BindView(R.id.et_url_server)
    EditText etUrl;

    @BindView(R.id.et_port_server)
    EditText etPort;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_server);
        ButterKnife.bind(this);
        prefs = getSharedPreferences("configs",MODE_PRIVATE);

        Boolean is_config = prefs.getBoolean("config",false);


        btnConnect.setOnClickListener(this);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.radioMain){
                    etUrl.setText("client-nodes.snowblossom.org");
                    etPort.setText("2338");
                }else{
                    etUrl.setText("snow-usw1.snowblossom.org");
                    etPort.setText("2339");

                    //node.snowblossom.cluelessperson.com
                    //snow-usw1.snowblossom.org
                }
            }
        });
        radioGroup.check(R.id.radioMain);
    }


    @Override
    public void onClick(View view) {

        String url = etUrl.getText().toString();
        String port = etPort.getText().toString();

        url = url.trim();
        url = url.replace(" ","");

        port = port.trim();
        port = port.replace(" ","");

        if(url.isEmpty() || port.isEmpty()){

            return;
        }

        int btn_id = radioGroup.getCheckedRadioButtonId();

        int net = 1;

        if  (btn_id != R.id.radioMain){
            net = 2;
        }

        Globals.addCryptoProviderAndroid();
        final TreeMap<String, String> configs = new TreeMap<>();
        configs.put("node_host", url);
        configs.put("node_port", port);
        if (net == 1) {
            configs.put("network", "mainnet");
        }else{
            configs.put("network", "testnet");
        }



        Boolean is_import = getIntent().getBooleanExtra("import",false);

        if(is_import){
            File f = (File)getIntent().getExtras().get("file");
            importWallet(f,url,port,net);
        }else{
            createWallet(url,port,net);
        }


        try {


//            Intent intent = new Intent(SelectServerActivity.this,MainActivity.class);
//            Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
//                // set the new task and clear flags
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            //startActivity(intent);
//            //finish();
//
//            intent.putExtra("net", net);
//            intent.putExtra("config", true);
//            intent.putExtra("url", url);
//            intent.putExtra("port", port);
//
//
//
//            startActivity(intent);
//            finish();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(SelectServerActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }


    private void createWallet(String url,String port,int net){

//        Boolean is_config = getIntent().getBooleanExtra("config",false);
//
//        if(is_config){

//            String url = getIntent().getStringExtra("url");
//            String port = getIntent().getStringExtra("port");

            url = url.trim();
            url = url.replace(" ", "");

            port = port.trim();
            port = port.replace(" ", "");
            if (url.isEmpty() || port.isEmpty()) {

                return;
            }




            Globals.addCryptoProviderAndroid();
            TreeMap<String, String> configs = new TreeMap<>();
            configs.put("node_host", url);
            configs.put("node_port", port);
            if (net == 1) {
                configs.put("network", "mainnet");
            } else {
                configs.put("network", "testnet");
            }

            ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
            File internal_file =  contextWrapper.getDir(getFilesDir().getName(), Context.MODE_PRIVATE);

            String filename_db = "wallet_db_"+System.currentTimeMillis();
            File wallet_path = new File(internal_file, filename_db);

            String path = wallet_path.getAbsolutePath();

            prefs.edit().putString("wallet_path",path).apply();

            configs.put("wallet_path", wallet_path.getAbsolutePath());

            try {
                SnowBlossomClient client = WalletHelper.InitClient(configs);
                // moving to bottom navigation


                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("net", net);
                editor.putBoolean("config",true);
                editor.putString("url", url);
                editor.putString("port", port);
                editor.apply();

                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                // set the new task and clear flags
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            } catch (Exception e) {
                e.printStackTrace();
            }


    }

    private void importWallet(File txt_file, String url,String port, int net){
        //if(is_config){

//            String url = getIntent().getStringExtra("url");
//            String port = getIntent().getStringExtra("port");

            url = url.trim();
            url = url.replace(" ", "");

            port = port.trim();
            port = port.replace(" ", "");
            if (url.isEmpty() || port.isEmpty()) {
                return;
            }
            //int net = getIntent().getIntExtra("net",0);

            Globals.addCryptoProviderAndroid();
            TreeMap<String, String> configs = new TreeMap<>();
            configs.put("node_host", url);
            configs.put("node_port", port);
            if (net == 1) {
                configs.put("network", "mainnet");
            } else {
                configs.put("network", "testnet");
            }

            ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
            File internal_file =  contextWrapper.getDir(getFilesDir().getName(), Context.MODE_PRIVATE);

            String filename_db = "wallet_db_"+ System.currentTimeMillis();
            File wallet_path = new File(internal_file, filename_db);

            String path = wallet_path.getAbsolutePath();

            prefs.edit().putString("wallet_path",path).apply();

            configs.put("wallet_path", wallet_path.getAbsolutePath());

            try {

                SnowBlossomClient client = WalletHelper.InitClient(configs);
                // Parsing txt file


                JsonFormat.Parser parser = JsonFormat.parser();
                WalletDatabase.Builder wallet_import = WalletDatabase.newBuilder();
                Reader input = new InputStreamReader(new FileInputStream(txt_file));
                parser.merge(input, wallet_import);

                WalletUtil.testWallet( wallet_import.build() );
                client.getPurse().mergeIn(wallet_import.build());

                client.printBasicStats(wallet_import.build());

                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("net", net);
                editor.putBoolean("config",true);
                editor.putString("url", url);
                editor.putString("port", port);
                editor.apply();

                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                // set the new task and clear flags
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }catch (Exception e){
                e.printStackTrace();
            }

        //}

            //end creating wallet
    }
}

package com.enginious.snowblossom;

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

import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import snowblossom.lib.Globals;

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
                    etUrl.setText("node.snowblossom.cluelessperson.com");
                    etPort.setText("2339");
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




        try {


            Intent intent = new Intent(SelectServerActivity.this,MainActivity.class);

            intent.putExtra("net", net);
            intent.putExtra("config", true);
            intent.putExtra("url", url);
            intent.putExtra("port", port);



            startActivity(intent);
            //finish();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(SelectServerActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }






    }
}

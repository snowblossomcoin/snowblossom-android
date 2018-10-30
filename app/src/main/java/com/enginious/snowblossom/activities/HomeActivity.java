package com.enginious.snowblossom.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.enginious.snowblossom.R;
import com.enginious.snowblossom.WalletHelper;
import com.enginious.snowblossom.interfaces.WalletBalanceInterface;

import butterknife.BindView;
import butterknife.ButterKnife;
import snowblossom.client.SnowBlossomClient;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.btn_camera_home_activity)
    Button btn_camera;

    @BindView(R.id.btn_receive_home_activity)
    Button btn_receive;

    @BindView(R.id.btn_send_home_activity)
    Button btn_send;

    @BindView(R.id.btn_settings_home_activity)
    Button btn_settings;

    @BindView(R.id.btn_refresh_home_activity)
    Button btn_refresh;

    @BindView(R.id.txt_connection_home_activity)
    TextView txt_connection;

    @BindView(R.id.txt_balance_activty_home)
    TextView txt_balance;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        prefs = getSharedPreferences("configs",MODE_PRIVATE);


        //adding listeners to all four buttons
        btn_camera.setOnClickListener(this);
        btn_receive.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        btn_settings.setOnClickListener(this);
        btn_refresh.setOnClickListener(this);

        //loading and connecting wallet
        loadWallet();


    }

    @Override
    protected void onResume() {
        super.onResume();

        long balance = WalletHelper.balance;
        final double spendable_flakes = (double)balance;
        final double spendable = spendable_flakes/(double)1000000;
        txt_balance.setText(""+spendable);

    }

    @Override
    public void onClick(View view) {

        Intent intent = null;
       switch (view.getId()){

           case R.id.btn_receive_home_activity:
               intent = new Intent(this,ReceiveActivity.class);

               break;
           case R.id.btn_camera_home_activity:

               break;
           case R.id.btn_send_home_activity:
               intent = new Intent(this,SendActivity.class);
               break;
           case R.id.btn_settings_home_activity:
               intent = new Intent(this,SettingActivity.class);
               break;

           case R.id.btn_refresh_home_activity:

               loadBalance();

               break;

       }
       if(intent!=null) {
           startActivity(intent);
       }
    }

    @SuppressLint("StaticFieldLeak")
    private void loadWallet(){

        final SnowBlossomClient client = WalletHelper.getClient();
        if (client!=null){
            try {
                txt_connection.setText("Connected");
                loadBalance();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void loadBalance(){

        MaterialDialog.Builder builder =  new MaterialDialog.Builder(HomeActivity.this)
                .title("Please Wait")
                .content("Calculating Spendable")
                .cancelable(false)
                .titleColor(ContextCompat.getColor(HomeActivity.this, R.color.colorPrimary))
                .widgetColor(ContextCompat.getColor(HomeActivity.this, R.color.PurpleLight))
                .contentColor(ContextCompat.getColor(HomeActivity.this, R.color.lightGray))
                .progress(true, 0);

        final MaterialDialog dialog = builder.build();
        dialog.show();

        WalletHelper.calculateBalance(new WalletBalanceInterface() {
            @Override
            public void balanceRetrieved(long balance) {
                final double spendable_flakes = (double)balance;
                final double spendable = spendable_flakes/(double)1000000;
                WalletHelper.balance = balance;
                //Updates the UI
                txt_balance.setText("" + spendable);
                dialog.dismiss();
            }

        });
    }
}

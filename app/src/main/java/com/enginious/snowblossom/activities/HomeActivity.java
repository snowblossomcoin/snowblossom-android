package com.enginious.snowblossom.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.enginious.snowblossom.R;
import com.enginious.snowblossom.Services.BalanceService;
import com.enginious.snowblossom.TimeAgo;
import com.enginious.snowblossom.WalletHelper;
import com.enginious.snowblossom.interfaces.WalletBalanceInterface;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import snowblossom.client.SnowBlossomClient;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String mBroadcastBalanceAction = "com.enginious.snowblossom.balance";


    @BindView(R.id.btn_camera_home_activity)
    Button btn_camera;

    @BindView(R.id.btn_receive_home_activity)
    Button btn_receive;

    @BindView(R.id.btn_send_home_activity)
    Button btn_send;

    @BindView(R.id.btn_settings_home_activity)
    Button btn_settings;

    @BindView(R.id.btn_refresh_home_activity)
    TextView btn_refresh;

    @BindView(R.id.txt_connection_home_activity)
    TextView txt_connection;

    @BindView(R.id.txt_balance_activty_home)
    TextView txt_balance;

    @BindView(R.id.txt_snow_activty_home)
    TextView txt_snow;


    SharedPreferences prefs;

    Date refreshDate;

    private IntentFilter mIntentFilter;

    private Timer mTimer = null;
    private Handler mHandler = new Handler();

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

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastBalanceAction);


        mTimer = new Timer();





    }

    @Override
    protected void onResume() {
        super.onResume();

        long balance = WalletHelper.balance;
        final double spendable_flakes = (double)balance;
        final double spendable = spendable_flakes/(double)1000000;
        txt_balance.setText(""+spendable);
        registerReceiver(mReceiver, mIntentFilter);

    }

    @Override
    protected void onPause() {
        unregisterReceiver(mReceiver);
        super.onPause();
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


                txt_connection.setText(getString(R.string.title_connected_home));
                loadBalanceInit();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void loadBalanceInit(){

        MaterialDialog.Builder builder =  new MaterialDialog.Builder(HomeActivity.this)
                .title(getString(R.string.title_loading_dialog))
                .content(getString(R.string.title_calculating_spendable))
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


                int net = prefs.getInt("net",0);

                if(net!=1){
                    txt_snow.setText("TESTSNOW");
                }


                dialog.dismiss();


                refreshDate = new Date();
                String time = TimeAgo.covertTimeToText(refreshDate,HomeActivity.this);

                btn_refresh.setText(time);


                mTimer.scheduleAtFixedRate(new HomeActivity.TimeDisplay(), 0, 10000);




                Intent serviceIntent = new Intent(HomeActivity.this, BalanceService.class);
                startService(serviceIntent);
            }

        });
    }
    private void loadBalance(){

        MaterialDialog.Builder builder =  new MaterialDialog.Builder(HomeActivity.this)
                .title(getString(R.string.title_loading_dialog))
                .content(getString(R.string.title_calculating_spendable))
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

                refreshDate = new Date();
                String time = TimeAgo.covertTimeToText(refreshDate,HomeActivity.this);

                btn_refresh.setText(time);

            }

        });
    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //String str = intent.getStringArrayListExtra("Data").toString();
            double spendable = intent.getDoubleExtra("Data",0.0);
            txt_balance.setText("" + spendable);

            refreshDate = new Date();
            String time = TimeAgo.covertTimeToText(refreshDate,HomeActivity.this);

            btn_refresh.setText(time);


            Log.d("Service says","hello world");
        }
    };

    class TimeDisplay extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    String time = TimeAgo.covertTimeToText(refreshDate,HomeActivity.this);

                    btn_refresh.setText(time);


                }
            });

        }

    }
}

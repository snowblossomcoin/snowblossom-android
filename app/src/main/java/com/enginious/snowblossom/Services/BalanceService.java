package com.enginious.snowblossom.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.enginious.snowblossom.WalletHelper;
import com.enginious.snowblossom.activities.HomeActivity;
import com.enginious.snowblossom.interfaces.WalletBalanceInterface;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by waleed on 11/8/18.
 */

public class BalanceService extends Service {

    public static final int notify = 10000;  //interval between two services(Here Service run every 5 seconds)
    int count = 0;  //number of times service is display
    private Timer mTimer = null;
    private Handler mHandler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new BalanceDisplay(), 0, notify);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);


        new Thread(new Runnable() {
            @Override
            public void run() {



            }
        }).start();
        return START_REDELIVER_INTENT;
    }

    //class TimeDisplay for handling task
    class BalanceDisplay extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    WalletHelper.calculateBalance(new WalletBalanceInterface() {
                        @Override
                        public void balanceRetrieved(long balance) {
                            final double spendable_flakes = (double)balance;
                            final double spendable = spendable_flakes/(double)1000000;
                            WalletHelper.balance = balance;
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction(HomeActivity.mBroadcastBalanceAction);
                            broadcastIntent.putExtra("Data", spendable);
                            sendBroadcast(broadcastIntent);
                        }

                    });

                }
            });

        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();    //For Cancel Timer
    }


}

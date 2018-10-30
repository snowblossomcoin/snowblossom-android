package com.enginious.snowblossom.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.enginious.snowblossom.R;
import com.enginious.snowblossom.WalletHelper;
import com.enginious.snowblossom.interfaces.WalletBalanceInterface;
import com.enginious.snowblossom.interfaces.WalletTransactioninterface;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import snowblossom.client.SnowBlossomClient;
import snowblossom.lib.Globals;

public class SendActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.et_address_send_activity)
    EditText etAddress;

    @BindView(R.id.et_amount_send_activity)
    EditText etAmount;

    @BindView(R.id.qr_img_send_activity)
    ImageView imgSend;

    @BindView(R.id.btn_send_activity)
    Button btn_send;

    @BindView(R.id.txt_balance_send_activity)
    TextView txtBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Send Snow");


        ButterKnife.bind(this);



        imgSend.setOnClickListener(this);
        btn_send.setOnClickListener(this);



        long balance = WalletHelper.balance;
        final double spendable_flakes = (double)balance;
        final double spendable = spendable_flakes/(double)1000000;
        txtBalance.setText(""+spendable);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }



    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("Snowblossom needs permission to open camera in order to read QR codes. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(intentResult != null) {
            if(intentResult.getContents() == null) {
                Log.d("MainActivity", "Cancelled");

            } else {
                String address = intentResult.getContents();
                Log.d("Address",address);
                etAddress.setText(address);
            }
        }

    }
    @Override
    public void onClick(View view) {


        if(view.getId() == R.id.qr_img_send_activity){

            openCamera();

        }else if(view.getId() == R.id.btn_send_activity){

            if(etAmount.getText().toString().isEmpty() || etAddress.getText().toString().isEmpty()){
                return;
            }
            if(!etAmount.getText().toString().matches("[0-9]+")){
                return;

            }
            try {
                SnowBlossomClient client = WalletHelper.getClient();
                double val_snow = Double.parseDouble(etAmount.getText().toString());
                long value = (long) (val_snow * Globals.SNOW_VALUE);
                String to = etAddress.getText().toString();
                sendSnow(client,value,to);

            }catch (Exception e){
                e.printStackTrace();
            }
        }



    }


    private void openCamera(){

        Dexter.withActivity(SendActivity.this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // permission is granted
                        IntentIntegrator integrator = new IntentIntegrator(SendActivity.this);
                        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                        integrator.setCameraId(0);  // Use a specific camera of the device
                        integrator.setBeepEnabled(false);
                        integrator.setBarcodeImageEnabled(true);
                        integrator.initiateScan();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();


    }

    @SuppressLint("StaticFieldLeak")
    private void sendSnow(final SnowBlossomClient client , final long val , final String address ){
        if(client!= null){

            MaterialDialog.Builder builder =  new MaterialDialog.Builder(this)
                    .title("Please Wait")
                    .content("Sending Snow Flakes")
                    .cancelable(false)
                    .titleColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .widgetColor(ContextCompat.getColor(this, R.color.PurpleLight))
                    .contentColor(ContextCompat.getColor(this, R.color.lightGray))
                    .progress(true, 0);

            final MaterialDialog dialog = builder.build();
            dialog.show();

            WalletHelper.sendSnow(val, address, new WalletTransactioninterface() {
                @Override
                public void onTransactioncompleted(Boolean success) {
                    dialog.dismiss();
                    if(success){
                        etAddress.setText("");
                        etAmount.setText("");
                        calculateBalance();
                    }else{

                        MaterialDialog.Builder builder2 = new MaterialDialog.Builder(SendActivity.this)
                                .title("Failed")
                                .content("Could not complete transaction")
                                .titleColor(ContextCompat.getColor(SendActivity.this, R.color.colorPrimary))
                                .widgetColor(ContextCompat.getColor(SendActivity.this, R.color.PurpleLight))
                                .contentColor(ContextCompat.getColor(SendActivity.this, R.color.lightGray))
                                .negativeColor(ContextCompat.getColor(SendActivity.this, R.color.colorPrimary))
                                .negativeText("Cancel");
                        builder2.build().show();
                    }

                }
            });
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void calculateBalance(){
        MaterialDialog.Builder builder =  new MaterialDialog.Builder(this)
                .title("Please Wait")
                .content("Calculating Spendable")
                .cancelable(false)
                .titleColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .widgetColor(ContextCompat.getColor(this, R.color.PurpleLight))
                .contentColor(ContextCompat.getColor(this, R.color.lightGray))
                .progress(true, 0);

        final MaterialDialog dialog = builder.build();
        dialog.show();
        WalletHelper.calculateBalance(new WalletBalanceInterface() {
            @Override
            public void balanceRetrieved(long balance) {
                final double spendable_flakes = (double)balance;
                final double spendable = spendable_flakes/(double)1000000;
                WalletHelper.balance = balance;
                dialog.dismiss();
                txtBalance.setText(""+spendable);
            }
        });
    }
}

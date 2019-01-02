package com.enginious.snowblossom.activities;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.enginious.snowblossom.R;
import com.enginious.snowblossom.WalletHelper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import snowblossom.client.SnowBlossomClient;
import snowblossom.lib.AddressSpecHash;

public class ReceiveActivity extends AppCompatActivity implements View.OnClickListener{

    AddressSpecHash ash =null;
    SnowBlossomClient clt = null;

    String address_global = null;


    @BindView(R.id.qr_img_receive_activity)
    ImageView imgQr;

    @BindView(R.id.txt_address_receive_activity)
    TextView txtAddress;

    @BindView(R.id.btn_generate_receive_activity)
    Button btnGenerate;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("configs",MODE_PRIVATE);

        int net = prefs.getInt("net",0);

        if(net == 2) {

            setTheme(R.style.AppThemeTest);
        }

        setContentView(R.layout.activity_receive);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_receive_snow));

        ButterKnife.bind(this);

        clt = WalletHelper.getClient();


        btnGenerate.setOnClickListener(this);

        generateAddress();



        imgQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyAddress();
            }
        });
        txtAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyAddress();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private void copyAddress() {
        if(address_global!=null){
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("address",address_global);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(this,R.string.title_address_copied,Toast.LENGTH_SHORT).show();
        }
    }

    public void createQR(String address){
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(address, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imgQr.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("StaticFieldLeak")
    public void generateAddress(){

        if(clt != null) {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                    .title(getString(R.string.title_loading_dialog))
                    .content(getString(R.string.title_retrieving_address))
                    .cancelable(false)
                    .titleColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .widgetColor(ContextCompat.getColor(this, R.color.PurpleLight))
                    .contentColor(ContextCompat.getColor(this, R.color.lightGray))
                    .progress(true, 0);

            final MaterialDialog dialog = builder.build();
            dialog.show();

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        ash = clt.getPurse().getUnusedAddress(false, false);
                        final String text = ash.toAddressString(clt.getParams());
                        address_global = text;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                // Stuff that updates the UI
                                txtAddress.setText(text);
                                createQR(text);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    return null;
                }
            }.execute();
        }
    }

    @Override
    public void onClick(View view) {
        try {
            if(clt!= null && ash != null) {
                clt.getPurse().markUsed(ash);
                generateAddress();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

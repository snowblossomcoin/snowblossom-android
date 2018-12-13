package com.enginious.snowblossom.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.enginious.snowblossom.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransactionDetailActivity extends AppCompatActivity {

    @BindView(R.id.qr_img_hash)
    ImageView img_hash;

    String hash = null;

    @BindView(R.id.txt_hash_activity)
    TextView txtHash;

    @BindView(R.id.txt_hash_amount_activity) TextView txtAmount;


    String amount = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);
        ButterKnife.bind(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.activity_hash_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");


        hash = getIntent().getStringExtra("hash");

        amount = getIntent().getStringExtra("amount");


        if(amount != null){
            String amnt = getString(R.string.title_amount);
            txtAmount.setText(amnt+" : "+amount);
        }

        if(hash != null){
            createQR(hash);
            txtHash.setText("TXID:"+hash);

            img_hash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    copryHash();
                }
            });
            txtHash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    copryHash();
                }
            });

        }

    }

    private void copryHash() {
        if(hash!=null){
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("hash",hash);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(this,R.string.title_hash_copied,Toast.LENGTH_SHORT).show();
        }
    }

    public void createQR(String hash){
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(hash, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            img_hash.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }


}

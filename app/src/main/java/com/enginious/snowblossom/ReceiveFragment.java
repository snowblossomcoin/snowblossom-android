package com.enginious.snowblossom;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import snowblossom.client.SnowBlossomClient;
import snowblossom.lib.AddressSpecHash;

public class ReceiveFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match


    private OnFragmentInteractionListener mListener;

    public ReceiveFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }
    @BindView(R.id.qr_img_receive)
    ImageView imgQr;

    @BindView(R.id.txt_address_receive_fragment)
    TextView txtAddress;

    @BindView(R.id.btn_generate_receive_fragment)
    Button btnGenerate;

    AddressSpecHash ash =null;
    SnowBlossomClient clt = null;

    String address_global = null;

    @SuppressLint("StaticFieldLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_receive, container, false);

        ButterKnife.bind(this, v);

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






        return  v ;
    }

    private void copyAddress() {
        if(address_global!=null){
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("address",address_global);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(getContext(),"Address Copied",Toast.LENGTH_SHORT).show();
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
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    @SuppressLint("StaticFieldLeak")
    public void generateAddress(){

        if(clt != null) {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(getContext())
                    .title("Please Wait")
                    .content("Retreiving Address")
                    .cancelable(false)
                    .titleColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                    .widgetColor(ContextCompat.getColor(getContext(), R.color.PurpleLight))
                    .contentColor(ContextCompat.getColor(getContext(), R.color.lightGray))
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
                        getActivity().runOnUiThread(new Runnable() {
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

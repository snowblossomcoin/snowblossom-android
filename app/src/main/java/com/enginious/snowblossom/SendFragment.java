package com.enginious.snowblossom;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
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
import snowblossom.client.TransactionFactory;
import snowblossom.lib.AddressSpecHash;
import snowblossom.lib.AddressUtil;
import snowblossom.lib.Globals;
import snowblossom.lib.TransactionUtil;
import snowblossom.proto.SubmitReply;
import snowblossom.proto.Transaction;
import snowblossom.proto.TransactionOutput;
import snowblossom.util.proto.TransactionFactoryConfig;
import snowblossom.util.proto.TransactionFactoryResult;

public class SendFragment extends Fragment implements View.OnClickListener{

    public SendFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @BindView(R.id.et_address_send_fragment)
    EditText etAddress;

    @BindView(R.id.et_amount_send_fragment)
    EditText etAmount;

    @BindView(R.id.qr_img_send)
    ImageView imgSend;

    @BindView(R.id.btn_send_fragment)
    Button btn_send;

    @BindView(R.id.txt_balance_send_fragment)
    TextView txtBalance;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View v = inflater.inflate(R.layout.fragment_send, container, false);
         ButterKnife.bind(this,v);
         imgSend.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 Dexter.withActivity(getActivity())
                         .withPermission(Manifest.permission.CAMERA)
                         .withListener(new PermissionListener() {
                             @Override
                             public void onPermissionGranted(PermissionGrantedResponse response) {
                                 // permission is granted
                                 IntentIntegrator integrator = IntentIntegrator.forSupportFragment(SendFragment.this);
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
         });

         btn_send.setOnClickListener(this);



        long balance = WalletHelper.balance;
        final double spendable_flakes = (double)balance;
        final double spendable = spendable_flakes/(double)1000000;
        txtBalance.setText(""+spendable);


        return v;
    }
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
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
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onClick(View view) {

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

    @SuppressLint("StaticFieldLeak")
    private void sendSnow(final SnowBlossomClient client , final long val , final String address ){
        if(client!= null){

            MaterialDialog.Builder builder =  new MaterialDialog.Builder(getContext())
                    .title("Please Wait")
                    .content("Sending Snow Flakes")
                    .cancelable(false)
                    .titleColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                    .widgetColor(ContextCompat.getColor(getContext(), R.color.PurpleLight))
                    .contentColor(ContextCompat.getColor(getContext(), R.color.lightGray))
                    .progress(true, 0);

            final MaterialDialog dialog = builder.build();
            dialog.show();







            new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        try {
                            TransactionFactoryConfig.Builder tx_config = TransactionFactoryConfig.newBuilder();
                            tx_config.setSign(true);
                            AddressSpecHash to_hash = AddressUtil.getHashForAddress(client.getParams().getAddressPrefix(),address);
                            tx_config.addOutputs(TransactionOutput.newBuilder().setRecipientSpecHash(to_hash.getBytes()).setValue(val).build());
                            tx_config.setChangeFreshAddress(true);
                            tx_config.setInputConfirmedThenPending(true);
                            tx_config.setFeeUseEstimate(false);

                            TransactionFactoryResult res = TransactionFactory.createTransaction(tx_config.build(), client.getPurse().getDB(), client);
                            Transaction tx = res.getTx();
                            TransactionUtil.prettyDisplayTx(tx, System.out, client.getParams());
                            //                ManagedChannel channel = ManagedChannelBuilder.forAddress("node.snowblossom.cluelessperson.com", 2339).usePlaintext(true).build();
                            //                UserServiceGrpc.UserServiceBlockingStub blockingStub;
                            //                blockingStub = UserServiceGrpc.newBlockingStub(channel);
                            SubmitReply str = client.getStub().submitTransaction(tx);
                            if(str != null) {
                                return str.getSuccess();
                            }else{
                                return false;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            return  false;
                        }
                    }

                    @Override
                    protected void onPostExecute(Boolean s) {
                        super.onPostExecute(s);

                        dialog.dismiss();
                        if(s){
                            etAddress.setText("");
                            etAmount.setText("");
                            calculateBalance();
                        }else{

                            MaterialDialog.Builder builder2 = new MaterialDialog.Builder(getContext())
                                    .title("Failed")
                                    .content("Could not complete transaction")
                                    .titleColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                                    .widgetColor(ContextCompat.getColor(getContext(), R.color.PurpleLight))
                                    .contentColor(ContextCompat.getColor(getContext(), R.color.lightGray))
                                    .negativeColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                                    .negativeText("Cancel");
                            builder2.build().show();
                        }
                    }
                }.execute();

        }
    }

    @SuppressLint("StaticFieldLeak")
    public void calculateBalance(){

        final SnowBlossomClient client =WalletHelper.getClient();

        MaterialDialog.Builder builder =  new MaterialDialog.Builder(getContext())
                .title("Please Wait")
                .content("Calculating Spendable")
                .cancelable(false)
                .titleColor(ContextCompat.getColor(getContext(), R.color.colorPrimary))
                .widgetColor(ContextCompat.getColor(getContext(), R.color.PurpleLight))
                .contentColor(ContextCompat.getColor(getContext(), R.color.lightGray))
                .progress(true, 0);



        final MaterialDialog dialog = builder.build();
        dialog.show();

        new AsyncTask<Void,Void,Long>() {
            @Override
            protected Long doInBackground(Void... voids) {
                if(client != null) {
                    long balance = client.getBalance().getSpendable();
                    return balance;

                }else{
                    return 0l;

                }
            }

            @Override
            protected void onPostExecute(Long abalance) {
                super.onPostExecute(abalance);
                long balance = abalance.longValue();
                final double spendable_flakes = (double)balance;
                final double spendable = spendable_flakes/(double)1000000;
                WalletHelper.balance = balance;
                dialog.dismiss();
                txtBalance.setText(""+spendable);
            }
        }.execute();



    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

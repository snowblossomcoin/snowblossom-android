package com.enginious.snowblossom;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.enginious.snowblossom.activities.SendActivity;
import com.enginious.snowblossom.interfaces.WalletBalanceInterface;
import com.enginious.snowblossom.interfaces.WalletTransactioninterface;
import com.google.protobuf.ByteString;

import java.util.TreeMap;

import duckutil.ConfigMem;
import snowblossom.client.SnowBlossomClient;
import snowblossom.client.TransactionFactory;
import snowblossom.client.WalletUtil;
import snowblossom.lib.AddressSpecHash;
import snowblossom.lib.AddressUtil;
import snowblossom.lib.ChainHash;
import snowblossom.lib.TransactionUtil;
import snowblossom.proto.SubmitReply;
import snowblossom.proto.Transaction;
import snowblossom.proto.TransactionOutput;
import snowblossom.util.proto.TransactionFactoryConfig;
import snowblossom.util.proto.TransactionFactoryResult;

/**
 * Created by waleed on 10/19/18.
 */

public class WalletHelper {

    private static SnowBlossomClient client = null;

    public static long balance = 0;

    public static SnowBlossomClient InitClient(TreeMap<String, String> configs) throws Exception {
        client = new SnowBlossomClient(new ConfigMem(configs));

        return client;

    }
    public static SnowBlossomClient InitSeedClient(TreeMap<String, String> configs,String seed) throws Exception {
        client = new SnowBlossomClient(new ConfigMem(configs),seed);


        return client;

    }

    public static SnowBlossomClient getClient(){
        return client;
    }

    //calculates balance
    @SuppressLint("StaticFieldLeak")
    public static void calculateBalance(final WalletBalanceInterface balanceInterface){

        new AsyncTask<Void,Void,Long>() {
            @Override
            protected Long doInBackground(Void... voids) {
                if(client != null) {
                    try {
                        long balance = client.getBalance().getSpendable();
                        return balance;
                    }catch (Exception e){
                        e.printStackTrace();
                        return 0l;
                    }

                }else{
                    return 0l;

                }
            }

            @Override
            protected void onPostExecute(Long abalance) {
                super.onPostExecute(abalance);
                long balance = abalance.longValue();
                WalletHelper.balance = balance;
                balanceInterface.balanceRetrieved(balance);

            }
        }.execute();
    }
    // sending snow to addresses
    @SuppressLint("StaticFieldLeak")
    public static void sendSnow(final long value , final String address , final WalletTransactioninterface transactioninterface){
        if(client!= null){
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... voids) {
                    try {
                        TransactionFactoryConfig.Builder tx_config = TransactionFactoryConfig.newBuilder();
                        tx_config.setSign(true);
                        AddressSpecHash to_hash = AddressUtil.getHashForAddress(client.getParams().getAddressPrefix(),address);
                        tx_config.addOutputs(TransactionOutput.newBuilder().setRecipientSpecHash(to_hash.getBytes()).setValue(value).build());
                        tx_config.setChangeFreshAddress(true);
                        tx_config.setInputConfirmedThenPending(true);
                        tx_config.setFeeUseEstimate(true);

                        TransactionFactoryResult res = TransactionFactory.createTransaction(tx_config.build(), client.getPurse().getDB(), client);
                        Transaction tx = res.getTx();
                        TransactionUtil.prettyDisplayTx(tx, System.out, client.getParams());
                        //                ManagedChannel channel = ManagedChannelBuilder.forAddress("node.snowblossom.cluelessperson.com", 2339).usePlaintext(true).build();
                        //                UserServiceGrpc.UserServiceBlockingStub blockingStub;
                        //                blockingStub = UserServiceGrpc.newBlockingStub(channel);

                        SubmitReply str = client.getStub().submitTransaction(tx);

                        if(str != null) {
                            ChainHash tx_hash = new ChainHash(tx.getTxHash());
                            String hash_str = "" + tx_hash.toString();

                            return hash_str;

                        }else{

                            return null;

                        }
                    }catch (Exception e){
                        e.printStackTrace();

                        return  null;
                    }
                }

                @Override
                protected void onPostExecute(String hash) {
                    super.onPostExecute(hash);
                    if(hash != null){
                        transactioninterface.onTransactioncompleted(true,hash);
                    }else{
                        transactioninterface.onTransactioncompleted(false,"");
                    }

                }
            }.execute();

        }
    }


}

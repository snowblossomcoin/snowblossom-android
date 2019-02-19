package com.enginious.snowblossom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.enginious.snowblossom.adapters.TransactionsAdapter;
import com.enginious.snowblossom.models.Transaction;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import snowblossom.client.SnowBlossomClient;
import snowblossom.lib.TransactionBridge;


public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @BindView(R.id.txt_balance_fragment_home)
    TextView txtBalance;

    @BindView(R.id.recycler_fragment_home)
    RecyclerView recyclerView;

    @SuppressLint("StaticFieldLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, v);

        txtBalance.setText("....");

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

        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if(client != null) {
                    try
                    {
                    long balance = client.getBalance().getSpendable();
                    final double spendable_flakes = (double)balance;
                    final double spendable = spendable_flakes/(double)1000000;
                    WalletHelper.balance = balance;
                    HomeFragment.this.getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // Stuff that updates the UI
                            txtBalance.setText("" + spendable);
                            dialog.dismiss();
                        }
                    });
                    }
                    catch(Exception e)
                    {
                      e.printStackTrace();
                    }

                }else{
                    HomeFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    });

                }
                return null;
            }


        }.execute();

        txtBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),TransactionsActivity.class);
                startActivity(intent);
            }
        });

        List<Transaction> transactionsList = new ArrayList<>();
        TransactionsAdapter mAdapter;

        mAdapter = new TransactionsAdapter(transactionsList,getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        transactionsList.add(new Transaction());
        transactionsList.add(new Transaction());
        transactionsList.add(new Transaction());
        transactionsList.add(new Transaction());
        transactionsList.add(new Transaction());
        transactionsList.add(new Transaction());

        mAdapter.notifyDataSetChanged();


        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

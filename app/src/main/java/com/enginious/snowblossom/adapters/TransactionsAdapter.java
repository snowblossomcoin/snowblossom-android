package com.enginious.snowblossom.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.enginious.snowblossom.R;
import com.enginious.snowblossom.models.Transaction;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by waleed on 10/23/18.
 */

public class TransactionsAdapter extends  RecyclerView.Adapter<TransactionsAdapter.MyViewHolder> {

    private List<Transaction> transactionList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //public TextView title, year, genre;

        @BindView(R.id.img_transaction_row)
        ImageView imageView;
        @BindView(R.id.txt_address_transaction_rows)
        TextView txtAddress;
        @BindView(R.id.txt_amount_transaction_rows)
        TextView txtAmount;
        @BindView(R.id.txt_transaction_type_transaction_rows)
        TextView txtType;
        @BindView(R.id.txt_confirmation_transaction_rows)
        TextView txtConfirmation;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(context,view);

//            title = (TextView) view.findViewById(R.id.title);
//            genre = (TextView) view.findViewById(R.id.genre);
//            year = (TextView) view.findViewById(R.id.year);
        }
    }


    public TransactionsAdapter(List<Transaction> transactionList, Context context) {
        this.transactionList = transactionList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_row_layout, parent, false);



        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
//        Movie movie = moviesList.get(position);
//        holder.title.setText(movie.getTitle());
//        holder.genre.setText(movie.getGenre());
//        holder.year.setText(movie.getYear());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }
}
package com.enginious.snowblossom.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enginious.snowblossom.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by waleed on 10/24/18.
 */

public class LanguagesAdapter extends  RecyclerView.Adapter<LanguagesAdapter.MyViewHolder> {

    private List<String> languageList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //public TextView title, year, genre;

        @BindView(R.id.txt_language_language_row)
        TextView txtLanguage;



        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(context,view);

            txtLanguage = (TextView)view.findViewById(R.id.txt_language_language_row);

//            title = (TextView) view.findViewById(R.id.title);
//            genre = (TextView) view.findViewById(R.id.genre);
//            year = (TextView) view.findViewById(R.id.year);
        }
    }


    public LanguagesAdapter(List<String> langs, Context context) {
        this.languageList = langs;
        this.context = context;
    }

    @Override
    public LanguagesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.language_row_layout, parent, false);
        return new LanguagesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LanguagesAdapter.MyViewHolder holder,final int position) {
//        Movie movie = moviesList.get(position);
//        holder.title.setText(movie.getTitle());
//        holder.genre.setText(movie.getGenre());
//        holder.year.setText(movie.getYear());

        holder.txtLanguage.setText(languageList.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.setOnItemClickListener(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return languageList.size();
    }

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void setOnItemClickListener(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}

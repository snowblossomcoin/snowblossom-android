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
    public void onBindViewHolder(LanguagesAdapter.MyViewHolder holder, int position) {
//        Movie movie = moviesList.get(position);
//        holder.title.setText(movie.getTitle());
//        holder.genre.setText(movie.getGenre());
//        holder.year.setText(movie.getYear());
    }

    @Override
    public int getItemCount() {
        return languageList.size();
    }
}

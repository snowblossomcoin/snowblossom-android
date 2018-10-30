package com.enginious.snowblossom.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enginious.snowblossom.R;
import com.enginious.snowblossom.models.Server;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by waleed on 10/24/18.
 */

public class ServersAdapter extends  RecyclerView.Adapter<ServersAdapter.MyViewHolder> {

    private List<Server> serverList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //public TextView title, year, genre;

        TextView txtServer;
        TextView txtConnected;


        public MyViewHolder(View view) {
            super(view);

            txtConnected = (TextView)view.findViewById(R.id.txt_connected_server_row);
            txtServer = (TextView)view.findViewById(R.id.txt_server_server_row);
        }
    }


    public ServersAdapter(List<Server> servers, Context context) {
        this.serverList = servers;
        this.context = context;
    }

    @Override
    public ServersAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.server_row_layout, parent, false);



        return new ServersAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ServersAdapter.MyViewHolder holder, int position) {
//        Movie movie = moviesList.get(position);
//        holder.title.setText(movie.getTitle());
//        holder.genre.setText(movie.getGenre());
//        holder.year.setText(movie.getYear());
            Server server = serverList.get(position);
            holder.txtServer.setText(server.getUrl());
            if(server.getConnected()){
                holder.txtConnected.setText("Connected");
            }else{
                holder.txtConnected.setText("");
            }

    }

    @Override
    public int getItemCount() {
        return serverList.size();
    }
}

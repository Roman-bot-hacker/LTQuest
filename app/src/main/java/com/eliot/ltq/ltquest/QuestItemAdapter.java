package com.eliot.ltq.ltquest;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class QuestItemAdapter extends RecyclerView.Adapter<QuestItemAdapter.MyViewHolder> {

    private List<Quest> questList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name,distance;
        public ImageView image;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.quest_name);
            distance = (TextView) view.findViewById(R.id.quest_distance);
            image = (ImageView) view.findViewById(R.id.quest_image);

        }
    }


    public QuestItemAdapter(List<Quest> moviesList) {
        this.questList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quest_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Quest quest = questList.get(position);
        holder.name.setText(quest.getName());
        holder.distance.setText(quest.getDistance()+" km");
    }

    @Override
    public int getItemCount() {
        return questList.size();
    }


}
package com.eliot.ltq.ltquest;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class QuestItemAdapter extends RecyclerView.Adapter<QuestItemAdapter.MyViewHolder> {

    private List<QuestStructure> questList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView name,distance;
        public ImageView image;
        public ItemClickListener itemClickListener;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.quest_name);
            distance = (TextView) view.findViewById(R.id.quest_distance);
            image = (ImageView) view.findViewById(R.id.quest_image);
            view.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemOnClickListener){
            this.itemClickListener = itemOnClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition());
        }
    }


    public QuestItemAdapter(List<QuestStructure> moviesList) {
        this.questList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quest_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final QuestStructure quest = questList.get(position);
        holder.name.setText(quest.getQuestName());
        holder.distance.setText((quest.getDistance())+" km");
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                intent.putExtra("Position", "" + position);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return questList.size();
    }


}
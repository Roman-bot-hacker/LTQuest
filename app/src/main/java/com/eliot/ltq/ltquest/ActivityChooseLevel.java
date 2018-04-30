package com.eliot.ltq.ltquest;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class ActivityChooseLevel extends AppCompatActivity {
    FirebaseDataManager manager = new FirebaseDataManager();
    private RecyclerView easyRecyclerView;
    private List<Quest> easyQuests = new ArrayList<>();
    private QuestItemAdapter easyQuestItemAdapter;

    private RecyclerView recyclerView;
    private List<Quest> quests = new ArrayList<>();
    private QuestItemAdapter questItemAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_level);
        prepareQuestData();
        startNewButton();
    }


    private void prepareQuestData() {
        manager.questsRetriever(new FirebaseDataManager.DataRetrieveListener() {
            @Override
            public void onSuccess() {
                List<QuestStructure> questsList = new ArrayList<>(manager.getQuestStructureList());
                for (QuestStructure questStructure : questsList) {
                    if (questStructure.getLevel() == 1) {
                        Quest quest = new Quest(R.drawable.lviv1, questStructure.getQuestName(), questStructure.getDistance());
                        quests.add(quest);
                    } else if (questStructure.getLevel() == 2) {
                        Quest easyQuest = new Quest(R.drawable.lviv1, questStructure.getQuestName(), questStructure.getDistance());
                        easyQuests.add(easyQuest);
                    }
                }
                recyclerView = findViewById(R.id.first_recycler_view);
                questItemAdapter = new QuestItemAdapter(quests);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(questItemAdapter);
                easyRecyclerView = findViewById(R.id.second_recycler_view);
                easyQuestItemAdapter = new QuestItemAdapter(easyQuests);
                RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                easyRecyclerView.setLayoutManager(mLayoutManager1);
                easyRecyclerView.setItemAnimator(new DefaultItemAnimator());
                easyRecyclerView.setAdapter(easyQuestItemAdapter);
                easyRecyclerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }
        });
    }

    private void startNewButton() {
        /*ImageView CenterCafes = (ImageView) findViewById(R.id.my_recycler_view1);
        TextView CenterCafes0 = (TextView) findViewById(R.id.textView2);*/
       /* CenterCafes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                *//*Intent intent  = new Intent();
                intent.putExtra("Name","NAmeName");
                setResult(RESULT_OK,intent);
                finish();*//*
                startActivity(new Intent(ActivityChooseLevel.this, QuestScreen.class));
            }
        });
        CenterCafes0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityChooseLevel.this, QuestScreen.class));
            }
        });*/
    }
}

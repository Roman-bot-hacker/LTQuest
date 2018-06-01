package com.eliot.ltq.ltquest;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ActivityChooseLevel extends AppCompatActivity{
    FirebaseDataManager manager = new FirebaseDataManager();
    private RecyclerView easyRecyclerView;
    private List<QuestStructure> easyQuests = new ArrayList<>();
    private QuestItemAdapter easyQuestItemAdapter;

    private RecyclerView recyclerView;
    private List<QuestStructure> quests = new ArrayList<>();
    private QuestItemAdapter questItemAdapter;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_level);
        toolbar = findViewById(R.id.toolbar);
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
        prepareQuestData();
        startNewButton();
        configureToolbar();
    }


    private void prepareQuestData() {
        final String categoryToShow = getIntent().getStringExtra("Category");
        manager.questsRetriever(new FirebaseDataManager.DataRetrieveListenerForQuestStructure() {
            @Override
            public void onSuccess(List<QuestStructure> questStructureList) {
                questStructureList = sortQuestsForRecycler(questStructureList);
                switch (categoryToShow) {
                    case "0":
                        List<QuestStructure> questsInCategory0 = new ArrayList<>(manager.findQuestsByCategory(questStructureList, 0));
                        for (QuestStructure questStructure : questsInCategory0) {
                            if (questStructure.getLevel() == 1) {
                                quests.add(questStructure);
                                questItemAdapter.notifyDataSetChanged();
                            } else if (questStructure.getLevel() == 2) {
                                easyQuests.add(questStructure);
                                easyQuestItemAdapter.notifyDataSetChanged();
                            }
                        }
                        break;

                    case "1":
                        List<QuestStructure> questsInCategory1 = new ArrayList<>(manager.findQuestsByCategory(questStructureList, 1));
                        for (QuestStructure questStructure : questsInCategory1) {
                            if (questStructure.getLevel() == 1) {
                                quests.add(questStructure);
                                questItemAdapter.notifyDataSetChanged();
                            } else if (questStructure.getLevel() == 2) {
                                easyQuests.add(questStructure);
                                easyQuestItemAdapter.notifyDataSetChanged();
                            }
                        }
                        break;

                    case "2":
                        List<QuestStructure> questsInCategory2 = new ArrayList<>(manager.findQuestsByCategory(questStructureList, 2));
                        for (QuestStructure questStructure : questsInCategory2) {
                            if (questStructure.getLevel() == 1) {
                                quests.add(questStructure);
                                questItemAdapter.notifyDataSetChanged();
                            } else if (questStructure.getLevel() == 2) {
                                easyQuests.add(questStructure);
                                easyQuestItemAdapter.notifyDataSetChanged();
                            }
                        }
                        break;

                    case "all":
                        for (QuestStructure questStructure : questStructureList) {
                            if (questStructure.getLevel() == 1) {
                                quests.add(questStructure);
                                questItemAdapter.notifyDataSetChanged();
                            } else if (questStructure.getLevel() == 2) {
                                easyQuests.add(questStructure);
                                easyQuestItemAdapter.notifyDataSetChanged();
                            }
                        }
                        break;
                }
            }

            @Override
            public void onError(DatabaseError databaseError) {
                Log.e("Error","Can not retrieve QuestStructure");
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

    private void configureToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorText));
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Choose level");
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case android.R.id.home:
                Intent intent = new Intent(ActivityChooseLevel.this, MainActivity.class);
                setResult(RESULT_OK);
                finish();
                return true;
        }

        return true;
    }

    public List<QuestStructure> sortQuestsForRecycler(List<QuestStructure> quests){
        List<QuestStructure> sortedQuests = new ArrayList<>();
        Collections.sort(quests, new Comparator<QuestStructure>() {
            @Override
            public int compare(QuestStructure o1, QuestStructure o2) {
                String name1 = o1.getQuestName();
                String name2 = o2.getQuestName();
                return name1.compareTo(name2);
            }
        });
        Collections.sort(quests, new Comparator<QuestStructure>() {
            @Override
            public int compare(QuestStructure o1, QuestStructure o2) {
                int category1 = o1.getParentCategoryID();
                int category2 = o2.getParentCategoryID();
                return category1 - category2;
            }
        });

        return quests;
    }

    @Override
    public void onBackPressed() {
        // do nothing.
    }

}

package com.eliot.ltq.ltquest;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;


public class ActivityChooseLevel extends AppCompatActivity{
    FirebaseDataManager manager = new FirebaseDataManager();
    private RecyclerView easyRecyclerView;
    private List<Quest> easyQuests = new ArrayList<>();
    private QuestItemAdapter easyQuestItemAdapter;

    private RecyclerView recyclerView;
    private List<Quest> quests = new ArrayList<>();
    private QuestItemAdapter questItemAdapter;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_level);
        toolbar = findViewById(R.id.toolbar);
        prepareQuestData();
        startNewButton();
        configureToolbar();
    }


    private void prepareQuestData() {
        manager.questsRetriever(new FirebaseDataManager.DataRetrieveListenerForQuestStructure() {
            @Override
            public void onSuccess(List<QuestStructure> questStructureList) {
                recyclerView = findViewById(R.id.first_recycler_view);
                questItemAdapter = new QuestItemAdapter(questStructureList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(questItemAdapter);
                easyRecyclerView = findViewById(R.id.second_recycler_view);
                easyQuestItemAdapter = new QuestItemAdapter(questStructureList);
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
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
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
                startActivity(new Intent(ActivityChooseLevel.this, MainActivity.class));
                return true;
        }

        return true;
    }

}

package com.eliot.ltq.ltquest;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDataManager {
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private List<QuestCategory> questCategoryList = new ArrayList<>();
    private List<>;

    public interface DataRetrieveListener{
        void onSuccess();
    }

    public void getCategoriesNamesList(final DataRetrieveListener listener){
        firebaseDatabase.getReference("categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:
                     dataSnapshot.getChildren()) {
                    questCategoryList.add(dataSnapshot1.getValue(QuestCategory.class));
                }
                listener.onSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getQuests(){
        firebaseDatabase.getReference("quests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSpanshot1:
                     dataSnapshot.getChildren()) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public List<QuestCategory> getQuestCategoryList() {
        return questCategoryList;
    }
}

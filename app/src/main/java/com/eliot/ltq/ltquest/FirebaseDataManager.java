package com.eliot.ltq.ltquest;

import android.util.Log;

import com.eliot.ltq.ltquest.authentication.UserInformation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDataManager {
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private List<QuestCategory> questCategoryList = new ArrayList<>();
    private List<QuestStructure> questStructureList = new ArrayList<>();
    private List<LocationStructure> locationsList = new ArrayList<>();

    public interface DataRetrieveListener{
        void onSuccess();
    }

    public void categoriesNamesListRetriever(final DataRetrieveListener listener){
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

    public void questsRetriever(final DataRetrieveListener listener){
        firebaseDatabase.getReference("quests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSpanshot1:
                     dataSnapshot.getChildren()) {
                    questStructureList.add(dataSpanshot1.getValue(QuestStructure.class));
                    listener.onSuccess();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public List<QuestStructure> findQuestsByLevel(List<QuestStructure> fullList, int level){
        List<QuestStructure> foundQuests = new ArrayList<>();
        for (QuestStructure questStructure:
             fullList) {
            if (questStructure.getLevel() == level){
                foundQuests.add(questStructure);
            }
            else {
                Log.d(" Unfited Quest:", questStructure.getQuestName());
            }
        }
        return foundQuests;
    }

    public void locationsListRetriever(final DataRetrieveListener listener){
        firebaseDatabase.getReference("locations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:
                     dataSnapshot.getChildren()) {
                    locationsList.add(dataSnapshot1.getValue(LocationStructure.class));
                }
                listener.onSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void findLocationsById(){
    }

    public void addUserDataToFirabase(UserInformation userInformation){
        firebaseDatabase.getReference().child("userData").child("Tania").setValue(userInformation);
    }

    public List<QuestCategory> getQuestCategoryList() {
        return questCategoryList;
    }

    public List<QuestStructure> getQuestStructureList() {
        return questStructureList;
    }
}

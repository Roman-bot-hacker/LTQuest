package com.eliot.ltq.ltquest;

import android.util.Log;
import android.widget.Toast;

import com.eliot.ltq.ltquest.authentication.AuthActivity;
import com.eliot.ltq.ltquest.authentication.FirebaseAuthManager;
import com.eliot.ltq.ltquest.authentication.UserInformation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FirebaseDataManager {
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    public interface DataRetrieveListener{
        void onSuccess();
    }

    public interface DataRetrieveListenerForQuestCategory{
        void onSuccess(List<QuestCategory> questCategoryList);
        void onError(DatabaseError databaseError);
    }

    public interface DataRetrieveListenerForQuestStructure{
        void onSuccess(List<QuestStructure> questStructureList);
        void onError(DatabaseError databaseError);
    }

    public interface DataRetrieveListenerForLocationsStructure{
        void onSuccess(List<LocationStructure> locationStructureList);
        void onError(DatabaseError databaseError);
    }

    public interface DataRetrieveListenerForUserInformation{
        void onSuccess(UserInformation userInformation);
        void onError(DatabaseError databaseError);
    }

    public interface UserInformationWritingListener{
        void onSuccess();
        void onError();
    }

    public void categoriesNamesListRetriever(final DataRetrieveListenerForQuestCategory listener){
        firebaseDatabase.getReference("categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<QuestCategory> questCategoryList = new ArrayList<>();
                for (DataSnapshot dataSnapshot1:
                     dataSnapshot.getChildren()) {
                    questCategoryList.add(dataSnapshot1.getValue(QuestCategory.class));
                }
                listener.onSuccess(questCategoryList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError(databaseError);
            }
        });
    }

    public void questsRetriever(final DataRetrieveListenerForQuestStructure listener){
        firebaseDatabase.getReference("quest").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {List<QuestStructure> questStructureList = new ArrayList<>();
                for (DataSnapshot dataSpanshot1:
                     dataSnapshot.getChildren()) {
                    questStructureList.add(dataSpanshot1.getValue(QuestStructure.class));
                }
                listener.onSuccess(questStructureList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError(databaseError);
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

    public void locationsListRetriever(final DataRetrieveListenerForLocationsStructure listener){
        firebaseDatabase.getReference("locations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<LocationStructure> locationsList = new ArrayList<>();
                for (DataSnapshot dataSnapshot1:
                     dataSnapshot.getChildren()) {
                    locationsList.add(dataSnapshot1.getValue(LocationStructure.class));
                }
                listener.onSuccess(locationsList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError(databaseError);
            }
        });
    }

    public void findLocationsById(){
    }

    public void writeCurrentUserData(String uId, final UserInformation userInformation, final UserInformationWritingListener listener){
            firebaseDatabase.getReference().child("userData").child(uId).setValue(userInformation);
            checkIfUserInformationIsWritten(uId, new UserInformationWritingListener() {
                @Override
                public void onSuccess() {
                    listener.onSuccess();
                }

                @Override
                public void onError() {
                    listener.onError();
                }
            });
    }

    public void checkIfUserInformationIsWritten(String uId, final UserInformationWritingListener listener) {
        firebaseDatabase.getReference().child("userData").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInformation userInformation;
                userInformation = dataSnapshot.getValue(UserInformation.class);
                try {
                    String infIsAvailable = userInformation.getName();
                    listener.onSuccess();
                }
                catch (NullPointerException e) {
                    listener.onError();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("UserInf problem: ",databaseError.getMessage());
            }
        });
    }

    public void getCurrentUserData(String uId, final DataRetrieveListenerForUserInformation listener){
        firebaseDatabase.getReference().child("userData").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInformation userInformation;
                userInformation = dataSnapshot.getValue(UserInformation.class);
                listener.onSuccess(userInformation);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError(databaseError);
            }
        });
    }
}

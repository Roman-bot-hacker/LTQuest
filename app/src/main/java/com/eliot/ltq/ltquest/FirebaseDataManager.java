package com.eliot.ltq.ltquest;

import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.eliot.ltq.ltquest.authentication.AuthActivity;
import com.eliot.ltq.ltquest.authentication.FirebaseAuthManager;
import com.eliot.ltq.ltquest.authentication.UserInformation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.*;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
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

    public interface QuestPhotosResult {
        void onSuccess(List<String> list);
        void onError(String excepMassage);
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

    public List<QuestStructure> findQuestsByCategory(List<QuestStructure> fullList, int categoryId){
        List<QuestStructure> foundQuests = new ArrayList<>();
        for (QuestStructure questStructure:
                fullList) {
            if (questStructure.getParentCategoryID() == categoryId){
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
                try{
                    //trying if user information is written on database
                    if(userInformation.getName()==null){
                        listener.onError();
                    }
                    else {
                        listener.onSuccess();
                    }
                }
                catch (NullPointerException e){
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

    public void writeUserPoints(String uId, final int points){
        firebaseDatabase.getReference().child("userData").child(uId).child("points").setValue(points);
    }

    public void getQuestPhotos(String questName, final QuestPhotosResult listener){
        firebaseDatabase.getReference().child("photos").child(questName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> pathList = new LinkedList<>();
                GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                pathList = dataSnapshot.getValue(t);
                listener.onSuccess(pathList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError(databaseError.getMessage());
            }
        });
    }
}

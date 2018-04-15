package com.eliot.ltq.ltquest.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.eliot.ltq.ltquest.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    FirebaseUser user;
    UserInformation userInformation = new UserInformation();

    TextView textViewUserEmail;
    TextView textViewName;
    TextView logOut;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(ProfileActivity.this, RegistrationActivity.class));
        }

        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userInformationRequest();
        textViewUserEmail = (TextView) findViewById(R.id.email);
        textViewUserEmail.setText(user.getEmail());
        textViewName = (TextView) findViewById(R.id.name_user);
        textViewName.setText(userInformation.getName());
        logOut = (TextView) findViewById(R.id.logout);
        logOut.setOnClickListener(this);
    }

    public void userInformationRequest(){
        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userInformation = (UserInformation) dataSnapshot.child("users").child(user.getUid()).getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view == logOut){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
        }
    }
}

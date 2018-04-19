package com.eliot.ltq.ltquest.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eliot.ltq.ltquest.MainActivity;
import com.eliot.ltq.ltquest.R;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    private static AuthType authType = AuthType.LOGIN;
    private FirebaseAuthManager manager = new FirebaseAuthManager(this);
    private Button buttonLogIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextName;
    private TextView textViewChangeType;
    private String name;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        buttonLogIn = (Button) findViewById(R.id.buttonLogIn);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextName = (EditText) findViewById(R.id.editTextName);
        textViewChangeType = (TextView) findViewById(R.id.textChangeType);
        chooseAuth();
    }

    public static void setAuthType(AuthType authType){
        AuthActivity.authType = authType;
    }

    public void chooseAuth(){
        switch (authType) {
            case REGISTRATION: {
                setRegistrationVisibility();
                email = editTextEmail.getText().toString().trim();
                password = editTextPassword.getText().toString().trim();
                name = editTextName.getText().toString().trim();
                isFieldEmpty(name, email, password);
                buttonLogIn.setOnClickListener(this);
                textViewChangeType.setOnClickListener(this);
            } break;
            case LOGIN: {
                setLoginVisibility();
                email = editTextEmail.getText().toString().trim();
                password = editTextPassword.getText().toString().trim();
                isFieldEmpty(name, email, password);
                buttonLogIn.setOnClickListener(this);
                textViewChangeType.setOnClickListener(this);
            } break;
        }
    }

    public void isFieldEmpty(String name, String email, String password) {
        if ((TextUtils.isEmpty(name))&&(authType==AuthType.REGISTRATION)) {
            Toast.makeText(this, "Please, enter name", Toast.LENGTH_LONG).show();
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please, enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please, enter password", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void setRegistrationVisibility(){
        buttonLogIn.setText("Registration");
        buttonLogIn.setVisibility(View.VISIBLE);
        editTextEmail.setVisibility(View.VISIBLE);
        editTextPassword.setVisibility(View.VISIBLE);
        editTextName.setVisibility(View.VISIBLE);
        textViewChangeType.setText(R.string.to_sing_in);
        textViewChangeType.setVisibility(View.VISIBLE);
    }

    public void setLoginVisibility(){
        buttonLogIn.setText("Login");
        buttonLogIn.setVisibility(View.VISIBLE);
        editTextEmail.setVisibility(View.VISIBLE);
        editTextPassword.setVisibility(View.VISIBLE);
        editTextName.setVisibility(View.GONE);
        textViewChangeType.setText(R.string.to_sing_up);
        textViewChangeType.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick (View view){
        if((view==buttonLogIn)&&(authType==AuthType.REGISTRATION)){
            manager.registerUser(this, email, password, new FirebaseAuthManager.UserLoginListener() {
                @Override
                public void onSuccess() {
                    //Here must be a method to create a new user in Firebase
                    finish();
                    startActivity(new Intent(AuthActivity.this, ProfileActivity.class));
                }

                @Override
                public void onError() {
                    Toast.makeText(AuthActivity.this, "Could not register!", Toast.LENGTH_LONG).show();
                }
            });
        }

        if((view==buttonLogIn)&&(authType==AuthType.LOGIN)){
            manager.loginUser(this, email, password, new FirebaseAuthManager.UserLoginListener() {
                @Override
                public void onSuccess() {
                    finish();
                    startActivity(new Intent(AuthActivity.this, MainActivity.class));
                }

                @Override
                public void onError() {
                    Toast.makeText(AuthActivity.this, "Could not login!", Toast.LENGTH_LONG).show();
                }
            });
        }

        if((view==textViewChangeType)&&(authType==AuthType.REGISTRATION)){
            authType = AuthType.LOGIN;
            chooseAuth();
        }

        if((view==textViewChangeType)&&(authType==AuthType.LOGIN)){
            authType = AuthType.REGISTRATION;
            chooseAuth();
        }
    }
}

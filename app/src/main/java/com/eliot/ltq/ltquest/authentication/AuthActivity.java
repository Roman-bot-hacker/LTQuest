package com.eliot.ltq.ltquest.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eliot.ltq.ltquest.MainActivity;
import com.eliot.ltq.ltquest.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    private static AuthType authType = AuthType.LOGIN;
    private FirebaseAuthManager manager;
    private Button buttonLogIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextName;
    private TextView textViewChangeType;
    private ImageView buttonGoogleSingIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        manager = new FirebaseAuthManager(this);

        buttonLogIn = (Button) findViewById(R.id.buttonLogIn);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextName = (EditText) findViewById(R.id.editTextName);
        textViewChangeType = (TextView) findViewById(R.id.textChangeType);
        buttonGoogleSingIn = (ImageView) findViewById(R.id.google_sing_in);
        chooseAuth();
    }

    public static void setAuthType(AuthType authType){
        AuthActivity.authType = authType;
    }

    public void chooseAuth(){
        buttonLogIn.setOnClickListener(this);
        textViewChangeType.setOnClickListener(this);
        buttonGoogleSingIn.setOnClickListener(this);
        switch (authType) {
            case REGISTRATION: {
                setRegistrationVisibility();
            } break;
            case LOGIN: {
                setLoginVisibility();
            } break;
        }
    }

    public void setRegistrationVisibility(){
        buttonLogIn.setText("Registration");
        editTextName.setVisibility(View.VISIBLE);
        textViewChangeType.setText(R.string.to_sing_in);
    }

    public void setLoginVisibility(){
        buttonLogIn.setText("Login");
        editTextName.setVisibility(View.GONE);
        textViewChangeType.setText(R.string.to_sing_up);
    }

    public String getEmail(){
        String email = editTextEmail.getText().toString().trim();
        return email;
    }

    public String getPassword(){
        String password = editTextPassword.getText().toString().trim();
        return password;
    }

    public String getName(){
        String name = editTextName.getText().toString().trim();
        return  name;
    }

    public boolean isFieldEmpty(String field){
        boolean bool = (TextUtils.isEmpty(field));
        return bool;
    }


    @Override
    public void onClick (View view) {
        switch (view.getId()) {
            case R.id.buttonLogIn: {
                if (isFieldEmpty(getEmail())&&isFieldEmpty(getPassword())) {
                    Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show();
                if ((authType==AuthType.REGISTRATION)&&(isFieldEmpty(getName()))){
                    Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show();
                }
                } else {
                    if (authType == AuthType.REGISTRATION) {
                        manager.registerUser(getEmail(), getPassword(), new FirebaseAuthManager.UserLoginListener() {
                            @Override
                            public void onSuccess() {
                                manager.createNewUserWithEmail(getName());
                                finish();
                                startActivity(new Intent(AuthActivity.this, ProfileActivity.class));
                            }

                            @Override
                            public String onError(String massage) {
                                return massage;
                            }
                        });
                    }
                    if (authType == AuthType.LOGIN) {
                        manager.loginUser(getEmail(), getPassword(), new FirebaseAuthManager.UserLoginListener() {
                            @Override
                            public void onSuccess() {
                                finish();
                                startActivity(new Intent(AuthActivity.this, MainActivity.class));
                            }

                            @Override
                            public String onError(String massage) {
                                return massage;
                            }
                        });
                    }
                }
            }
            case R.id.textChangeType: {
                if(authType==AuthType.REGISTRATION){
                    authType = AuthType.LOGIN;
                    chooseAuth();
                }
                if(authType==AuthType.LOGIN){
                    authType = AuthType.REGISTRATION;
                    chooseAuth();
                }
            }
            case R.id.google_sing_in: {
                manager.singInWithGoogle();
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        manager.onActivityResult(requestCode, resultCode, data, new FirebaseAuthManager.UserLoginListener() {
            @Override
            public void onSuccess() {
                manager.createNewUserWithGoogle();
            }

            @Override
            public String onError(String massage) {
                return massage;
            }
        });
    }
}

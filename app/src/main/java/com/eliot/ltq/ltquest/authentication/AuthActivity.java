package com.eliot.ltq.ltquest.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eliot.ltq.ltquest.FirebaseDataManager;
import com.eliot.ltq.ltquest.MainActivity;
import com.eliot.ltq.ltquest.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    private static AuthType authType = AuthType.LOGIN;
    private static final int RC_SIGN_IN = 121;
    private FirebaseAuthManager manager;
    private FirebaseDataManager dataManager;
    private Button buttonLogIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewChangeType;
    private ImageView buttonGoogleSingIn;
    private TextView textViewForgotPass;
    private GoogleSignInAccount gSingInAccount;
    private GoogleSignInOptions gSingInOptions;
    private GoogleSignInClient gSingInClient;
    private static boolean isNewGoogleUser = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_authentication);

        manager = new FirebaseAuthManager();
        dataManager = new FirebaseDataManager();

        buttonLogIn = (Button) findViewById(R.id.login);
        editTextEmail = (EditText) findViewById(R.id.editEmail);
        editTextPassword = (EditText) findViewById(R.id.editPassword);
        textViewChangeType = (TextView) findViewById(R.id.changeTypeReg);
        textViewForgotPass = (TextView) findViewById(R.id.forgotpass);
        buttonGoogleSingIn = (ImageView) findViewById(R.id.authButtonGoogle);

        gSingInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("192781036687-ku6jlb4rn7v41h1libsovis77uu61jd3.apps.googleusercontent.com")
                .requestEmail()
                .build();
        chooseAuth();
    }

    public static void setAuthType(AuthType authType) {
        AuthActivity.authType = authType;
    }
    public static void setIsNewGoogleUser(boolean isNewUser){
        isNewGoogleUser = isNewUser;
    }

    public void chooseAuth() {
        buttonLogIn.setOnClickListener(this);
        textViewChangeType.setOnClickListener(this);
        buttonGoogleSingIn.setOnClickListener(this);
        switch (authType) {
            case REGISTRATION: {
                setRegistrationVisibility();
            }
            break;
            case LOGIN: {
                setLoginVisibility();
            }
            break;
        }
    }

    public void singInWithGoogle() {
        gSingInClient = GoogleSignIn.getClient(this, gSingInOptions);
        Intent signInIntent = gSingInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void setRegistrationVisibility() {
        buttonLogIn.setText("SIGN IN");
        textViewChangeType.setText(R.string.haven_t_got_an_account_sign_in_with);
    }

    public void setLoginVisibility() {
        buttonLogIn.setText("LOGIN");
        textViewChangeType.setText(R.string.have_an_account_login);
    }

    public String getEmail() {
        return editTextEmail.getText().toString().trim();
    }

    public String getPassword() {
        return editTextPassword.getText().toString().trim();
    }

    public boolean isFieldEmpty(String field) {
        return (TextUtils.isEmpty(field));
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login: {
                if (isFieldEmpty(getEmail()) && isFieldEmpty(getPassword())) {
                    Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show();
                    /*if ((authType == AuthType.REGISTRATION) && (isFieldEmpty(getName()))) {
                        Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show();
                    }*/
                }
                else if (!(isEmailValid((CharSequence) getEmail()))) {
                    Toast.makeText(this, "Please, enter a valid email", Toast.LENGTH_SHORT).show();
                } else {
                    if (authType == AuthType.REGISTRATION) {
                        manager.registerUser(getEmail(), getPassword(), new FirebaseAuthManager.UserLoginListener() {
                            @Override
                            public void onSuccess() {
                                dataManager.writeCurrentUserData(manager.getCurrentUser().getUid(), new UserInformation(getEmail()));

                                finish();
                                startActivity(new Intent(AuthActivity.this, ProfileActivity.class));
                            }

                            @Override
                            public void onError(String massage) {
                                Toast.makeText(AuthActivity.this, "Cannot registrate, some problems found", Toast.LENGTH_SHORT).show();
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
                            public void onError(String massage) {
                                Toast.makeText(AuthActivity.this, "Cannot registrate, some problems found", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                break;
            }
            case R.id.changeTypeReg: {
                if (authType == AuthType.LOGIN) {
                    authType = AuthType.REGISTRATION;
                    chooseAuth();
                }
                else {
                    authType = AuthType.LOGIN;
                    chooseAuth();
                }
                break;
            }
            case R.id.authButtonGoogle: {
                singInWithGoogle();
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        gSingInAccount = GoogleSignIn.getLastSignedInAccount(this);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                gSingInAccount = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(gSingInAccount.getIdToken(), null);
                manager.firebaseAuthWithGoogle(credential, new FirebaseAuthManager.UserLoginListener() {
                    @Override
                    public void onSuccess() {
                        if(isNewGoogleUser) {
                            dataManager.writeCurrentUserData(manager.getCurrentUser().getUid(), new UserInformation(AccountType.GOOGLE, gSingInAccount.getDisplayName(), gSingInAccount.getEmail()));
                            startActivity(new Intent(AuthActivity.this, ProfileActivity.class));
                        }
                        else startActivity(new Intent(AuthActivity.this, ProfileActivity.class));
                    }

                    @Override
                    public void onError(String massage) {
                        Toast.makeText(AuthActivity.this, "Cannot registrate, some problems found", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (ApiException e) {
                Log.e("Error",e.getLocalizedMessage());
            }
        } else {
           Log.e("Error","Cannot sing in with your Google account");
        }

    }
}
package com.eliot.ltq.ltquest.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eliot.ltq.ltquest.FirebaseDataManager;
import com.eliot.ltq.ltquest.MainActivity;
import com.eliot.ltq.ltquest.R;
import com.facebook.*;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.*;
import com.google.firebase.database.DatabaseError;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    private static AuthType authType = AuthType.LOGIN;
    private static final int RC_SIGN_IN = 121;
    private FirebaseAuthManager manager;
    private FirebaseDataManager dataManager;
    private Button buttonLogIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private ImageButton buttonGoogleSingIn;
    private ImageButton buttonFacebookSignIn;
    private TextView changeTypeAuth;
    private TextView forgotPass;
    private GoogleSignInAccount gSingInAccount;
    private GoogleSignInOptions gSingInOptions;
    private GoogleSignInClient gSingInClient;
    private static boolean isNewUser = true;
    private CallbackManager facebookManager;
    private LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        facebookManager = CallbackManager.Factory.create();
        updateWithToken(AccessToken.getCurrentAccessToken());
        setContentView(R.layout.activity_authentication);

        manager = new FirebaseAuthManager();
        dataManager = new FirebaseDataManager();

        buttonLogIn = (Button) findViewById(R.id.login);
        editTextEmail = (EditText) findViewById(R.id.editEmail);
        editTextPassword = (EditText) findViewById(R.id.editPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.confirmPassword);
        changeTypeAuth = (TextView) findViewById(R.id.changeTypeAuth);
        forgotPass = (TextView) findViewById(R.id.forgotpass);
        buttonGoogleSingIn = (ImageButton) findViewById(R.id.authButtonGoogle);
        buttonFacebookSignIn = (ImageButton) findViewById(R.id.authButtonFacebook);

        loginButtonForFacebookInit();

        gSingInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("192781036687-ku6jlb4rn7v41h1libsovis77uu61jd3.apps.googleusercontent.com")
                .requestEmail()
                .build();
        chooseAuth();
    }

    public static void setAuthType(AuthType authType) {
        AuthActivity.authType = authType;
    }

    public static void setIsNewUser(boolean isNewUser) {
        AuthActivity.isNewUser = isNewUser;
    }

    public void chooseAuth() {
        buttonLogIn.setOnClickListener(this);
        changeTypeAuth.setOnClickListener(this);
        buttonGoogleSingIn.setOnClickListener(this);
        buttonFacebookSignIn.setOnClickListener(this);
        forgotPass.setOnClickListener(this);
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

    public void loginButtonForFacebookInit() {
        loginButton = findViewById(R.id.fb_login_button);
        loginButton.registerCallback(facebookManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken(), manager.getAuth(), loginResult);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });
    }

    private void updateWithToken(AccessToken currentAccessToken) {

        if (currentAccessToken != null) {
            LoginManager.getInstance().logOut();
        } else {
        }
    }


    public void setRegistrationVisibility() {
        forgotPass.setVisibility(View.GONE);
        editTextConfirmPassword.setVisibility(View.VISIBLE);
        buttonLogIn.setText("SIGN UP");
        changeTypeAuth.setText(R.string.have_an_account_login);
    }

    public void setLoginVisibility() {
        editTextConfirmPassword.setVisibility(View.GONE);
        forgotPass.setVisibility(View.VISIBLE);
        buttonLogIn.setText("LOGIN");
        changeTypeAuth.setText(R.string.haven_t_got_an_account_sign_in_with);
    }

    public String getEmail() {
        return editTextEmail.getText().toString().trim();
    }

    public String getPassword() {
        return editTextPassword.getText().toString().trim();
    }

    public String getConfirmPassword() {
        return editTextConfirmPassword.getText().toString().trim();
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
                if (isFieldEmpty(getEmail()) || isFieldEmpty(getPassword())) {
                    Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show();
                } else if (!(isEmailValid((CharSequence) getEmail()))) {
                    Toast.makeText(this, "Please, enter a valid email", Toast.LENGTH_SHORT).show();
                } else {
                    if (authType == AuthType.LOGIN) {
                        manager.loginUser(getEmail(), getPassword(), new FirebaseAuthManager.UserLoginListener() {
                            @Override
                            public void onSuccess() {
                                finish();
                                startActivity(new Intent(AuthActivity.this, MainActivity.class));
                            }

                            @Override
                            public void onError(String massage) {
                                if (!isNetworkAvailable()) {
                                    Toast.makeText(AuthActivity.this, "Don't have Internet connection", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AuthActivity.this, "Cannot login, some problems found", Toast.LENGTH_SHORT).show();
                                }
                                Log.e("User Mail Login: ", massage);
                            }
                        });
                    } else if (authType == AuthType.REGISTRATION) {
                        if (getPassword().equals(getConfirmPassword())) {
                            manager.registerUser(getEmail(), getPassword(), new FirebaseAuthManager.UserLoginListener() {
                                @Override
                                public void onSuccess() {
                                    dataManager.writeCurrentUserData(manager.getCurrentUser().getUid(),
                                            new UserInformation(getEmail()), new FirebaseDataManager.UserInformationWritingListener() {
                                                @Override
                                                public void onSuccess() {
                                                    startActivity(new Intent(AuthActivity.this, MainActivity.class));
                                                }

                                                @Override
                                                public void onError() {
                                                    /*
                                                    it check if app can read current UserData and if its false, delete user's Firebase account
                                                     */
                                                    Toast.makeText(AuthActivity.this, "Something wrong with your registration, please try again", Toast.LENGTH_SHORT).show();
                                                    FirebaseUser user = manager.getCurrentUser();
                                                    manager.logout(new FirebaseAuthManager.UserLoginListener() {
                                                        @Override
                                                        public void onSuccess() {

                                                        }

                                                        @Override
                                                        public void onError(String massage) {

                                                        }
                                                    });
                                                    manager.deleteUser(user, new FirebaseAuthManager.UserLoginListener() {
                                                        @Override
                                                        public void onSuccess() {

                                                        }

                                                        @Override
                                                        public void onError(String massage) {
                                                            Log.e("MailRegUsDelFail: ", massage);
                                                        }
                                                    });
                                                }
                                            });
                                }

                                @Override
                                public void onError(String massage) {
                                    if (!isNetworkAvailable()) {
                                        Toast.makeText(AuthActivity.this, "Don't have Internet connection", Toast.LENGTH_SHORT).show();
                                    } else if (getPassword().length() < 6) {
                                        Toast.makeText(AuthActivity.this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
                                    } else if (!isNewUser) {
                                        Toast.makeText(AuthActivity.this, "User with this email is already exist", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(AuthActivity.this, "Cannot registrate, some problems found", Toast.LENGTH_SHORT).show();
                                    }
                                    Log.e("User Mail Regist: ", massage);
                                }
                            });
                        } else {
                            Toast.makeText(AuthActivity.this, "Please, confirm your password correctly", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            break;
            case R.id.changeTypeAuth: {
                if (authType == AuthType.LOGIN) {
                    authType = AuthType.REGISTRATION;
                    chooseAuth();
                } else {
                    authType = AuthType.LOGIN;
                    chooseAuth();
                }
                break;
            }
            case R.id.authButtonGoogle: {
                if (!isNetworkAvailable()) {
                    Toast.makeText(AuthActivity.this, "Don't have Internet connection", Toast.LENGTH_SHORT).show();
                } else {
                    singInWithGoogle();
                }
                break;
            }
            case R.id.authButtonFacebook: {
                loginButton.performClick();
                //Toast.makeText(this, "Sorry, sign in with Facebook is disable in this version", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.forgotpass: {
                Toast.makeText(this, "Sorry, this function is disable in this version", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        gSingInAccount = GoogleSignIn.getLastSignedInAccount(this);
        facebookManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                gSingInAccount = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(gSingInAccount.getIdToken(), null);
                manager.firebaseAuthWithGoogle(credential, new FirebaseAuthManager.UserLoginListener() {
                    @Override
                    public void onSuccess() {
                        if (isNewUser) {
                            dataManager.writeCurrentUserData(manager.getCurrentUser().getUid(),
                                    new UserInformation(AccountType.GOOGLE, gSingInAccount.getDisplayName(),
                                            gSingInAccount.getEmail()), new FirebaseDataManager.UserInformationWritingListener() {
                                        @Override
                                        public void onSuccess() {
                                            startActivity(new Intent(AuthActivity.this, MainActivity.class));
                                        }

                                        @Override
                                        public void onError() {
                                            /*
                                                    it check if app can read current UserData and if its false, delete user's Firebase account
                                                     */
                                            Toast.makeText(AuthActivity.this, "Something wrong with your sign in, please try again", Toast.LENGTH_SHORT).show();
                                            FirebaseUser user = manager.getCurrentUser();
                                            manager.logout(new FirebaseAuthManager.UserLoginListener() {
                                                @Override
                                                public void onSuccess() {

                                                }

                                                @Override
                                                public void onError(String massage) {

                                                }
                                            });
                                            manager.deleteUser(user, new FirebaseAuthManager.UserLoginListener() {
                                                @Override
                                                public void onSuccess() {

                                                }

                                                @Override
                                                public void onError(String massage) {
                                                    Log.e("GogRegUsDelFail:", massage);
                                                }
                                            });
                                        }
                                    });
                        } else startActivity(new Intent(AuthActivity.this, MainActivity.class));
                    }

                    @Override
                    public void onError(String massage) {
                        if (isNetworkAvailable()) {
                            Toast.makeText(AuthActivity.this, "Cannot sign in with google, some problems found", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AuthActivity.this, "Don't have Internet connection", Toast.LENGTH_SHORT).show();
                        }
                        Log.e("User Google sign: ", massage);
                    }
                });
            } catch (ApiException e) {
                Log.e("Error", e.getLocalizedMessage());
            }
        } else {
            if (!isNetworkAvailable()) {
                Toast.makeText(AuthActivity.this, "Don't have Internet connection", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("Error", "Cannot sing in with your Google account");
            }
        }

    }

    private void handleFacebookAccessToken(AccessToken token, FirebaseAuth auth, final LoginResult loginResult) {
        Log.d("FacebookAccess", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.getResult().getAdditionalUserInfo().isNewUser()){
                            isNewUser=true;
                        }
                        else isNewUser=false;
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FacebookAccess", "signInWithCredential:success");
                            if (isNewUser) {
                                String name = task.getResult().getUser().getDisplayName();
                                String email = task.getResult().getUser().getEmail();
                                String photoUrl = task.getResult().getUser().getPhotoUrl().toString();
                                        dataManager.writeCurrentUserData(manager.getCurrentUser().getUid(),
                                                    new UserInformation(name, email, photoUrl), new FirebaseDataManager.UserInformationWritingListener() {
                                                        @Override
                                                        public void onSuccess() {
                                                            startActivity(new Intent(AuthActivity.this, MainActivity.class));
                                                        }

                                                        @Override
                                                        public void onError() {
                                                            Toast.makeText(AuthActivity.this, "Something wrong with your sign in, please try again", Toast.LENGTH_SHORT).show();
                                                            FirebaseUser user = manager.getCurrentUser();
                                                            manager.logout(new FirebaseAuthManager.UserLoginListener() {
                                                                @Override
                                                                public void onSuccess() {

                                                                }

                                                                @Override
                                                                public void onError(String massage) {

                                                                }
                                                            });
                                                            manager.deleteUser(user, new FirebaseAuthManager.UserLoginListener() {
                                                                @Override
                                                                public void onSuccess() {

                                                                }

                                                                @Override
                                                                public void onError(String massage) {
                                                                    Log.e("GogRegUsDelFail:", massage);
                                                                }
                                                            });
                                                        }
                                                    });

                            } else startActivity(new Intent(AuthActivity.this, MainActivity.class));
                        }
                        else {Toast.makeText(AuthActivity.this, "Auth with Facebook failed", Toast.LENGTH_SHORT).show();}
                    }
                });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        // do nothing.
    }
}
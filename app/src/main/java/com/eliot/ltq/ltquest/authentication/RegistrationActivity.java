package com.eliot.ltq.ltquest.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eliot.ltq.ltquest.MainActivity;
import com.eliot.ltq.ltquest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSingUp;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSingIn;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }

        buttonSingUp = (Button) findViewById(R.id.buttonSingUp);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        textViewSingIn = (TextView) findViewById(R.id.textSingIn);

        buttonSingUp.setOnClickListener(this);
        textViewSingIn.setOnClickListener(this);

        progressBar = new ProgressBar(this);
    }

    private void UserRegistration(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            //email is empty
            Toast.makeText(this, "Please, enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            //password is empty
            Toast.makeText(this, "Please, enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.getProgress();
        progressBar.isShown();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            finish();
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        }
                        else {
                            Toast.makeText(RegistrationActivity.this, "Could not register!",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if (view == buttonSingUp){
            UserRegistration();
        }
        if (view == textViewSingIn){
            finish();
            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
        }
    }
}

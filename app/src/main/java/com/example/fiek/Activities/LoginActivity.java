package com.example.fiek.Activities;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fiek.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            clLogin.setVisibility(View.VISIBLE);
            clDetails.setVisibility(View.VISIBLE);
        }
    };

    private EditText userEmail, userPassword;
    private Button btnLogin;
    private ProgressBar loginProgress;
    private FirebaseAuth mAuth;
    private Intent HomeActivity;
    private Intent RegisterActivity;
    private TextView txtRegister;
    private ConstraintLayout clDetails;
    private ConstraintLayout clLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        userEmail = findViewById(R.id.login_mail);
        userPassword = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.btnLogin);
        loginProgress = findViewById(R.id.loadingProgress);
        txtRegister = findViewById(R.id.txtRegister);
        clDetails = findViewById(R.id.clDetails);
        clLogin = findViewById(R.id.clLogin);
        mAuth = FirebaseAuth.getInstance();
        HomeActivity = new Intent(this, com.example.fiek.Activities.Home.class);
        RegisterActivity = new Intent(this,com.example.fiek.Activities.RegisterActivity.class);

        handler.postDelayed(runnable,1500);
        loginProgress.setVisibility(View.INVISIBLE);



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProgress.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);

                final String email = userEmail.getText().toString();
                final String password = userPassword.getText().toString();
                if (email.isEmpty() || password.isEmpty()){
                    showMessage("Ju lutem plotesoni te dhenat");
                    loginProgress.setVisibility(View.INVISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                }
                else{
                    signIn(email,password);
                }
            }
        });

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(RegisterActivity);
                finish();
            }
        });
    }

    private void signIn(String email, String password) {


        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    loginProgress.setVisibility(View.INVISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                    updateUI();
                }

            }
        });

    }

    private void updateUI() {
        startActivity(HomeActivity);
        finish();
    }


    private void showMessage(String meesage) {
        Toast.makeText(getApplicationContext(),meesage,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            updateUI();
        }
    }


}

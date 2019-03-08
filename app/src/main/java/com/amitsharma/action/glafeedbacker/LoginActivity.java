package com.amitsharma.action.glafeedbacker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private ImageView loginButton;
    private EditText emailField,passField;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private TextView forgetPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        mProgress=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();

        loginButton= findViewById(R.id.loginButton);
        emailField=findViewById(R.id.loginEmailField);
        passField=findViewById(R.id.loginPasswordField);
        forgetPass=findViewById(R.id.forgetLink);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInputs();
            }
        });

        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(LoginActivity.this,ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

    }

    private void validateInputs() {

        String email=emailField.getText().toString().toLowerCase().trim();
        String pass=passField.getText().toString().trim();

        if (email.isEmpty()){
            emailField.setError("Email id required");
            emailField.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailField.setError("Please enter a valid email address");
            emailField.requestFocus();
            return;
        }

        if (pass.isEmpty()){
            passField.setError("Password should not be empty");
            passField.requestFocus();
            return;
        }

        if (pass.length()<6){
            passField.setError("Password length should be 6");
            passField.requestFocus();
            return;
        }

        mProgress.setTitle("Logging in..");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        loginUser(email,pass);
    }

    private void loginUser(String email, String pass) {

        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    mProgress.dismiss();

                    Intent intent=new Intent(LoginActivity.this, MainPanelActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    mProgress.dismiss();

                    if (task.getException() instanceof FirebaseAuthInvalidUserException){
                        Toast.makeText(getApplicationContext(),"User with email address does not exist!",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}

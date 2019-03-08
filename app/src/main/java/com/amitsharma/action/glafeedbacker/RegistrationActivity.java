package com.amitsharma.action.glafeedbacker;

import android.accounts.NetworkErrorException;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private FirebaseUser currentUser;
    private String uid;

    private EditText emailField,passField,conPassField;
    private ImageView signupButton;
    private Spinner mSpinner;
    private String designation="";
    private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FirebaseApp.initializeApp(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_registration);

        mAuth=FirebaseAuth.getInstance();

        mProgress=new ProgressDialog(this);

        emailField=findViewById(R.id.signupEmailField);
        passField=findViewById(R.id.signupPasswordField);
        conPassField=findViewById(R.id.signupConPasswordField);
        signupButton=findViewById(R.id.signupButton);
        mSpinner =findViewById(R.id.spinner);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInputs();
            }
        });

    }

    private void validateInputs() {
        String email=emailField.getText().toString().toLowerCase().trim();
        String pass=passField.getText().toString().trim();
        String conPass=conPassField.getText().toString().trim();

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                designation=parent.getItemAtPosition(position).toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

        if (!conPass.equals(pass)){
            conPassField.setError("Passwords did not match");
            conPassField.requestFocus();
            return;
        }

        if (designation.equals("Who are you?") || designation.equals("")){
            Toast.makeText(RegistrationActivity.this, "Please select your designation!", Toast.LENGTH_SHORT).show();
            return;
        }

        mProgress.setTitle("Registering User");
        mProgress.setMessage("Please wait while we create your account !");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        createAccount(email,pass,designation);
    }

    private void createAccount(String email, String pass, final String designation) {

        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    currentUser=mAuth.getCurrentUser();
                    uid=currentUser.getUid();

                    mRef= FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                    mRef.child("designation").setValue(designation).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                mProgress.dismiss();
                                Toast.makeText(RegistrationActivity.this,"Registration Successfull!",Toast.LENGTH_LONG).show();

                                Intent mainIntent = new Intent(RegistrationActivity.this, MainPanelActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();
                            }else {
                                mProgress.dismiss();
                                Toast.makeText(RegistrationActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else{
                    mProgress.dismiss();
                    if (task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(RegistrationActivity.this, "You are already registered!",
                                Toast.LENGTH_SHORT).show();

                    }
                    else if (task.getException() instanceof NetworkErrorException){
                        Toast.makeText(RegistrationActivity.this, "Please check your internet connection!",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {

                        Toast.makeText(RegistrationActivity.this, task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
    }
}

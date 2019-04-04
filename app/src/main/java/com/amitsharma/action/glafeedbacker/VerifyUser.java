package com.amitsharma.action.glafeedbacker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VerifyUser extends AppCompatActivity {

    private Button verifyBtn;
    private TextView result,resultCon;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    ProgressDialog mProgress;
//#e57373
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_verify_user);

        mProgress=new ProgressDialog(this);

        verifyBtn=(Button) findViewById(R.id.verifyButton);
        result=(TextView) findViewById(R.id.verifyResult);
        resultCon=(TextView) findViewById(R.id.verifyConfirm);

        mAuth=FirebaseAuth.getInstance();

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyUser();
            }
        });
    }

    private void verifyUser() {
        mProgress.setTitle("Sending verification link!");
        mProgress.setMessage("Please wait!");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();


        final FirebaseUser current_user=mAuth.getCurrentUser();
        final String uid = current_user.getUid();
        current_user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mProgress.dismiss();

                if (task.isSuccessful()){
                    verifyBtn.setAlpha(0.5f);
                    verifyBtn.setEnabled(false);

                    result.setText("Verification link sent to your email address!\nClick the link below after verifying your email.");
                    resultCon.setText("Continue >>");
                    resultCon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            mProgress.setTitle("Confirming Verification..");
                            mProgress.setMessage("Please wait!");
                            mProgress.setCanceledOnTouchOutside(false);
                            mProgress.show();

                            FirebaseAuth.getInstance().getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                        mProgress.dismiss();

                                        if (current_user.isEmailVerified()){
                                            Intent intent =new Intent(VerifyUser.this,PostFeedback.class);
                                            startActivity(intent);

                                        }else {
                                            Toast.makeText(VerifyUser.this, "First verify your email!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });

                        }
                    });
                }
                else{
                    Toast.makeText(VerifyUser.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void goToMainPanel(View view) {
        Intent intent =new Intent(VerifyUser.this,PostFeedback.class);
        startActivity(intent);
    }
}

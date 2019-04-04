package com.amitsharma.action.glafeedbacker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostFeedback extends AppCompatActivity {
    private Toolbar mToolbar;
    private ImageView star1,star2,star3,star4,star5;
    private Button postButton;
    private EditText feedInput,suggestionInput;
    private Spinner feedType;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef,mFeedbackRef;
    private String uid="",feedText="",suggText="",feedTypeText="",saveCurrentDate="",saveCurrentTime="",postRandomName="",time;
    private FirebaseUser curUser;
    private int star=1,pos=0;
    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_post_feedback);

        mAuth=FirebaseAuth.getInstance();
        mUserRef= FirebaseDatabase.getInstance().getReference().child("users");
        mFeedbackRef= FirebaseDatabase.getInstance().getReference().child("Feedbacks");
        curUser=mAuth.getCurrentUser();
        uid= curUser.getUid();

        mProgress=new ProgressDialog(this);

        mToolbar=findViewById(R.id.post_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Hi, "+curUser.getDisplayName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
/*
        FirebaseUser user = mAuth.getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(mName).build();

        user.updateProfile(profileUpdates);
*/
        star1=findViewById(R.id.star1);
        star2=findViewById(R.id.star2);
        star3=findViewById(R.id.star3);
        star4=findViewById(R.id.star4);
        star5=findViewById(R.id.star5);

        postButton=findViewById(R.id.post_button);

        feedInput=findViewById(R.id.feedback_field);
        suggestionInput=findViewById(R.id.suggetion_field);

        feedType=findViewById(R.id.feed_type);

        feedType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                feedTypeText=parent.getItemAtPosition(position).toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOne();
                star=1;
            }
        });

        star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTwo();
                star=2;
            }
        });

        star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectThree();
                star=3;
            }
        });

        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFour();
                star=4;
            }
        });
        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFive();
                star=5;
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyField();

            }
        });

    }

    private void verifyField() {
        feedText=feedInput.getText().toString().trim();
        suggText=suggestionInput.getText().toString().trim();

        if (feedText.isEmpty()){
            feedInput.setError("Please write your feedback.");
            feedInput.requestFocus();
            return;
        }
        if (suggText.isEmpty()){
            suggestionInput.setError("Please write your feedback.");
            suggestionInput.requestFocus();
            return;
        }
        if (feedTypeText.equals("Select feedback type?")){
            Toast.makeText(PostFeedback.this, "Please select feedback type.", Toast.LENGTH_SHORT).show();
            return;
        }

        mProgress.setTitle("Posting Feedback..");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        HashMap feedMap=new HashMap<>();
        feedMap.put("name",curUser.getDisplayName());
        feedMap.put("feedType",feedTypeText);
        feedMap.put("feedText",feedText);
        feedMap.put("feedRating",Integer.toString(star));
        feedMap.put("feedSuggestion",suggText);

        long milis=System.currentTimeMillis();
        time=Long.toString(milis);

        Calendar calendarForDate= Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd:MMMM:yyyy");
        saveCurrentDate=currentDate.format(calendarForDate.getTime());

        Calendar calendarForTime= Calendar.getInstance();
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm");
        saveCurrentTime=currentTime.format(calendarForTime.getTime());

        postRandomName=saveCurrentDate+saveCurrentTime;

        mFeedbackRef.child(uid + postRandomName).updateChildren(feedMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    mProgress.dismiss();
                    Toast.makeText(PostFeedback.this, "Feedback posted successfully :)", Toast.LENGTH_LONG).show();

                    Intent intent=new Intent(PostFeedback.this,MainPanelActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                }else{

                    mProgress.dismiss();
                    Toast.makeText(PostFeedback.this, task.getException().getMessage()+"Please retry!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void selectOne() {
        star1.setImageResource(R.drawable.yellow_star);
        star2.setImageResource(R.drawable.grey_star);
        star3.setImageResource(R.drawable.grey_star);
        star4.setImageResource(R.drawable.grey_star);
        star5.setImageResource(R.drawable.grey_star);
    }
    private void selectTwo() {
        star1.setImageResource(R.drawable.yellow_star);
        star2.setImageResource(R.drawable.yellow_star);
        star3.setImageResource(R.drawable.grey_star);
        star4.setImageResource(R.drawable.grey_star);
        star5.setImageResource(R.drawable.grey_star);
    }
    private void selectThree() {
        star1.setImageResource(R.drawable.yellow_star);
        star2.setImageResource(R.drawable.yellow_star);
        star3.setImageResource(R.drawable.yellow_star);
        star4.setImageResource(R.drawable.grey_star);
        star5.setImageResource(R.drawable.grey_star);
    }
    private void selectFour() {
        star1.setImageResource(R.drawable.yellow_star);
        star2.setImageResource(R.drawable.yellow_star);
        star3.setImageResource(R.drawable.yellow_star);
        star4.setImageResource(R.drawable.yellow_star);
        star5.setImageResource(R.drawable.grey_star);
    }
    private void selectFive() {
        star1.setImageResource(R.drawable.yellow_star);
        star2.setImageResource(R.drawable.yellow_star);
        star3.setImageResource(R.drawable.yellow_star);
        star4.setImageResource(R.drawable.yellow_star);
        star5.setImageResource(R.drawable.yellow_star);
    }


}

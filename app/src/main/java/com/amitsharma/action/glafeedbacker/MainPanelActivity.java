package com.amitsharma.action.glafeedbacker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainPanelActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef,mFeedbacks;
    private RecyclerView mRecyclerView;
    private String name="",uid="";
    private FirebaseUser currentuser;
    //private ImageView star1,star2,star3,star4,star5;

    boolean doubleTap=false;

    private CircleImageView navProfileImageView;
    private TextView navProfileUserName;

    private LinearLayoutManager linearLayoutManager;

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Context context;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_main_panel);

        mAuth=FirebaseAuth.getInstance();
        mRef= FirebaseDatabase.getInstance().getReference().child("users");
        mFeedbacks=FirebaseDatabase.getInstance().getReference().child("Feedbacks");

        context=getApplicationContext();

        mToolbar=(Toolbar) findViewById(R.id.main_page_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("GLA-Feedbacker");

        drawerLayout=(DrawerLayout) findViewById(R.id.main_drawer_layout);
        actionBarDrawerToggle=new ActionBarDrawerToggle(MainPanelActivity.this,drawerLayout,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        navigationView=(NavigationView) findViewById(R.id.main_navigation_view);
        fab=(FloatingActionButton) findViewById(R.id.fab);

        //star1=findViewById(R.id.s1);
        //star2=findViewById(R.id.s2);
        //star3=findViewById(R.id.s3);
        //star4=findViewById(R.id.s4);
        //star5=findViewById(R.id.s5);

        mRecyclerView=findViewById(R.id.main_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mRecyclerView.setLayoutManager(linearLayoutManager);


        View navView=navigationView.inflateHeaderView(R.layout.navigation_header);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                UserMenuSelector(menuItem);
                return false;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentuser.isEmailVerified()){
                    startActivity(new Intent(MainPanelActivity.this,PostFeedback.class));
                }else{
                    startActivity(new Intent(MainPanelActivity.this,VerifyUser.class));
                }
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

        currentuser=mAuth.getCurrentUser();
        if (currentuser==null){
            sendUserToMainActivity();
        }

        FirebaseRecyclerOptions options=new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(mFeedbacks, Model.class)
                .build();


        FirebaseRecyclerAdapter<Model,FeedbackViewHolder> firebaseRecyclerAdapter
                =new FirebaseRecyclerAdapter<Model,FeedbackViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position, @NonNull Model model) {

                //from here we can check if user is anonymous or not than we can display profile image and name of user accordingly

                final String postKey=getRef(position).getKey();

                holder.setName(model.getName());
                holder.setFeedText(model.getFeedText().trim());
                holder.setFeedType(model.getFeedType());
                holder.setFeedRating(model.getFeedRating());


            }

            @NonNull
            @Override
            public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_recycler,parent,false);
                FeedbackViewHolder viewHolder=new FeedbackViewHolder(view);
                return viewHolder;
            }
        };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class FeedbackViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public FeedbackViewHolder(View itemView) {
            super(itemView);

            mView=itemView;
        }
        public void setName(String name) {
            TextView userName= mView.findViewById(R.id.display_name);
            userName.setText(name);
        }

        public void setFeedText(String feedText){
            TextView FeedText= mView.findViewById(R.id.feedback_text);
            FeedText.setText(feedText);
        }

        public void setFeedType(String feedType) {
            TextView FeedType= mView.findViewById(R.id.feedback_type);
            FeedType.setText(feedType);

            CircleImageView feedImage= mView.findViewById(R.id.feed_type_image);

                if (feedType.equals("Hostel")){
                    feedImage.setImageResource(R.drawable.hostel);
                }else if (feedType.equals("Mess")){
                    feedImage.setImageResource(R.drawable.mess);
                }else if (feedType.equals("Academic")){
                    feedImage.setImageResource(R.drawable.academic);
                }else if (feedType.equals("Training and Placement")){
                    feedImage.setImageResource(R.drawable.placement);
                }else if (feedType.equals("Environment")){
                    feedImage.setImageResource(R.drawable.sports);
                }else if (feedType.equals("Faculty")){
                    feedImage.setImageResource(R.drawable.faculty);
                }else if (feedType.equals("Other")){
                    feedImage.setImageResource(R.drawable.academic);
                }else if (feedType.equals("Students")){
                    feedImage.setImageResource(R.drawable.students);
                }
        }

        public void setFeedRating(String feedRating){
            ImageView star1,star2,star3,star4,star5;

            star1=mView.findViewById(R.id.s1);
            star2=mView.findViewById(R.id.s2);
            star3=mView.findViewById(R.id.s3);
            star4=mView.findViewById(R.id.s4);
            star5=mView.findViewById(R.id.s5);

                if (feedRating.equals("1")){
                    star1.setImageResource(R.drawable.yellow_star);
                    star2.setImageResource(R.drawable.grey_star);
                    star3.setImageResource(R.drawable.grey_star);
                    star4.setImageResource(R.drawable.grey_star);
                    star5.setImageResource(R.drawable.grey_star);
                }

                if (feedRating.equals("2")){
                    star1.setImageResource(R.drawable.yellow_star);
                    star2.setImageResource(R.drawable.yellow_star);
                    star3.setImageResource(R.drawable.grey_star);
                    star4.setImageResource(R.drawable.grey_star);
                    star5.setImageResource(R.drawable.grey_star);
                }
                if (feedRating.equals("3")){
                    star1.setImageResource(R.drawable.yellow_star);
                    star2.setImageResource(R.drawable.yellow_star);
                    star3.setImageResource(R.drawable.yellow_star);
                    star4.setImageResource(R.drawable.grey_star);
                    star5.setImageResource(R.drawable.grey_star);
                }

                if (feedRating.equals("4")){
                    star1.setImageResource(R.drawable.yellow_star);
                    star2.setImageResource(R.drawable.yellow_star);
                    star3.setImageResource(R.drawable.yellow_star);
                    star4.setImageResource(R.drawable.yellow_star);
                    star5.setImageResource(R.drawable.grey_star);
                }

                if (feedRating.equals("5")){
                    star1.setImageResource(R.drawable.yellow_star);
                    star2.setImageResource(R.drawable.yellow_star);
                    star3.setImageResource(R.drawable.yellow_star);
                    star4.setImageResource(R.drawable.yellow_star);
                    star5.setImageResource(R.drawable.yellow_star);
                }


        }

    }




    @Override
    public void onBackPressed() {
        if (doubleTap){
            super.onBackPressed();
        }
        else {
            Toast.makeText(context, "Double tap back to exit the app!", Toast.LENGTH_SHORT).show();
            doubleTap=true;
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleTap=false;
                }
            },500); //half second
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem menuItem) {

        switch (menuItem.getItemId()){

            case R.id.nav_my_home:
                    drawerLayout.closeDrawer(Gravity.START);
                break;


            case R.id.nav_log_out:
                AlertDialog.Builder builder=new AlertDialog.Builder(MainPanelActivity.this);
                builder.setTitle("Dou you really want to logout!");
                builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        sendUserToMainActivity();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog=builder.create();
                alertDialog.show();
                break;

            case R.id.nav_about_us:
                Intent aboutIntent=new Intent(MainPanelActivity.this,AboutUsActivity.class);
                startActivity(aboutIntent);
                break;
        }
    }

    private void sendUserToMainActivity() {

        Intent mainIntent = new Intent(MainPanelActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


}

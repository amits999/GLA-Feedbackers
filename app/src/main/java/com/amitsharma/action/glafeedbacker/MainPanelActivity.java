package com.amitsharma.action.glafeedbacker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;

public class MainPanelActivity extends AppCompatActivity {
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_main_panel);

        mToolbar=(Toolbar) findViewById(R.id.student_page_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("  Hi, Amit");
        //getSupportActionBar().setIcon(getDrawable(R.drawable.app_bar_icon));
    }
}

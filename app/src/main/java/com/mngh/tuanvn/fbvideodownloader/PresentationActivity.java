package com.mngh.tuanvn.fbvideodownloader;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class PresentationActivity extends AppCompatActivity {
    Button start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        SharedPreferences mPref = getSharedPreferences("adsserver", Activity.MODE_PRIVATE);
//        if(mPref.contains("shortcut"))
//        {
//            mPref.edit().remove("shortcut").commit();
//        }

        setContentView(R.layout.activity_presentation);
        start = findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PresentationActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}

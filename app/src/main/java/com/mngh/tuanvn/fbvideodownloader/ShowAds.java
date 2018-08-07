package com.mngh.tuanvn.fbvideodownloader;

import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;


public class ShowAds extends AppCompatActivity {
    private static ShowAds instance;

    public static ShowAds getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.about);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            setTaskDescription(new ActivityManager.TaskDescription("", bitmap,
                    ContextCompat.getColor(getApplicationContext(), R.color.tuan)));
        if (instance == null)
            instance = this;
    }

}

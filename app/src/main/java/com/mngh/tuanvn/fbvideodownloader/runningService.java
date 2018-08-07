package com.mngh.tuanvn.fbvideodownloader;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class runningService extends Service {
    public static boolean check = false;
    private static boolean killedAds = true;
    private boolean threeDay = false;
    private long oldTime;
    private long hours;
    private Runnable runnableCode;
    private Handler handler1;
    private SharedPreferences pref;


    @Override
    public void onCreate() {
        pref = getApplicationContext().getSharedPreferences("DataCountService", MODE_PRIVATE);
        handler1 = new Handler();
        super.onCreate();
        MyBroadcast myBroadcast = new MyBroadcast();
        IntentFilter filter = new IntentFilter("android.intent.action.USER_PRESENT");
        registerReceiver(myBroadcast, filter);

        // milli min  hour  day 30day
        oldTime = pref.getLong("timeInstall", 0);
        hours = 1000 * 60 * 60;

        scheduleTask();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void scheduleTask() {
        ScheduledExecutorService scheduleTaskExecutor = Executors.newSingleThreadScheduledExecutor();
        int delayAds = pref.getInt("delayAds", 0);

        hours = 1000 * 60 * 60 * delayAds;
        // This schedule a runnable task every 2 minutes
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                try {
                    runnableCode = new Runnable() {
                        @Override
                        public void run() {
                            long current = System.currentTimeMillis();
                            if (current - oldTime >= hours)
                                threeDay = true;

                            Random r = new Random();
                            int rand = r.nextInt(100);
                            int int_percentAds = pref.getInt("percentAds", 0);

                            if (check && threeDay && (rand < int_percentAds)) {
                                final InterstitialAd mInterstitialAd;
                                mInterstitialAd = new InterstitialAd(runningService.this);
                                mInterstitialAd.setAdUnitId("/21617015150/734252/21734167453");
                                mInterstitialAd.loadAd(new AdRequest.Builder().build());

                                mInterstitialAd.setAdListener(new AdListener() {
                                    @Override
                                    public void onAdLoaded() {
                                        // Code to be executed when an ad finishes loading.
                                        Intent showAds = new Intent(getApplicationContext(), ShowAds.class);
                                        showAds.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        if (killedAds && check) {
                                            startActivity(showAds);
                                            mInterstitialAd.show();
                                            killedAds = false;
                                        }
                                    }

                                    @Override
                                    public void onAdClosed() {
                                        check = false;
                                        killedAds = true;
                                        try {
                                            if (Build.VERSION.SDK_INT < 21) {
                                                ShowAds.getInstance().finishAffinity();
                                            } else {
                                                ShowAds.getInstance().finishAndRemoveTask();
                                            }
                                            android.os.Process.killProcess(android.os.Process.myPid());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }

                        }
                    };
                    handler1.post(runnableCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1, 5, TimeUnit.MINUTES);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class MyBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("OnScreen", " U 've opened app");
            check = true;
        }
    }
}

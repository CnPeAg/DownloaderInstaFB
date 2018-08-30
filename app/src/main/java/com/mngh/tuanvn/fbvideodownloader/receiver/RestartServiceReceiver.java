package com.mngh.tuanvn.fbvideodownloader.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mngh.tuanvn.fbvideodownloader.service.MyService;

public class RestartServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("cao", "onReceive");
        if (!checkServiceRunning(context))
            context.startService(new Intent(context.getApplicationContext(), MyService.class));
//        Intent select = new Intent(context, MainActivity.class);
//        context.startActivity(select);
    }

    public boolean checkServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.mngh.tuanvn.fbvideodownloader.service.MyService"
                    .equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
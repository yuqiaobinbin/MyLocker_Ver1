package com.example.mylocker_ver1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


// 接收开机广播,开启锁屏服务
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // BOOT_COMPLETED是系统在开机加载完毕后发送的
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            if(Config.getBootOnEnable(context)) {
                LockService.KILL = false;
                context.startService(new Intent(context, LockService.class));
                if (BuildConfig.DEBUG) Log.d("BootReceiver", "BOOT SERVICE");
            }
        }
    }
}

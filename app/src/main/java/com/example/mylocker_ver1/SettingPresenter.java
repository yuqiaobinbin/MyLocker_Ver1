package com.example.mylocker_ver1;

import android.app.Activity;
import android.content.Intent;

public class SettingPresenter{
    private Activity activity;
    private Intent serviceIntent;

    // init Intent
    public void start() {
        serviceIntent = new Intent(activity, LockService.class);
    }

    // 赋值私有变量
    public SettingPresenter(Activity ac) {
        this.activity = ac;
    }

    //更新锁屏的状态
    public void setLockStatus(boolean isChecked) {
        if (isChecked && !LockService.running) {
            LockService.KILL = false;
            activity.startService(serviceIntent);
        } else if (!isChecked && LockService.running) {
            LockService.KILL = true;
            activity.stopService(serviceIntent);
        }
    }
}

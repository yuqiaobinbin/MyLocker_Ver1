package com.example.mylocker_ver1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mylocker_ver1.algorithm.SaveImage;
import com.example.mylocker_ver1.SaveImageActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.osgi.OpenCVNativeLoader;

public class MainActivity extends AppCompatActivity {
    private CheckBox lockSwitcher;
    private TextView switcherInfo;
    private SaveImage viewManager = null;
    private SaveImageActivity SaveCoord = null;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            super.onManagerConnected(status);
            if (status == LoaderCallbackInterface.SUCCESS) {
                Log.d("LogLogLog", "成功");
            }
        }
    };

    @SuppressLint("SdCardPath")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 申请覆盖在应用程序上层的权限，否则会出现闪退
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 1);
            }
        }

        new OpenCVNativeLoader().init();

        final SettingPresenter settingPresenter = new SettingPresenter(this);
        // 设置锁屏的界面
        setContentView(R.layout.activity_main);
        // 服务开关的按钮
        lockSwitcher = findViewById(R.id.lock_switcher);
        lockSwitcher.setChecked(LockService.running);
        // 下面的一行，显示是否开启服务
        switcherInfo = findViewById(R.id.lock_switcher_info);
        switcherInfo.setText(getResources().getString(R.string.current_lock_service_opening, LockService.running ? "开启" : "关闭"));
        // 初始化启动
        settingPresenter.start();
        // 根据服务开关的选项 -> 展示是否开启了服务, 根据是否开启服务，来决定跳转界面
        lockSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 更新当前的状态
                settingPresenter.setLockStatus(isChecked);
                switcherInfo.setText(getResources().getString(R.string.current_lock_service_opening,
                        isChecked ? "开启" : "关闭"));
            }
        });

        Button set_bg = findViewById(R.id.set_lock_bg);
        set_bg.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //里面写点击后想要实现的效果
                Intent intent = new Intent(MainActivity.this, SaveImageActivity.class);
                startActivity(intent);

            }
        }));
        float[] DataCoord = new float[150];
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            //启动OpenCV服务
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, getApplicationContext(), mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
}

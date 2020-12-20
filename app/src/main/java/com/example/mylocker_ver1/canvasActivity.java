package com.example.mylocker_ver1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class canvasActivity extends AppCompatActivity implements LockViewManager.LockStatusListener{

    private LockViewManager viewManager = null;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bitmap standardBitmap = HandleStandardBitMap();
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();
        viewManager = LockViewManager.getInstance(canvasActivity.this);
        viewManager.standardBitmap = standardBitmap;
        viewManager.screenWidth = width;
        viewManager.screenHeight = height;
        viewManager.setLockStatusListener(this);
        viewManager.setLockView(LayoutInflater.from(canvasActivity.this).inflate(R.layout.activity_canvas,null));
        viewManager.updateActivity(canvasActivity.this);

//        getWindow().setDecorFitsSystemWindows(false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);

        setContentView(R.layout.activity_canvas);


    }

    @Override
    protected void onStart() {
        super.onStart();
        viewManager.Lock();
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int key = event.getKeyCode();
        return key == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }

    @Override
    public void onLocked() {
    }

    @Override
    public void onUnlock() {
        finish();
    }
    private Bitmap HandleStandardBitMap() {
        @SuppressLint("SdCardPath") String absolutePath = "/data/data/com.example.mylocker_ver1/files";
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(absolutePath+"/standard1.png");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(fis);
//        return BitmapFactory.decodeStream(getClass().getResourceAsStream("/res/drawable-v21/standard.png"));
    }
}

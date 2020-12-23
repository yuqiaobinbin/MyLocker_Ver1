package com.example.mylocker_ver1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import com.example.mylocker_ver1.algorithm.HashCompare;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class LockViewManager {
    private volatile boolean isLock = false;
    public static LockViewManager manager = null;
    private Activity activity;
    private LockStatusListener lockStatusListener;
    private WeakReference<WindowManager> windowManagerRef;
    private static View lockView;
    private static WindowManager.LayoutParams layoutParams;
    public Bitmap standardBitmap;
    private ImageView imageView;
    private Bitmap copyBitmap;
    private Paint paint;
    private Canvas canvas;
    private float startX;
    private float startY;
    public int screenWidth;
    public int screenHeight;
    public LockViewManager(Activity activity) {
        this.activity = activity;
        isLock = false;
        windowManagerRef = new WeakReference<>(activity.getWindowManager());
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        layoutParams.flags = 1280;
        layoutParams.windowAnimations = R.anim.zoom_enter;
    }

    public synchronized void unLock() {
        if (getWindowManager() != null && isLock) {
            getWindowManager().removeView(lockView);
            isLock = false;
            lockStatusListener.onUnlock();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("ClickableViewAccessibility")
    public void setLockView(View v) {
        lockView = v;

        imageView = lockView.findViewById(R.id.iv_image);
        //使用Bitmap工厂把图片加载进来
        Bitmap bitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);

        bitmap.eraseColor(Color.parseColor("#FFFFFF"));//填充颜色
        copyBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        //创建画笔
        paint = new Paint();
        paint.setStrokeWidth(20);
        //创建一个画布
        canvas = new Canvas(copyBitmap);
        //开始画画
        canvas.drawBitmap(bitmap, new Matrix(), paint);
        imageView.setImageBitmap(copyBitmap);

        //图片的触摸事件
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //获取动作的事件
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        //按下事件
                        startX = event.getX();
                        startY = event.getY();
                        Log.e("按下", startX + "," + startY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //滑动事件
                        float x = event.getX();
                        float y = event.getY();
                        //在画布上画直线，不能画点，滑动事件获得的坐标不是连续的
                        canvas.drawLine(startX, startY, x, y, paint);
                        //更新图片
                        imageView.setImageBitmap(copyBitmap);
                        startX = x;
                        startY = y;
                        break;
                    case MotionEvent.ACTION_UP:
                        //抬起事件
                        float upX = event.getX();
                        float upY = event.getY();
                        judgeUnLock();
//                        unLock();
                        break;
                }
                //必须设置为true，否则只执行按下事件

                return true;
            }
        });


    }

    private void judgeUnLock(){
        int judgment = HashCompare.HashCompareFunc(copyBitmap,standardBitmap);

//        List<Float> setList = new ArrayList<>();
//        setList = CoordCompare.ApproximateTransform();
//        int judgment = CoordCompare.Calculate(Coord,setList);

        Log.e("judgment",judgment+"");
        if(judgment <= 15 && judgment != -1) //lock解锁 1：16  2：1000
            unLock();
        else {
//            //加判断条件
            copyBitmap.eraseColor(Color.parseColor("#FFFFFF"));
            imageView.setImageBitmap(copyBitmap);
            Log.e("error" ,"");
        }
    }
    //保存图片

    public void updateActivity(Activity activity_send) {
        activity = activity_send;
    }
    public static synchronized LockViewManager getInstance(Activity activity) {
        if (manager == null)
            manager = new LockViewManager(activity);
        return manager;
    }
    private WindowManager getWindowManager() {
        WindowManager windowManager = windowManagerRef.get();
        if (windowManager == null) {
            windowManager = activity.getWindowManager();
            windowManagerRef = new WeakReference<WindowManager>(windowManager);
        }
        return windowManager;
    }

    public synchronized void Lock(){
        if (isLock)
            getWindowManager().updateViewLayout(lockView, layoutParams);
        else
            getWindowManager().addView(lockView, layoutParams);
        isLock = true;
    }
    public void setLockStatusListener(LockStatusListener listener) {
        this.lockStatusListener = listener;
    }

    public interface LockStatusListener {
        void onLocked();
        void onUnlock();
    }
}

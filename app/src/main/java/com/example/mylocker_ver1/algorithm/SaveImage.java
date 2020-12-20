package com.example.mylocker_ver1.algorithm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.mylocker_ver1.R;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 图像保存
 */
public class SaveImage {
    private volatile boolean isLock = false;
    private static View lockView;
    private ImageView imageView;
    private Bitmap bmp;
    private Paint paint;
    private Canvas canvas;
    private float startX;
    private float startY;
    public int screenWidth;
    public int screenHeight;
    Activity activity;
    public static SaveImage manager = null;

    public SaveImage(Activity activity) {
        this.activity = activity;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setLockBg(View v) {
        lockView = v;

        imageView = lockView.findViewById(R.id.SaveImage);
        //使用Bitmap工厂把图片加载进来
        Bitmap bitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);

        bitmap.eraseColor(Color.parseColor("#FFFFFF"));//填充颜色
        bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        //创建画笔
        paint = new Paint();
        paint.setStrokeWidth(20);
        //创建一个画布
        canvas = new Canvas(bmp);
        //开始画画
        canvas.drawBitmap(bitmap, new Matrix(), paint);
        imageView.setImageBitmap(bmp);

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
                        imageView.setImageBitmap(bmp);
                        startX = x;
                        startY = y;
                        break;
                    case MotionEvent.ACTION_UP:
                        //抬起事件
                        float upX = event.getX();
                        float upY = event.getY();

                        canvas.save();
                        canvas.restore();

                        //文件路径
                        File file = new File("/res/drawable-v21/");
                        if(!file.exists())
                            file.mkdirs();
                        try {
                            FileOutputStream fos = new FileOutputStream(file.getPath() + "/2.png");
                            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            fos.close();
                            System.out.println("saveBmp is here");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
                //必须设置为true，否则只执行按下事件
                return true;
            }
        });
    }
    public static synchronized SaveImage getInstance(Activity activity) {
        if (manager == null)
            manager = new SaveImage(activity);
        return manager;
    }
}

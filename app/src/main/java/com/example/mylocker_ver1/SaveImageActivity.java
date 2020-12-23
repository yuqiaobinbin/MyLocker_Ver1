package com.example.mylocker_ver1;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mylocker_ver1.algorithm.SaveImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class SaveImageActivity extends Activity {
    private ImageView imageView;
    private Bitmap copyBitmap;
    private Paint paint;
    private Canvas canvas;
    private float startX;
    private float startY;

    

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_iamge);

        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay(); // 为获取屏幕宽、高
        android.view.WindowManager.LayoutParams p = getWindow().getAttributes();
        int height = (int) (d.getHeight() * 0.7); // 高度设置为屏幕的0.7
        int width = (int) (d.getWidth() * 0.7); // 宽度设置为屏幕的0.7
        getWindow().setAttributes(p);

        imageView = findViewById(R.id.SaveImage);
        //使用Bitmap工厂把图片加载进来
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        bitmap.eraseColor(Color.parseColor("#FFFFFF"));//填充颜色
        //创建一个空的图片，宽度和高度 还有信息跟原图片一样
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
            @SuppressLint({"ClickableViewAccessibility", "SdCardPath"})
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

                        Log.e("抬起", upX + "," + upY);
                        saveBitmap(getApplicationContext(),copyBitmap);
                        Toast.makeText(getApplicationContext(), "设置成功", Toast.LENGTH_SHORT).show();
                        finish();
                }
                //必须设置为true，否则只执行按下事件
                return true;
            }
        });

    }
    public static String saveBitmap(Context context, Bitmap mBitmap) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            savePath = "/data/data/com.example.mylocker_ver1/files";
        } else {
            savePath = context.getApplicationContext().getFilesDir()
                    .getAbsolutePath()
                    + "/pic/";
        }
        try {
            filePic = new File(savePath , "standard1.png");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return filePic.getAbsolutePath();
    }

    /**
     * FileChannel通道写入float数据
     *
     * @param verts float数组
     * @param gcodeFile 文件名
     * @param count 一次传输的大小
     */
    public static void writeFloatToData(float[] verts, String gcodeFile, int count) {
        try {
            RandomAccessFile aFile = new RandomAccessFile(gcodeFile, "rw");
            FileChannel outChannel = aFile.getChannel();
            ByteBuffer buf = ByteBuffer.allocate(4 * count * 3 * 3);
            buf.clear();
            buf.asFloatBuffer().put(verts);
            {
                outChannel.write(buf);
            }
            buf.rewind();
            outChannel.close();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * FileChannel通道读出float数据
     *
     * @param gcodeFile 文件名
     * @param Count 一次传输的大小
     * @return float数组
     */
    public static float[] readFloatFromData(String gcodeFile, int Count) {
        float[] verts = new float[Count * 3 * 3];
        try {
            RandomAccessFile rFile = new RandomAccessFile(gcodeFile, "rw");
            FileChannel inChannel = rFile.getChannel();
            ByteBuffer buf_in = ByteBuffer.allocate(3 * 3 * Count * 4);
            buf_in.clear();
            inChannel.read(buf_in);
            buf_in.rewind();
            buf_in.asFloatBuffer().get(verts);
            inChannel.close();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return verts;
    }
}
package com.example.mylocker_ver1.algorithm;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;

import static org.opencv.imgproc.Imgproc.cvtColor;

public class aHash {

    /**
     * aHash算法
     *
     * @param srcMat1 图像1的Mat类型数据
     * @param srcMat2 图像2的Mat类型数据
     * @return 两个图像的差异值
     */
    public static int calculateByAHash(Mat srcMat1, Mat srcMat2) {
        Mat dst1 = new Mat();
        Mat dst2 = new Mat();

        //图像变灰度图，四通道变一通道，ARGB->G（第一个G是Green,第二个是Gray）
        cvtColor(srcMat1, dst1, Imgproc.COLOR_BGR2GRAY);
        cvtColor(srcMat2, dst2, Imgproc.COLOR_BGR2GRAY);

        //将MAT转回Bitmap类型
        Bitmap dst1bmp = Bitmap.createBitmap(dst1.cols(), dst1.rows(), Bitmap.Config.ARGB_8888);
        Bitmap dst2bmp = Bitmap.createBitmap(dst2.cols(), dst2.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dst1, dst1bmp);
        Utils.matToBitmap(dst2, dst2bmp);

        //图像缩放
        AreaAveragingScale imgPre = new AreaAveragingScale(dst1bmp);
        AreaAveragingScale img2Pre = new AreaAveragingScale(dst2bmp);
        dst1bmp = imgPre.getScaledBitmap(8,8);
        dst2bmp = img2Pre.getScaledBitmap(8,8);

        //把灰度图图缩成8*8（效果不佳）
//        resize(dst1, dst1, new Size(8, 8), 0, 0, INTER_CUBIC);
//        resize(dst2, dst2, new Size(8, 8), 0, 0, INTER_CUBIC);
//        dst1bmp = scaleBitmap(dst1bmp,false,8);
//        dst2bmp = scaleBitmap(dst2bmp,false,8);
        Utils.bitmapToMat(dst1bmp, dst1);
        Utils.bitmapToMat(dst2bmp, dst2);

        double[][] data1 = new double[64][1];
        double[][] data2 = new double[64][1];
        //Avg 记录平均像素灰度值，arr记录像素灰度值，data用来取灰度值。
        double Avg1 = 0, Avg2 = 0;
        double[] arr1 = new double[64];
        double[] arr2 = new double[64];

        //二维转一维，data二维数组转arr一维数组
        for (int i = 0; i < 8; i++) {
            int tmp = i * 8;
            for (int j = 0; j < 8; j++) {
                int tmp1 = tmp + j;
                data1[tmp1] = dst1.get(i, j);
                data2[tmp1] = dst2.get(i, j);
                arr1[tmp1] = data1[tmp1][0];
                arr2[tmp1] = data2[tmp1][0];
                Avg1 += arr1[tmp1];
                Avg2 += arr2[tmp1];
            }
        }
        Avg1 /= 64;
        Avg2 /= 64;


        //比对每个像素灰度值和平均灰度值大小
        for (int i = 0; i < 64; i++) {
            arr1[i] = (arr1[i] >= Avg1) ? 1 : 0;
            arr2[i] = (arr2[i] >= Avg2) ? 1 : 0;
        }
//        Log.e("standard.jpg ", Arrays.toString(arr1)+"");

        //计算差异值
        int AHashdifferent = 0;
        for (int i = 0; i < 64; i++)
            if (arr1[i] != arr2[i])
                ++AHashdifferent;
        return AHashdifferent;
    }
}

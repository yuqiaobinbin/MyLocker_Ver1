package com.example.mylocker_ver1.algorithm;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.cvtColor;

public class HashCompare {
    public static int HashCompareFunc(Bitmap Bp1, Bitmap Bp2) {

        //数据定义导入部分
        Mat src1 = new Mat();
        Mat dst1 = new Mat();
        Mat src2 = new Mat();
        Mat dst2 = new Mat();

        //读取位图到MAT
        Utils.bitmapToMat(Bp1, src1);
        Utils.bitmapToMat(Bp2, src2);
        src1 = detectColoredBlob(src1);
        src2 = detectColoredBlob(src2);

        if(src1==null || src2==null)return 100 ;

        //变ARGB变灰度图，四通道变一通道
        cvtColor(src1, dst1, Imgproc.COLOR_BGR2GRAY);
        cvtColor(src2, dst2, Imgproc.COLOR_BGR2GRAY);


        Bitmap dst1bmp = null,dst2bmp = null;
        //Imgproc.cvtColor(seedsImage, tmp, Imgproc.COLOR_RGB2BGRA);
        dst1bmp = Bitmap.createBitmap(dst1.cols(), dst1.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dst1, dst1bmp);
        dst2bmp = Bitmap.createBitmap(dst2.cols(), dst2.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dst2, dst2bmp);

        AreaAveragingScale imgPre = new AreaAveragingScale(dst1bmp);
        AreaAveragingScale img2Pre = new AreaAveragingScale(dst2bmp);
        dst1bmp = imgPre.getScaledBitmap(8,8);
        dst2bmp = img2Pre.getScaledBitmap(8,8);
        Utils.bitmapToMat(dst1bmp, dst1);
        Utils.bitmapToMat(dst2bmp, dst2);

        //把灰度图图缩成8*8
//        resize(dst1, dst1, new Size(8, 8), 0, 0, INTER_CUBIC);
//        resize(dst2, dst2, new Size(8, 8), 0, 0, INTER_CUBIC);

        //核心算法部分
        //这里变成二维数组才可以用Mat.get(row,cul)去获取，二维是因为每个像素点里面可能有很多属性（ARGB）
        // 变成灰度之后就只有一个G了，这个G是Gray，前面那个G是Green。
        double[][] data1 = new double[64][1];
        double[][] data2 = new double[64][1];
        //iAvg 记录平均像素灰度值，arr记录像素灰度值，data是个跳板。
        double iAvg1 = 0, iAvg2 = 0;
        double[] arr1 = new double[64];
        double[] arr2 = new double[64];
        //get灰度给data，用data给arr充值，算平均灰度值iAvg。
        for (int i = 0; i < 8; i++) {
            int tmp = i * 8;
            for (int j = 0; j < 8; j++) {
                int tmp1 = tmp + j;
                data1[tmp1] = dst1.get(i, j);
                data2[tmp1] = dst2.get(i, j);
                arr1[tmp1] = data1[tmp1][0];
                arr2[tmp1] = data2[tmp1][0];
                iAvg1 += arr1[tmp1];
                iAvg2 += arr2[tmp1];

            }
        }
        iAvg1 /= 64;
        iAvg2 /= 64;
        //比对每个像素灰度值和平均灰度值大小
        for (int i = 0; i < 64; i++) {
            arr1[i] = (arr1[i] >= iAvg1) ? 1 : 0;
            arr2[i] = (arr2[i] >= iAvg2) ? 1 : 0;
        }
        //计算差异值
        int iDiffNum = 0;
        for (int i = 0; i < 64; i++)
            if (arr1[i] != arr2[i])
                ++iDiffNum;
        Log.d("difference", String.valueOf(iDiffNum));
        return iDiffNum;
    }

    private static Mat detectColoredBlob(Mat rgbaFrame) {

        // 颜色变黑白
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(rgbaFrame, hsvImage, Imgproc.COLOR_RGB2GRAY);

        // 反转黑白
        Mat thresh = new Mat();
        Imgproc.threshold(hsvImage, thresh, 30, 255, Imgproc.THRESH_BINARY_INV);


        List<MatOfPoint> contours = new ArrayList<>();
        //contours参数为检测的轮廓数组，每一个轮廓用一个MatOfPoint类型的List表示
        Imgproc.findContours(thresh, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        if(contours.size() == 0) return null;
        MatOfPoint largestContour = contours.get(0);
        double largestContourArea = Imgproc.contourArea(largestContour);
        for (int i = 1; i < contours.size(); ++i) {// NB Notice the prefix increment.
            MatOfPoint currentContour = contours.get(i);
            double currentContourArea = Imgproc.contourArea(currentContour);
            if (currentContourArea > largestContourArea) {
                largestContourArea = currentContourArea;
                largestContour = currentContour;
            }
        }
        Rect detectedBlobRoi = Imgproc.boundingRect(largestContour);
        Mat returnData = rgbaFrame.submat(detectedBlobRoi);
//        HighGui.imshow("image",returnData); //显示
//        HighGui.waitKey(0);
        return returnData;
    }
}

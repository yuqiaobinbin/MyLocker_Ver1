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

/**
 * 均值、感知、差值哈希算法的调用与对比
 */
public class HashCompare {

    /**
     * 哈希算法函数
     *
     * @param bitmap1 图像1的Bitmap类型数据
     * @param bitmap2 图像2的Bitmap类型数据
     * @return 哈希对比的差异值
     */
    public static int HashCompareFunc(Bitmap bitmap1, Bitmap bitmap2){
        Mat src1 = new Mat();
        Mat dst1 = new Mat();
        Mat src2 = new Mat();
        Mat dst2 = new Mat();

        //Bitmap转MAT(opencv处理的图像格式为MAT)
        Utils.bitmapToMat(bitmap1, src1);
        Utils.bitmapToMat(bitmap2, src2);

        //opencv截取最大外接矩阵
        src1 = detectColoredBlob(src1);
        src2 = detectColoredBlob(src2);

        if(src1==null || src2==null)return -1; //图像为空即出错

        //MAT转Bitmap
        Bitmap dst_1 = null,dst_2 = null;
        dst_1 = Bitmap.createBitmap(src1.cols(), src1.rows(), Bitmap.Config.ARGB_8888);
        dst_2 = Bitmap.createBitmap(src2.cols(), src2.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(src1, dst_1);
        Utils.matToBitmap(src2, dst_2);

        //ahash对比
        int AHashDifference = aHash.calculateByAHash(src1,src2);
        Log.e("ahash's difference",AHashDifference+" ");

        //phash对比
        int PHashDifference = pHash.calculateByPHash(dst_1,dst_2);
        Log.e("phash's difference",PHashDifference+" ");

        //dhash对比
        int DHashDifference = dHash.calculateByDHash(dst_1,dst_2);
        Log.e("dhash's difference",DHashDifference+" ");
        return DHashDifference;
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
        return returnData;
    }
}

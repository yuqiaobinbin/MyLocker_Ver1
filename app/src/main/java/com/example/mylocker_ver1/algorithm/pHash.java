package com.example.mylocker_ver1.algorithm;

import android.graphics.Bitmap;
import android.graphics.Color;

public class pHash {

    /**
     * pHash算法
     *
     * @param bitmap1 图像1的Bitmap类型数据
     * @param bitmap2 图像2的Bitmap类型数据
     * @return 两个图像的差异值
     */
    public static int calculateByPHash(Bitmap bitmap1, Bitmap bitmap2) {
        long hash1 = pHash.dctImageHash(bitmap1);
        long hash2 = pHash.dctImageHash(bitmap2);
        int PHashDiff = hammingDistance(hash1, hash2);
        return PHashDiff;
    }

    //获取指纹，long刚好64位，方便存放
    public static long dctImageHash(Bitmap src) {
        //由于计算dct需要图片长宽相等，所以统一取32
        int length = 32;

        //缩放图片
        AreaAveragingScale imgPre = new AreaAveragingScale(src);
        Bitmap bitmap = imgPre.getScaledBitmap(length,length);

        //获取灰度图
        int[] pixels = createGrayImage(bitmap, length);

        //先获得32*32的dct，再取dct左上角8*8的区域
        return computeHash(DCT8(pixels, length));
    }

    private static int[] createGrayImage(Bitmap src, int length) {
        int[] pixels = new int[length * length];
        src.getPixels(pixels, 0, length, 0, 0, length, length);
        src.recycle();
        for (int i = 0; i < pixels.length; i++) {
            int gray = computeGray(pixels[i]);
            pixels[i] = Color.rgb(gray, gray, gray);
        }
        return pixels;
    }

    //计算hash值
    private static long computeHash(double[] pxs) {
        double t = 0;
        for (double i : pxs) {
            t += i;
        }
        double median = t / pxs.length;
        long one = 0x0000000000000001;
        long hash = 0x0000000000000000;
        for (double current : pxs) {
            if (current > median)
                hash |= one;
            one = one << 1;
        }
        return hash;
    }

    //计算灰度值
    private static int computeGray(int pixel) {
        int red = Color.red(pixel);
        int green = Color.green(pixel);
        int blue = Color.blue(pixel);
        //计算公式Gray = R*0.299 + G*0.587 + B*0.114
        return (red * 38 + green * 75 + blue * 15) >> 7;
    }

    //取dct图左上角8*8的区域
    private static double[] DCT8(int[] pix, int n) {
        double[][] iMatrix = DCT(pix, n);

        double px[] = new double[8 * 8];
        for (int i = 0; i < 8; i++) {
            System.arraycopy(iMatrix[i], 0, px, i * 8, 8);
        }
        return px;
    }

    /**
     * 离散余弦变换
     * 计算公式为：系数矩阵*图片矩阵*转置系数矩阵
     *
     * @param pix 原图像的数据矩阵
     * @param n   原图像矩阵(n*n)
     * @return 变换后的矩阵数组
     */
    private static double[][] DCT(int[] pix, int n) {
        double[][] iMatrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                iMatrix[i][j] = (double) (pix[i * n + j]);
            }
        }
        double[][] quotient = coefficient(n);   //求系数矩阵
        double[][] quotientT = transposingMatrix(quotient, n);  //转置系数矩阵

        double[][] temp;
        temp = matrixMultiply(quotient, iMatrix, n);
        iMatrix = matrixMultiply(temp, quotientT, n);
        return iMatrix;
    }

    /**
     * 矩阵转置
     *
     * @param matrix 原矩阵
     * @param n      矩阵(n*n)
     * @return 转置后的矩阵
     */
    private static double[][] transposingMatrix(double[][] matrix, int n) {
        double nMatrix[][] = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                nMatrix[i][j] = matrix[j][i];
            }
        }
        return nMatrix;
    }

    /**
     * 求离散余弦变换的系数矩阵
     *
     * @param n n*n矩阵的大小
     * @return 系数矩阵
     */
    private static double[][] coefficient(int n) {
        double[][] coeff = new double[n][n];
        double sqrt = Math.sqrt(1.0 / n);
        double sqrt1 = Math.sqrt(2.0 / n);
        for (int i = 0; i < n; i++) {
            coeff[0][i] = sqrt;
        }
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < n; j++) {
                coeff[i][j] = sqrt1 * Math.cos(i * Math.PI * (j + 0.5) / n);
            }
        }

        return coeff;
    }

    /**
     * 矩阵相乘
     *
     * @param A 矩阵A
     * @param B 矩阵B
     * @param n 矩阵的大小n*n
     * @return 结果矩阵
     */
    private static double[][] matrixMultiply(double[][] A, double[][] B, int n) {
        double nMatrix[][] = new double[n][n];
        double t;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                t = 0;
                for (int k = 0; k < n; k++) {
                    t += A[i][k] * B[k][j];
                }
                nMatrix[i][j] = t;
            }
        }
        return nMatrix;
    }

    /**
     * 计算两个图片指纹的汉明距离
     *
     * @param hash1 指纹1
     * @param hash2 指纹2
     * @return 返回汉明距离
     */
    public static int hammingDistance(long hash1, long hash2) {
        long x = hash1 ^ hash2;
        final long m1 = 0x5555555555555555L;
        final long m2 = 0x3333333333333333L;
        final long h01 = 0x0101010101010101L;
        final long m4 = 0x0f0f0f0f0f0f0f0fL;
        x -= (x >> 1) & m1;
        x = (x & m2) + ((x >> 2) & m2);
        x = (x + (x >> 4)) & m4;
        return (int) ((x * h01) >> 56);
    }
}
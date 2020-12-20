package com.example.mylocker_ver1.algorithm;

import android.graphics.Bitmap;

public class dHash {

    /**
     * dHash算法
     *
     * @param bitmap1 图像1的Bitmap类型数据
     * @param bitmap2 图像2的Bitmap类型数据
     * @return 两个图像的差异值
     */
    public static int calculateByDHash(Bitmap bitmap1, Bitmap bitmap2) {
        String hash1 = getDHash(bitmap1);
        String hash2 = getDHash(bitmap2);
        int DHashDiff = hammingDistance(hash1, hash2);
        return DHashDiff;
    }

    private static String getDHash(Bitmap bitmap) {
        //缩小尺寸
        AreaAveragingScale imgPre = new AreaAveragingScale(bitmap);
        bitmap = imgPre.getScaledBitmap(9,8);
        //获取图片数组
        int[] p = bitmap2intArray(bitmap);
        //灰度化
        p = Grey(p, 9, 8);
        //计算灰度差值
        String result = ImgFingerprint(p);
        return result;
    }

    //Bitmap类型转一维数组
    public static int[] bitmap2intArray(Bitmap bitmap) {
        //获取位图的宽、高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //通过位图的大小创建像素点数组
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;
    }

    public static boolean compareGrey(int current, int next) {
        if (current > next) {
            return true;
        }
        return false;
    }

    //计算灰度值
    public static int[] Grey(int[] pixels, int width, int height) {
        int[] result = new int[pixels.length];
        int alpha = 0xFF << 24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];

                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);

                grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                result[width * i + j] = grey;
            }
        }
        return result;
    }

    //获取图像指纹
    public static String ImgFingerprint(int[] pixels) {
        StringBuilder stringBuilder = new StringBuilder();
        //遍历9*8像素点，记录相邻像素之间的大小关系，产生8*8=64个对比
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boolean d = compareGrey(pixels[9 * j + i], pixels[9 * j + i + 1]);
                if (d) {
                    stringBuilder.append("1");
                } else {
                    stringBuilder.append("0");
                }
            }
        }

        return stringBuilder.toString();
    }

    /**
     * 计算两个图片指纹的汉明距离
     *
     * @param s1 指纹1
     * @param s2 指纹2
     * @return 返回汉明距离
     */
    public static int hammingDistance(String s1, String s2) {
        int DHashdifferent = 0;
        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                DHashdifferent++;
            }
        }
        return DHashdifferent;
    }
}

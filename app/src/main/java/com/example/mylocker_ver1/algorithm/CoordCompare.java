package com.example.mylocker_ver1.algorithm;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.mylocker_ver1.SaveImageActivity;

import java.util.ArrayList;
import java.util.List;

public class CoordCompare {
    private static List<Float> list;

    @SuppressLint("SdCardPath")
    public static List<Float> ApproximateTransform(){
        float[] Coord = new float[500];
        list = new ArrayList<>();
        Coord = SaveImageActivity.readFloatFromData("/data/data/com.example.mylocker_ver1/files/coorddata.txt",100);
        for (float ele : Coord) {
            if(ele == 0.0) break;
            list.add(ele);
        }
//        Log.e("list1:长度 ",list.size()+" 内容： "+list+"第一个元素" + list.get(0));

        for(int i = 1; i < list.size()-4; ){
            double sub = Math.abs(list.get(i) - list.get(i+2));
            double sub2 = Math.abs(list.get(i+1) - list.get(i+3));

            if (sub < 10 && sub2 < 10){
                list.remove(i+2);
                list.remove(i+2);
            }
            else i += 2;
        }
        list.set(0,(float)list.size());
        Log.e("修改后的List1: ", list +"" );
        return list;
    }

    /**
     * 坐标点匹配对比算法
     *
     * @param StandardImgCoord 标准图像的坐标数组
     * @param NewPrintImgCoord 锁屏解锁图像坐标集合
     * @return 比对差异值
     */
    public static int Calculate(float[] StandardImgCoord, List<Float> NewPrintImgCoord){
        List<Float> listCoord = new ArrayList<>();
        int j = 1, flag = 0, Difference;
        //数组转集合
        for (float ele : StandardImgCoord) {
            if(ele == 0.0) break;
            listCoord.add(ele);
        }
        Log.e("listCoord:长度 ",listCoord.size()+" 内容： "+listCoord);

        for(int i = 1; i < listCoord.size()-4; ){
            double sub = Math.abs(listCoord.get(i) - listCoord.get(i+2));
            double sub2 = Math.abs(listCoord.get(i+1) - listCoord.get(i+3));

            //剔除相似度过高的点集
            if (sub < 10 && sub2 < 10){
                listCoord.remove(i+2);
                listCoord.remove(i+2);
            }
            else i += 2;
        }

        listCoord.set(0,(float)listCoord.size());   //首位值设置为坐标点的数量
        Log.e("listCoord: ", listCoord +"" );

        float num1 = NewPrintImgCoord.get(0);
        float num2 = listCoord.get(0);
        flag = (int)(Math.min(num1, num2))/2 - 1;
        double Different = 0;

        //坐标对比
        Log.e("flag: ", flag +"" );
        while (flag != 0){
            if(Math.abs(num1 - num2)>18) return -1;
            double DiffX = NewPrintImgCoord.get(j) - listCoord.get(j);
            double DiffY = NewPrintImgCoord.get(j+1) - listCoord.get(j+1);

            //差异值为两个坐标的距离值
            Different += Math.sqrt(Math.pow(DiffX,2) + Math.pow(DiffY,2));
            j+=2;
            flag--;
        }
        Difference = (int)Different;
        return Difference;
    }

}

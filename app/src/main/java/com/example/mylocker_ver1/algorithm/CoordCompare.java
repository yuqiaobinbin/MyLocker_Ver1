package com.example.mylocker_ver1.algorithm;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.mylocker_ver1.SaveImageActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CoordCompare {
    private static List<Float> list;

    @SuppressLint("SdCardPath")
    public static List<Float> ApproximateTransform(){
        float[] Coord = new float[150];
        list = new ArrayList<>();
        Coord = SaveImageActivity.readFloatFromData("/data/data/com.example.mylocker_ver1/files/coorddata.txt",100);
        for (float ele : Coord) {
            if(ele == 0.0) break;
            list.add(ele);
        }
        Log.e("list1:长度 ",list.size()+" 内容： "+list+"第一个元素" + list.get(0));

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

    public static int Calculate(float[] coord, List<Float> list){
        List<Float> list2 = new ArrayList<>();

        for (float ele : coord) {
            if(ele == 0.0) break;
            list2.add(ele);
        }
        Log.e("list2:长度 ",list2.size()+" 内容： "+list2);

        for(int i = 1; i < list2.size()-4; ){
            double sub = Math.abs(list2.get(i) - list2.get(i+2));
            double sub2 = Math.abs(list2.get(i+1) - list2.get(i+3));
            if (sub < 10 && sub2 < 10){
                list2.remove(i+2);
                list2.remove(i+2);
            }
            else i += 2;
        }

        list2.set(0,(float)list2.size());
        Log.e("修改后的List2: ", list2 +"" );
        int i = 1, flag = 0, Diff;
        float num1 = list.get(0);
        float num2 = list2.get(0);
        flag = (int)(Math.min(num1, num2))/2 - 1;
        double Different = 0;

        //对比
        Log.e("flag: ", flag +"" );
        while (flag != 0){

            if(Math.abs(num1 - num2)>18) return 9999999;

            double DiffX = list.get(i) - list2.get(i);
            double DiffY = list.get(i+1) - list2.get(i+1);
            Different += Math.pow(DiffX,2) + Math.pow(DiffY,2);
            i+=2;
            flag--;
        }
        Diff = (int)Different;
        return Diff;
    }

}

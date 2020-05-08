package com.keith.common.utils;

import android.util.Log;

/**
 * Created by KeithLee on 2020/4/30.
 * Introduction:全局打印的工具类
 */
public class KLogger {
    private static boolean sELog;
    private static boolean sDLog;
    private static boolean sILog;

    public static void e(String str) {
        if (sELog) {
            Log.e("Keith", "KeithLog e() :  _  " + str);
        }
    }

    public static void d(String str) {
        if (sDLog) {
            Log.d("Keith", "KeithLog d() :  _  " + str);
        }
    }

    public static void i(String str) {
        if (sILog) {
            Log.i("Keith", "KeithLog i() :  _  " + str);
        }
    }

    public static void enableLog(boolean enable) {
        sELog = enable;
        sDLog = enable;
        sILog = enable;
    }
}

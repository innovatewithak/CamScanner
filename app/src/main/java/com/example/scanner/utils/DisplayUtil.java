package com.example.scanner.utils;

import android.content.Context;

public class DisplayUtil {

    public static int px2dp(Context context, float pxValue){
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue/density+0.5f);
    }
    public static int dp2px(Context context, float dpValue){
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue*density+0.5f);
    }
    public static int px2sp(Context context, float pxValue){
        float scaleDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue/scaleDensity+0.5f);
    }
    public static int sp2px(Context context, float spValue) {
        float scaleDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue*scaleDensity+0.5f);
    }
}

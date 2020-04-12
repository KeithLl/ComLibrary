package com.keith.baselibrary.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;

/**
 * Created by KeithLee on 17/03/30.
 * Introduction:状态栏的颜色修改的兼容工具类
 */

public class StatusBarCompat {

    //statusBar的默认颜色(黑色)
    private static final int COLOR_DEFAULT = Color.parseColor("#000000");

    /**
     * 状态栏兼容的方法,如果需要内容延伸到状态栏的情况,statusColor设置为Color.TRANSPARENT
     *
     * @param activity    对应的activity对象
     * @param statusColor 状态栏颜色
     */
    public static void compat(Activity activity, int statusColor) {

        //对于Android5.0以上系统,可以直接设置状态栏的颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            //默认Color.TRANSPARENT表示延伸到状态栏
            if (statusColor == Color.TRANSPARENT) {
                /*
                 * 如果是透明状态,表示内容延伸到状态栏,5.0以上View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                 * 和 View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN需要手动设置
                 */
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View
                        .SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            } else {
                /*
                 * 设置contentLayout的第一个元素fitsSystemWindows属性为true,不让它延伸到状态栏
                 */
                ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
                if (statusColor != Color.TRANSPARENT) {
                    View contentChild = contentView.getChildAt(0);
                    contentChild.setFitsSystemWindows(true);
                }
            }

            //5.0以上需要配合FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS使用,且不能设置FLAG_TRANSLUCENT_STATUS
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            //设置status bar 的背景颜色
            activity.getWindow().setStatusBarColor(statusColor);

            //设置状态栏图标颜色
            setDarkIcon(activity, isLightColor(statusColor));
            return;
        }

        /**
         * 兼容到4.4,需要添加一个和状态栏一样高度的布局在contentLayout中的第一个元素,并设置背景颜色
         * 如果内容为图片等需要延伸到状态栏,则不设置fitSystemWindow
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES
                .LOLLIPOP) {

            /*
             * 状态栏透明
             * WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS，Added in API level 19，如果这个flag被设置，View
             * .SYSTEM_UI_FLAG_LAYOUT_STABLE 和 View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN 这两个flag会被自动添加到
             * system UI visibility中。
             * 如果这里不设置则需要在values-v19中设置一个appTheme,values-v23设一个空
             * <style name="AppTheme" parent="@style/BaseAppTheme">
             * <item name="android:windowTranslucentStatus">true</item>
             * </style>
             */
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            /*
             * navigation透明
             * WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION，Added in API level 19，如果这个flag被设置，View
             * .SYSTEM_UI_FLAG_LAYOUT_STABLE 和 View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION 这两个flag会被自动添加到
             * system UI visibility中。
             */
//            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            /*
             * 添加一个跟status bar一样高度的布局
             */
            ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
            View statusBarView = new View(activity);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    getStatusBarHeight(activity));
            statusBarView.setBackgroundColor(statusColor);
            contentView.addView(statusBarView, lp);

            /*
             * 因为4.4设置FLAG_TRANSLUCENT_STATUS会默认设置全屏,因此需要设置contentLayout的第一个元素
             * fitsSystemWindows属性为true,不让它延伸到状态栏
             */
            if (statusColor != Color.TRANSPARENT) {
                View contentChild = contentView.getChildAt(0);
                contentChild.setFitsSystemWindows(true);
            }

            //设置状态栏颜色
            setDarkIcon(activity, isLightColor(statusColor));
            return;
        }
    }

    /**
     * 默认兼容(状态栏透明),会调用本类中的另一个方法,statusColor传入默认值
     *
     * @param activity 将要设置内容的activity
     */
    public static void compat(Activity activity) {
        compat(activity, COLOR_DEFAULT);
    }

    /**
     * 设置状态栏颜色是否为黑色
     *
     * @param activity     activity
     * @param needDarkIcon 是否为黑色字体
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static void setDarkIcon(Activity activity, boolean needDarkIcon) {
        //小米修改状态栏字体
        setMIUIStatusBarDarkIcon(activity, needDarkIcon);
        //魅族修改状态栏字体
        setMeiZuStatusBarDarkIcon(activity, needDarkIcon);

        //6.0以上设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (needDarkIcon) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            } else {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        }
    }

    /**
     * 获取状态栏的高度
     *
     * @param context 上下文
     * @return 状态栏的高度值
     */
    private static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    /**
     * 修改 MIUI V6  以上状态栏颜色
     */
    private static void setMIUIStatusBarDarkIcon(@NonNull Activity activity, boolean darkIcon) {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            int darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkIcon ? darkModeFlag : 0, darkModeFlag);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    /**
     * 修改魅族状态栏字体颜色 Flyme 4.0
     */
    private static void setMeiZuStatusBarDarkIcon(@NonNull Activity activity, boolean darkIcon) {
        try {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (darkIcon) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(lp, value);
            activity.getWindow().setAttributes(lp);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    /**
     * 判断颜色是不是亮色,判断条件是>=0.5
     *
     * @param color
     * @return 是否为亮色
     * @from https://stackoverflow.com/questions/24260853/check-if-color-is-dark-or-light-in-android
     */
    private static boolean isLightColor(@ColorInt int color) {
        return ColorUtils.calculateLuminance(color) >= 0.5;
    }
}

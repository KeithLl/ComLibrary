package com.keith.comlibrary;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.keith.baselibrary.utils.NumberUtils;
import com.keith.baselibrary.utils.StatusBarCompat;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //适配状态栏颜色,并且设置背景字体颜色
        StatusBarCompat.compat(this, Color.WHITE);

        for (int i = 0; i < 1000; i++) {
            Log.e("Keith", "MainActivity onCreate() :  _  " + NumberUtils.formatNumber(i));
        }
    }
}

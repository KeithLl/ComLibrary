package com.keith.common.provider;

import android.app.Application;
import android.content.Context;

/**
 * Created by KeithLee on 2020/4/30.
 * Introduction:全局获取Context的类
 */
public class KContextProvider {
    @SuppressWarnings("StaticFieldLeak")
    private static volatile KContextProvider sInstance;
    private Context mContext;

    //构造函数初始化
    private KContextProvider(Context context) {
        this.mContext = context;
    }

    public static KContextProvider get() {
         KContextProvider temp = sInstance;
        if (temp == null) {
            synchronized (KContextProvider.class) {
                if (temp == null) {
                    Context context = ApplicationContentProvider.mContext;
                    if (context == null) {
                        throw new IllegalArgumentException("context is null");
                    }
                    temp = new KContextProvider(context);
                    sInstance = temp;
                }
            }
        }
        return sInstance;
    }

    //获取application
    public Application getApplicationContext() {
        return (Application) mContext.getApplicationContext();
    }

    //获取全局context
    public Context getContext() {
        return mContext;
    }
}

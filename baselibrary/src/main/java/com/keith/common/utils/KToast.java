package com.keith.common.utils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.keith.baselibrary.R;
import com.keith.common.provider.KContextProvider;

import java.lang.ref.SoftReference;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;

/**
 * Created by KeithLee on 2020/7/10.
 * Introduction:Toast类封装
 */
public class KToast {

    //构造方法私有
    private KToast() throws IllegalAccessException {
        throw new IllegalAccessException("Cannot init this by invoke KToast()");
    }

    //Toast的实例
    private static SoftReference<Toast> mToast;

    //判断是否不为空
    private static boolean checkInstance() {
        return mToast != null && mToast.get() != null;
    }

    //返回builder
    public static Builder get() {
        return new Builder();
    }

    //定义的对齐方式
    public enum Gravity {
        CENTER, BOTTOM
    }

    //定义正确状态
    public enum Status {
        SUCCESS, FAILED, WARNING
    }

    private static class Builder {

        private Gravity mGravity;
        private Status mStatus;
        @LayoutRes
        private int mLayoutId;
        @DrawableRes
        private int mImageId;
        private int mYOffset;
        private boolean mInvokeYOffset;
        private int mDuration;

        Builder() {
        }

        public void show(String msg) {
            //消息为空不显示
            if (TextUtils.isEmpty(msg)) {
                return;
            }

            //如果有显示则先取消
            if (checkInstance()) {
                mToast.get().cancel();
            }

            //创建Toast
            Toast toast = new Toast(KContextProvider.get().getApplicationContext());

            //设置位置
            if (Gravity.CENTER == mGravity) {
                toast.setGravity(android.view.Gravity.CENTER, 0, mYOffset);
            } else {
                //没有调用设置间距则默认50
                toast.setGravity(android.view.Gravity.BOTTOM, 0, !mInvokeYOffset ? 50 : mYOffset);
            }

            //设置时间
            toast.setDuration(Toast.LENGTH_SHORT);

            if (mLayoutId == 0) {
                //加载布局
                View inflate = LayoutInflater.from(KContextProvider.get().getApplicationContext())
                        .inflate(R.layout.layout_toast_default, null);
                ImageView mIvToast = inflate.findViewById(R.id.iv_toast);
                TextView mTvToast = inflate.findViewById(R.id.tv_toast);

                //显示图片数据,优先ImageId
                if (mImageId != 0) {
                    mIvToast.setVisibility(View.VISIBLE);
                    mIvToast.setImageResource(mImageId);
                } else if (null == mStatus) {
                    mIvToast.setVisibility(View.GONE);
                } else if (Status.SUCCESS == mStatus) {
                    mIvToast.setVisibility(View.VISIBLE);
                    mIvToast.setImageResource(R.drawable.toast_pic_complete);
                } else if (Status.FAILED == mStatus) {
                    mIvToast.setVisibility(View.VISIBLE);
                    mIvToast.setImageResource(R.drawable.toast_pic_error);
                } else if (Status.WARNING == mStatus) {
                    mIvToast.setVisibility(View.VISIBLE);
                    mIvToast.setImageResource(R.drawable.toast_pic_warning);
                }

                //显示文字
                mTvToast.setText(msg);
                toast.setView(inflate);
            } else {
                //加载布局
                View inflate = LayoutInflater.from(KContextProvider.get().getApplicationContext())
                        .inflate(mLayoutId, null);
                toast.setView(inflate);
            }

            //赋值新的引用
            mToast = new SoftReference<>(toast);
            toast.show();
        }

        public Builder gravity(Gravity gravity) {
            mGravity = gravity;
            return this;
        }

        public Builder status(Status status) {
            mStatus = status;
            return this;
        }

        public Builder layoutId(@LayoutRes int layoutId) {
            this.mLayoutId = layoutId;
            return this;
        }

        public Builder imageId(@DrawableRes int imageId) {
            this.mImageId = imageId;
            return this;
        }

        public Builder duration( int duration) {
            this.mDuration = duration;
            return this;
        }

        public Builder yOffset(int yOffset) {
            mInvokeYOffset = true;
            mYOffset = yOffset;
            return this;
        }
    }
}

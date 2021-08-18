package com.hongri.multimedia.util;

import android.util.Log;

/**
 * Create by zhongyao on 2021/8/18
 * Description: 避免快速点击
 */
public class DoubleClickUtil {

    private static final long INTERVAL_TIME = 500;
    private static long mLastTime;
    private static long mCurrentTime;

    public static boolean isFastDoubleInvoke(long id) {
        boolean flag = true;
        mCurrentTime = System.currentTimeMillis();
        if (mCurrentTime - mLastTime >= INTERVAL_TIME) {
            flag = false;
        }
        mLastTime = mCurrentTime;
        Log.d("ZKAliPhoneProtocol", "flag--->:" + flag + " id:" + id + " INTERVAL_TIME:" + (mCurrentTime - mLastTime));
        return flag;
    }
}

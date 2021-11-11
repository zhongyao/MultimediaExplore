package com.hongri.multimedia.util;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * Create by zhongyao on 2021/8/18
 * Description:
 */
public class AppUtil {
    /**
     * 获取屏幕宽度
     * @param mActivity
     * @return
     */
    public static int getPhoneWidth(Activity mActivity) {
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels; // 宽
    }
}

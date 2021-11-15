package com.hongri.multimedia.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.List;

/**
 * Create by zhongyao on 2021/11/15
 * Description:
 */
public class SchemeUtil {

    /**
     * 如何判断一个Scheme是否有效
     *
     * @param context
     * @param uriString
     * @return
     */
    public static boolean isSchemeValid(Context context, String uriString) {
        if (context == null) {
            return false;
        }
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        if (!activities.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 是否是Http协议
     *
     * @param url
     * @return
     */
    public static boolean isHttpProtocol(String url) {
        if (url.startsWith("http") || url.startsWith("https") || url.startsWith("www")) {
            return true;
        }
        return false;
    }


    /**
     * 是否是office在线文档
     *
     * @param url
     * @return
     */
    public static boolean isDownloadFile(String url) {
        if (url.endsWith(".apk") || url.endsWith(".doc") || url.endsWith(".docx") || url.endsWith("xls") || url.endsWith("xlsx") || url.endsWith("ppt") || url.endsWith("pptx")) {
            return true;
        }
        return false;
    }

    public static boolean isImageFile(String url) {
        if (url.endsWith(".png") || url.endsWith(".jpg") || url.endsWith("jpeg") || url.endsWith(".gif") || url.endsWith(".webp")) {
            return true;
        }
        return false;
    }
}

package com.hongri.multimedia.audio.state;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.hongri.multimedia.audio.VideoPlayer;

/**
 * Create by zhongyao on 2021/8/30
 * Description:
 */
public class VideoStatusManager {
    private static final String TAG = "VideoStatusManager";
    private static Uri uri;

    public static void setStatus(Status curStatus) {
        Log.d(TAG, "curStatus:" + curStatus);
        setStatus(null, null, curStatus, null);
    }

    public static void setStatus(Context context, Handler handler, Status curStatus, Object object) {
        switch (curStatus) {
            case STATUS_NO_READY:

                break;

            case STATUS_READY:
                //视频初始化
                if (context != null && handler != null && object instanceof Uri) {
                    uri = (Uri) object;
                    VideoPlayer.getInstance().createDefaultPlayer(context, handler, uri);
                }
                break;

            case STATUS_START:
                VideoPlayer.getInstance().play();
                break;

            case STATUS_PAUSE:
                VideoPlayer.getInstance().pause();
                break;

            case STATUS_STOP:
                VideoPlayer.getInstance().stop();
                break;

            case STATUS_CANCEL:
                VideoPlayer.getInstance().cancel();
                break;

            case STATUS_RELEASE:
                VideoPlayer.getInstance().release();
                break;

            default:
                break;
        }
    }

    public static Status getStatus() {
        //TODO
        return VideoPlayer.getInstance().getStatus();
    }

    public static long getDuration() {
        return VideoPlayer.getInstance().getDuration();
    }
}

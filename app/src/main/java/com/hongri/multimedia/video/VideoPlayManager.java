package com.hongri.multimedia.video;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.hongri.multimedia.video.state.VideoStatus;

/**
 * Create by zhongyao on 2021/8/30
 * Description:
 */
public class VideoPlayManager {
    private static final String TAG = "VideoPlayManager";
    private static Uri uri;

    public static void setStatus(VideoStatus curStatus) {
        Log.d(TAG, "curStatus:" + curStatus);
        setStatus(null, null, curStatus, null);
    }

    public static void setStatus(Context context, Handler handler, VideoStatus curStatus, Object object) {
        switch (curStatus) {
            case VIDEO_IDLE:

                break;

            case VIDEO_READY:
                //视频初始化
                if (context != null && handler != null && object instanceof Uri) {
                    uri = (Uri) object;
                    VideoPlayer.getInstance().createDefaultPlayer(context, handler, uri);
                }
                break;

            case VIDEO_START:
                VideoPlayer.getInstance().play();
                break;

            case VIDEO_PAUSE:
                VideoPlayer.getInstance().pause();
                break;

            case VIDEO_STOP:
                VideoPlayer.getInstance().stop();
                break;

            case VIDEO_CANCEL:
                VideoPlayer.getInstance().cancel();
                break;

            case VIDEO_RELEASE:
                VideoPlayer.getInstance().release();
                break;

            default:
                break;
        }
    }

    public static VideoStatus getStatus() {
        return VideoPlayer.getInstance().getStatus();
    }

    public static long getDuration() {
        return VideoPlayer.getInstance().getDuration();
    }
}

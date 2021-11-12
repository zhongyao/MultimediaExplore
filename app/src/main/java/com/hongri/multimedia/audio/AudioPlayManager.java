package com.hongri.multimedia.audio;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.hongri.multimedia.audio.state.Status;

/**
 * Create by zhongyao on 2021/8/30
 * Description:
 */
public class AudioPlayManager {

    private static final String TAG = "PlayStatusManager";
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
                //音频初始化
                if (context != null && handler != null && object instanceof Uri) {
                    uri = (Uri) object;
                    AudioPlayer.getInstance().createDefaultPlayer(context, handler, uri);
                }
                break;

            case STATUS_START:
                AudioPlayer.getInstance().play();
                break;

            case STATUS_PAUSE:
                AudioPlayer.getInstance().pause();
                break;

            case STATUS_STOP:
                AudioPlayer.getInstance().stop();
                break;

            case STATUS_CANCEL:
                AudioPlayer.getInstance().cancel();
                break;

            case STATUS_RELEASE:
                AudioPlayer.getInstance().release();
                break;

            default:
                break;
        }
    }

    /**
     * 获取音频播状态
     * @return
     */
    public static Status getStatus() {
        return AudioPlayer.getInstance().getStatus();
    }

    /**
     * 获取音频时长
     * @return
     */
    public static long getDuration() {
        return AudioPlayer.getInstance().getDuration();
    }

    /**
     * 音频是否正在播放
     * @return
     */
    public static boolean isPlaying() {
        return getStatus() == Status.STATUS_START;
    }

}

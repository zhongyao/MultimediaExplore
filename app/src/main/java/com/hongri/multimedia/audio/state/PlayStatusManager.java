package com.hongri.multimedia.audio.state;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.hongri.multimedia.audio.AudioPlayer;
import com.hongri.multimedia.audio.AudioRecorder;

/**
 * Create by zhongyao on 2021/8/30
 * Description:
 */
public class PlayStatusManager {

    private static final String TAG = "PlayStatusManager";
    private static Uri uri;

    public static void setStatus(Status curStatus) {
        Log.d(TAG, "curStatus:" + curStatus);
        setStatus(null, curStatus,null);
    }

    public static void setStatus(Context context, Status curStatus, Object object) {
        switch (curStatus) {
            case STATUS_NO_READY:

                break;

            case STATUS_READY:
                //音频初始化
                if (object instanceof Uri) {
                     uri = (Uri) object;
                    AudioPlayer.getInstance().createDefaultPlayer(context, uri);
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

    public static Status getStatus() {
        //TODO
        return AudioPlayer.getInstance().getStatus();
    }
}

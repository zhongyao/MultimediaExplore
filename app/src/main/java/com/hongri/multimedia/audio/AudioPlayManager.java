package com.hongri.multimedia.audio;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.hongri.multimedia.audio.state.AudioPlayStatus;
/**
 * Create by zhongyao on 2021/8/30
 * Description:音频播放管理类
 */
public class AudioPlayManager {

    private static final String TAG = "PlayStatusManager";
    private static Uri uri;

    public static void setStatus(AudioPlayStatus curAudioStatus) {
        Log.d(TAG, "curStatus:" + curAudioStatus);
        setStatus(null, null, curAudioStatus, null);
    }

    public static void setStatus(Context context, Handler handler, AudioPlayStatus curAudioStatus, Object object) {
        switch (curAudioStatus) {
            case AUDIO_IDLE:

                break;

            case AUDIO_PREPARE:
                //音频初始化
                if (context != null && handler != null && object instanceof Uri) {
                    uri = (Uri) object;
                    AudioPlayer.getInstance().prepareAudioPlayer(context, handler, uri);
                }
                break;

            case AUDIO_START:
                AudioPlayer.getInstance().play();
                break;

            case AUDIO_PAUSE:
                AudioPlayer.getInstance().pause();
                break;

            case AUDIO_STOP:
                AudioPlayer.getInstance().stop();
                break;

            case AUDIO_CANCEL:
                AudioPlayer.getInstance().cancel();
                break;

            case AUDIO_RELEASE:
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
    public static AudioPlayStatus getStatus() {
        return AudioPlayer.getInstance().getAudioStatus();
    }

    /**
     * 获取音频焦点
     */
    public static void requestAudioFocus() {
        AudioModeManager.getInstance().requestAudioFocus();
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
        return getStatus() == AudioPlayStatus.AUDIO_START;
    }

}

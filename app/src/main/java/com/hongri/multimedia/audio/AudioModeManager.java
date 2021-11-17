package com.hongri.multimedia.audio;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.preference.PreferenceManager;

import java.util.Objects;

import static com.hongri.multimedia.audio.AudioModeManager.PlayMode.*;
import static com.hongri.multimedia.audio.AudioModeManager.PlayMode.Receiver;
import static com.hongri.multimedia.audio.AudioModeManager.PlayMode.Speaker;

/**
 * Create by zhongyao on 2021/11/11
 * Description: 音频播放模式
 */
public class AudioModeManager {

    private Context appContext;

    private boolean isPauseMusic;

    enum PlayMode {
        Speaker,//外放
        Headset,//耳机
        Receiver//听筒
    }

    public static AudioModeManager getInstance() {
        return AudioModeManagerHolder.instance;
    }

    public static class AudioModeManagerHolder {
        public static AudioModeManager instance = new AudioModeManager();
    }

    private AudioModeManager() {
    }

    private boolean isPlaying = false;
    private AudioManager audioManager;
    private final String AUDIO_PLAY_IS_SPEAKER_ON = "audio_play_is_speaker_on";
    private boolean defaultIsOpenSpeaker = true;//默认扬声器模式
    private PlayMode playMode = Speaker;

    public void init(Application application) {
        if (application == null) {
            return;
        }
        appContext = application;
        audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);
        setSpeakerOn(defaultIsOpenSpeaker);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        //监听耳机的插拔
        appContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Objects.requireNonNull(intent.getAction()).equals(Intent.ACTION_HEADSET_PLUG)) {
                    int state = intent.getIntExtra("state", 0);
                    if (state == 1) {
                        playMode = Headset;
                    } else if (state == 0) {
                        if (isSpeakerOn()) {
                            playMode = Speaker;
                        } else {
                            playMode = Receiver;
                        }
                    }
                    changeMode(playMode);
                }
            }

        }, intentFilter);
    }


    /**
     * 判断 当前是否是扬声器模式
     * （注：当前只是记录值 可能也是耳机模式 耳机模式下允许切换模式 但是只有拔下耳机时才生效）
     */
    public boolean isSpeakerOn() {
        if (appContext == null) {
            return false;
        }
        return PreferenceManager.getDefaultSharedPreferences(appContext).getBoolean(AUDIO_PLAY_IS_SPEAKER_ON, defaultIsOpenSpeaker);
    }

    /**
     * 设置播放模式
     */
    public void setSpeakerOn(Boolean isSpeaker) {
        if (appContext == null) {
            return;
        }
        PreferenceManager.getDefaultSharedPreferences(appContext).edit().putBoolean(AUDIO_PLAY_IS_SPEAKER_ON, isSpeaker).apply();

        if (playMode != PlayMode.Headset) {
            if (isSpeaker) {
                playMode = Speaker;
            } else {
                playMode = Receiver;
            }
            changeMode(playMode);
        }
    }

    public void requestAudioFocus() {
        if (audioManager != null && audioManager.isMusicActive()) {
            audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            isPauseMusic = true;
        }
    }

    public void abandonAudioFocus() {
        if (audioManager != null && isPauseMusic) {
            audioManager.abandonAudioFocus(null);
            isPauseMusic = false;
        }
    }

    /**
     * 切换到外放
     */
    private void changeToSpeaker() {
        if (appContext == null) {
            return;
        }
        AudioManager audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(true);
    }

    /**
     * 切换到耳机模式
     */
    private void changeToHeadset() {
        audioManager.setSpeakerphoneOn(false);
    }

    /**
     * `
     * 切换到听筒
     */
    private void changeToReceiver() {
        audioManager.setSpeakerphoneOn(false);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    }

    /**
     * 更换模式
     */
    public void changeMode(PlayMode playMode) {
//        if (!isPlaying) {
//            return;
//        }
        switch (playMode) {
            case Receiver:
                changeToReceiver();
                break;
            case Speaker:
                changeToSpeaker();
                break;
            case Headset:
                changeToHeadset();
                break;
        }
    }

    /**
     * 判断当前是否是听筒模式
     * （注：播放时切换到听筒模式 前1-2s 可能无声音 建议 在听筒模式下延迟1-2s播放）
     */
    public boolean isReceiver() {
        return playMode == Receiver;
    }

    /**
     * 播放时语音时 调用该方法 屏蔽第三方音乐 同时使当前播放模式生效
     */
    public void onPlay() {
        isPlaying = true;
        audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        changeMode(playMode);
    }

    /**
     * 语音停止播放时 调用该方法 恢复第三方音乐播放  恢复播放模式
     */
    public void onStop() {
        isPlaying = false;
        audioManager.abandonAudioFocus(null);
        audioManager.setMode(AudioManager.MODE_NORMAL);
    }


    /**
     * 耳机是否插入
     *
     * @return
     */
    public boolean isWiredHeadsetOn() {
        if (audioManager == null) {
            return false;
        }
        return audioManager.isWiredHeadsetOn();
    }
}

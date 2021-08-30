package com.hongri.multimedia.audio;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.LoadEventInfo;
import com.google.android.exoplayer2.source.MediaLoadData;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.hongri.multimedia.audio.state.Status;

/**
 * Create by zhongyao on 2021/8/30
 * Description:
 */
public class AudioPlayer {

    private ExoPlayer player;
    private DefaultDataSourceFactory dataSourceFactory;
    private ProgressiveMediaSource mediaSource;
    //播放状态
    private Status status = Status.STATUS_NO_READY;
    private long duration;
    public static final int WHAT_DURATION = 0;

    private AudioPlayer() {
//        createDefaultPlayer();
    }

    public Status getStatus() {
        return status;
    }

    public static class AudioPlayerHolder {
        public static AudioPlayer instance = new AudioPlayer();
    }

    public static AudioPlayer getInstance() {
        return AudioPlayerHolder.instance;
    }


    public void createDefaultPlayer(Context context, Handler handler, Uri uri) {
        player = new SimpleExoPlayer.Builder(context).build();
        dataSourceFactory = new DefaultDataSourceFactory(context);
        mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        mediaSource.addEventListener(handler, new MediaSourceEventListener() {
            @Override
            public void onLoadCompleted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
                mediaSource.removeEventListener(this);
                duration = player.getDuration()/1000;
                Message msg = new Message();
                msg.what = WHAT_DURATION;
                msg.obj = duration;
                handler.sendMessage(msg);
            }
        });
        player.setMediaSource(mediaSource);
        player.prepare();
    }

    public long getDuration() {
        return duration;
    }

    public void play() {
        if (player == null) {
            return;
        }
        player.play();
    }

    public void pause() {
        if (player == null) {
            return;
        }
        if (player.isPlaying()) {
            player.pause();
        }
    }

    public void stop() {
        if (player == null) {
            return;
        }
        player.stop();
    }

    public void cancel() {
        if (player == null) {
            return;
        }
        //TODO cancel
    }

    public void release() {
        if (player == null) {
            return;
        }
        player.release();
    }
}

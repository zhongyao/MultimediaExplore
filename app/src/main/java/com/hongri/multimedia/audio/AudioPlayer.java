package com.hongri.multimedia.audio;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
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


    public void createDefaultPlayer(Context context, Uri uri) {
        player = new SimpleExoPlayer.Builder(context).build();
        dataSourceFactory = new DefaultDataSourceFactory(context);
        mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        player.setMediaSource(mediaSource);
        player.prepare();
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

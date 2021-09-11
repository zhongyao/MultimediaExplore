package com.hongri.multimedia.audio;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.LoadEventInfo;
import com.google.android.exoplayer2.source.MediaLoadData;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.hongri.multimedia.audio.state.Status;

/**
 * Create by zhongyao on 2021/9/8
 * Description:
 */
public class VideoPlayer {

    private final String TAG = "VideoPlayer";
    private ExoPlayer player;
    private DefaultDataSourceFactory dataSourceFactory;
    private MediaSource mediaSource;
    //播放状态
    private Status status = Status.STATUS_NO_READY;
    private long duration;
    public static final int WHAT_DURATION = 0;
    public static final int WHAT_POSITION = 1;
    private Handler handler;
    private long playbackPosition, currentWindow;
    private boolean playWhenReady;

    private VideoPlayer() {
    }

    public static class Holder {
        public static VideoPlayer videoPlayer = new VideoPlayer();
    }

    public static VideoPlayer getInstance() {
        return Holder.videoPlayer;
    }

    public ExoPlayer createDefaultPlayer(Context context, Handler handler, Uri uri) {
        this.handler = handler;
        player = new SimpleExoPlayer.Builder(context).build();
        dataSourceFactory = new DefaultDataSourceFactory(context);
        mediaSource = buildMediaSource(uri);
        mediaSource.addEventListener(handler, new MediaSourceEventListener() {
            @Override
            public void onLoadCompleted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
                mediaSource.removeEventListener(this);
                duration = player.getDuration() / 1000;
                Message msg = new Message();
                msg.what = WHAT_DURATION;
                msg.obj = duration;
                handler.sendMessage(msg);

                Log.d(TAG, "onLoadCompleted---> duration:" + duration);
            }
        });
        player.setMediaSource(mediaSource);
        player.prepare();

        player.addListener(new Player.Listener() {

            @Override
            public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
                Log.d(TAG, "onPlayWhenReadyChanged---> playWhenReady:" + playWhenReady);
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Log.d(TAG, "onPlaybackStateChanged---> playbackState:" + playbackState);
                switch (playbackState) {
                    case Player.STATE_READY:
                        Log.d(TAG, "STATE_READY");
                        break;
                    case Player.STATE_BUFFERING:
                        Log.d(TAG, "STATE_BUFFERING");
                        break;
                    case Player.STATE_ENDED:
                        Log.d(TAG, "STATE_ENDED");
                        break;
                    case Player.STATE_IDLE:
                        Log.d(TAG, "STATE_IDLE");
                        break;
                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Log.d(TAG, "onIsPlayingChanged---> isPlaying:" + isPlaying);
                if (isPlaying) {
                    status = Status.STATUS_START;
                } else {

                }
            }

            @Override
            public void onIsLoadingChanged(boolean isLoading) {
                Log.d(TAG, "onIsLoadingChanged---> isLoading:" + isLoading);
            }

            @Override
            public void onPositionDiscontinuity(Player.PositionInfo oldPosition, Player.PositionInfo newPosition, int reason) {
                Log.d(TAG, "onPositionDiscontinuity---> oldPosition:" + oldPosition + " newPosition:" + newPosition);
            }


            @Override
            public void onPlayerError(PlaybackException error) {
                Log.e(TAG, "onPlayerError---> onPlayerError:" + error != null ? error.toString() : "");
            }

            @Override
            public void onAudioAttributesChanged(AudioAttributes audioAttributes) {

            }

            @Override
            public void onMediaMetadataChanged(MediaMetadata mediaMetadata) {
            }
        });
        return player;
    }

    private MediaSource buildMediaSource(Uri uri) {
        int type = Util.inferContentType(uri);
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();
        Log.d(TAG, "buildMediaSource --- > type:" + type);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri));
            case C.TYPE_SS:
                return new SsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri));
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri));
            case C.TYPE_OTHER:
                return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri));
            default:
                throw new IllegalStateException("Unsupported type: " + type);
        }
    }

    public long getDuration() {
        return duration;
    }

    public Player getPlayer() {
        return player;
    }

    public void play() {
        if (player == null) {
            return;
        }
        Log.d(TAG, "play");
        player.setPlayWhenReady(true);
    }

    public void pause() {
        if (player == null) {
            return;
        }
        Log.d(TAG, "pause");
        if (player.isPlaying()) {
            player.pause();
        }
    }

    public void stop() {
        if (player == null) {
            return;
        }
        Log.d(TAG, "stop");
        player.stop();
    }

    public void cancel() {
        if (player == null) {
            return;
        }
        Log.d(TAG, "cancel");
        //TODO cancel
    }

    public void release() {
        if (player == null) {
            return;
        }
        Log.d(TAG, "release");
        playbackPosition = player.getCurrentPosition();
        currentWindow = player.getCurrentWindowIndex();
        playWhenReady = player.getPlayWhenReady();
        player.release();
        player = null;
    }

    public Status getStatus() {
        return status;
    }

}

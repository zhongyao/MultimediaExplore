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
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.hongri.multimedia.audio.state.Status;

/**
 * Create by zhongyao on 2021/8/30
 * Description:
 */
public class AudioPlayer {

    private final String TAG = "AudioPlayer";
    private ExoPlayer player;
    private DefaultDataSourceFactory dataSourceFactory;
    private MediaSource mediaSource;
    //播放状态
    private Status status = Status.STATUS_NO_READY;
    private long duration;
    public static final int WHAT_DURATION = 0;
    public static final int WHAT_POSITION = 1;
    private Handler handler;
    private long currentPosition, contentBufferedPosition;

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
        this.handler = handler;
        player = new SimpleExoPlayer.Builder(context).build();
        mediaSource = buildMediaSource(uri);
        if (mediaSource == null) {
            return;
        }
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

        player.setPlayWhenReady(true);

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
    }

    private MediaSource buildMediaSource(Uri uri) {
        if (uri == null) {
            return null;
        }
        int type = Util.inferContentType(uri);
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();
        Log.d(TAG, "buildMediaSource --- > uri:" + uri.toString() + " type:" + type);
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
        player.play();


//        if (handler != null) {
//            while (currentPosition < getDuration()) {
//                Message msg = new Message();
//                currentPosition = player.getCurrentPosition() / 1000;
//                contentBufferedPosition = player.getContentBufferedPosition() / 1000;
//                HashMap<String, Long> hashMap = new HashMap<>();
//                hashMap.put("currentPosition", currentPosition);
//                hashMap.put("contentBufferedPosition", contentBufferedPosition);
//                msg.what = WHAT_POSITION;
//                msg.obj = hashMap;
//                handler.sendMessageDelayed(msg, 1000);
//                Log.d(TAG, "player.getDuration():" + player.getDuration() + " player.getContentPosition():" + player.getContentPosition()+ " player.getCurrentPosition():" + player.getCurrentPosition() + " currentPosition:" + currentPosition + " getDuration:" + getDuration());
//            }
//        }
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

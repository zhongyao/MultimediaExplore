package com.hongri.multimedia.video.widget;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.hongri.multimedia.R;
import com.hongri.multimedia.video.VideoPlayer;


/**
 * Create by zhongyao on 2021/9/8
 * Description:
 */
public class VideoPlayView extends ConstraintLayout {
    private final String TAG = "VideoView";
    private ExoPlayer player;
    private ImageView playStatusIv;
    private PlayerView playerView;
    private PlayerControlView controlView;
    //private String uriString = "https://v-cdn.zjol.com.cn/280443.mp4";
    private String uriString = "https://www.w3school.com.cn/example/html5/mov_bbb.mp4";
//    private String uriString = "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8";

    private Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    public VideoPlayView(@NonNull Context context) {
        super(context);

        initView();

        initPlayer();
    }

    public VideoPlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.video_view, this);
        playStatusIv = view.findViewById(R.id.icon_status_play);
        playerView = view.findViewById(R.id.video_play_view);

        controlView = playerView.findViewById(R.id.exo_controller);
        controlView.setProgressUpdateListener((position, bufferedPosition) -> {
            Log.d(TAG, "position:" + position + " bufferedPosition:" + bufferedPosition);
        });

        Uri uri = Uri.parse(uriString);
        player = VideoPlayer.getInstance().createDefaultPlayer(getContext(), handler, uri);
        playerView.setPlayer(player);
    }


    private void initPlayer() {

    }

}

package com.hongri.multimedia;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.hongri.multimedia.audio.VideoPlayer;
import com.hongri.multimedia.audio.state.Status;
import com.hongri.multimedia.audio.state.VideoStatusManager;
import com.hongri.multimedia.audio.widget.VideoPlayView;

public class VideoActivity extends AppCompatActivity {

    private static final String TAG = "VideoActivity";
    private VideoPlayView videoPlayView;

    private Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        videoPlayView = findViewById(R.id.videoPlayView);
    }
}
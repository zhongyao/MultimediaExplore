package com.hongri.multimedia;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.hongri.multimedia.video.widget.VideoPlayView;

public class VideoActivity extends AppCompatActivity {

    private static final String TAG = "VideoActivity";
    private VideoPlayView videoPlayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        videoPlayView = findViewById(R.id.videoPlayView);
    }
}
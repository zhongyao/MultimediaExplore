package com.hongri.multimedia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button audioBtn, videoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        audioBtn = findViewById(R.id.audioBtn);
        videoBtn = findViewById(R.id.videoBtn);
        audioBtn.setOnClickListener(this);
        videoBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent;
        switch (id) {
            case R.id.audioBtn:
                intent = new Intent(MainActivity.this, AudioActivity.class);
                startActivity(intent);
                break;

            case R.id.videoBtn:
                intent = new Intent(MainActivity.this, VideoActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }
}
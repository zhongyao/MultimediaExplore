package com.hongri.multimedia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hongri.multimedia.audio.AudioActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button audioBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        audioBtn = findViewById(R.id.audioBtn);
        audioBtn.setOnClickListener(this);
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

            default:
                break;
        }
    }
}
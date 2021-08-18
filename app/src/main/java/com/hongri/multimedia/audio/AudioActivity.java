package com.hongri.multimedia.audio;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.hongri.multimedia.AppUtil;
import com.hongri.multimedia.R;
import com.hongri.multimedia.audio.state.AudioStatusManager;
import com.hongri.multimedia.audio.state.Status;
import com.hongri.multimedia.audio.widget.RecordButton;
import com.hongri.multimedia.audio.widget.RecordLayout;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 音频Activity：
 * https://bbs.huaweicloud.com/blogs/239037
 */
public class AudioActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "AudioActivity";
    private Button start, pause, cancel, stop;
    private RecordLayout recordLayout;
    private RecordButton recordBtn;
    private boolean permissionGranted = false;
    private int phoneWidth;
    private static String[] PERMISSION_ALL = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        phoneWidth = AppUtil.getPhoneWidth(this);

        start = findViewById(R.id.start);
        pause = findViewById(R.id.pause);
        cancel = findViewById(R.id.cancel);
        stop = findViewById(R.id.stop);
        recordLayout = findViewById(R.id.recordLayout);
        recordBtn = findViewById(R.id.recordBtn);

        start.setOnClickListener(this);
        pause.setOnClickListener(this);
        cancel.setOnClickListener(this);
        stop.setOnClickListener(this);
        recordLayout.setOnClickListener(this);
        recordBtn.setOnClickListener(this);

        recordLayout.setPhoneWidth(this, phoneWidth);

        XXPermissions.with(this).permission(PERMISSION_ALL).request(new OnPermissionCallback() {
            @Override
            public void onGranted(List<String> permissions, boolean all) {
                if (all) {
                    permissionGranted = true;
                    AudioStatusManager.setStatus(Status.STATUS_READY);
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (!permissionGranted) {
            Toast.makeText(AudioActivity.this, "请申请权限后再试", Toast.LENGTH_LONG).show();
            return;
        }
        int id = v.getId();
        switch (id) {
            case R.id.start:
                AudioStatusManager.setStatus(Status.STATUS_START);
                break;

            case R.id.pause:
                AudioStatusManager.setStatus(Status.STATUS_PAUSE);
                break;

            case R.id.cancel:
                AudioStatusManager.setStatus(Status.STATUS_CANCEL);
                break;

            case R.id.stop:
                AudioStatusManager.setStatus(Status.STATUS_STOP);
                break;

            case R.id.recordLayout:

                break;
            case R.id.recordBtn:

                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AudioStatusManager.setStatus(Status.STATUS_RELEASE);
    }
}
package com.hongri.multimedia.audio;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.hongri.multimedia.AppUtil;
import com.hongri.multimedia.R;
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

        //音频初始化
        String fileName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());

        XXPermissions.with(this).permission(PERMISSION_ALL).request(new OnPermissionCallback() {
            @Override
            public void onGranted(List<String> permissions, boolean all) {
                if (all) {
                    permissionGranted = true;
                    AudioRecorder.getInstance().createDefaultAudio(fileName);
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
                AudioRecorder.getInstance().startRecord(new RecordStreamListener() {
                    @Override
                    public void recordOfByte(byte[] data, int begin, int end) {
                        Log.d(TAG, "data:" + Arrays.toString(data) + " begin:" + begin + " end:" + end);
                    }
                });
                break;
            case R.id.pause:
                AudioRecorder.getInstance().pauseRecord();
                break;

            case R.id.cancel:
                AudioRecorder.getInstance().canel();
                break;

            case R.id.stop:
                AudioRecorder.getInstance().stopRecord();
                break;
            case R.id.recordLayout:

                break;
            case R.id.recordBtn:

                break;

            default:
                break;
        }
    }
}
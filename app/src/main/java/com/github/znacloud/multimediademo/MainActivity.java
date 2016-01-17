package com.github.znacloud.multimediademo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        Button audioBtn = (Button)findViewById(R.id.btn_audio_manager);
        audioBtn.setOnClickListener(this);

        Button photoBtn = (Button)findViewById(R.id.btn_photo_manager);
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            photoBtn.setOnClickListener(this);
        }else{
            photoBtn.setVisibility(View.GONE);
        }



    }

    @Override
    public void onClick(View v) {
        if(R.id.btn_photo_manager == v.getId()){
            startActivity(new Intent(this,TakePhotoActivity.class));
        }
    }
}

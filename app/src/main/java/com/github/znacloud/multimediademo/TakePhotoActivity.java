package com.github.znacloud.multimediademo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Stephan on 2016/1/11.
 */
public class TakePhotoActivity extends AppCompatActivity{

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_VIDEO_CAPTURE = 2;
    private static final int REQUEST_CAMERA_CONTROL = 3;
    private ImageView mImageView;
    private TextView mDescTv;
    private String mCurrentPhotoPath;
    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        findViewById(R.id.btn_take_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        findViewById(R.id.btn_take_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakeVideoIntent();
            }
        });
        findViewById(R.id.btn_control_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TakePhotoActivity.this,CameraActivity.class);
                startActivityForResult(intent, REQUEST_CAMERA_CONTROL);
            }
        });
        mImageView = (ImageView)findViewById(R.id.iv_thumbnail);
        mDescTv = (TextView)findViewById(R.id.tv_desc);

        mVideoView = (VideoView)findViewById(R.id.vv_video);
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                int height = mp.getVideoHeight();
                int width = mp.getVideoWidth();
                Log.e("test", "width=>" + width + ",height=>" + height);
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mVideoView.getLayoutParams();
                if(lp != null){
//                    lp.width = width;
//                    lp.height = height;
//                    mVideoView.requestLayout();
                }
            }
        });

    }



    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createMediaFile(".jpg");
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(photoFile != null){
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }


        }
    }
    private void dispatchTakeVideoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File videoFile = null;
            try {
                videoFile = createMediaFile(".mp4");
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(videoFile != null){
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
                startActivityForResult(takePictureIntent, REQUEST_VIDEO_CAPTURE);
            }


        }
    }

    private File createMediaFile(String suffix) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                suffix,         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath =  image.getAbsolutePath();
        Log.e("test","PATH=>"+mCurrentPhotoPath);
        return image;
    }

    private void addFileToMedia() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        Log.e("test","URI=>"+contentUri.toString());
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                addFileToMedia();
                if (data == null) return;
                Log.e("test", "data uri=>" + data.getData());
                Bundle extras = data.getExtras();
                if (extras == null) return;
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                if (imageBitmap != null) {
                    mImageView.setImageBitmap(imageBitmap);
                    String desc = "缩略图信息：\n" + "w=" + imageBitmap.getWidth() + "\nh=" + imageBitmap.getHeight();
                    mDescTv.setText(desc);
                }
            }else if(requestCode == REQUEST_VIDEO_CAPTURE){
                addFileToMedia();
                if (data == null) return;
                Uri videoUri = data.getData();
                Log.e("test", "data uri=>" + videoUri);
                if (videoUri != null) {
                    mVideoView.setVideoURI(videoUri);
                    mVideoView.start();
                }
            }
        }
    }
}

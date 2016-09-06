package com.sdi.castivate;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

/**
 * Created by twilightuser on 6/9/16.
 */
public class CastingVideoPreview extends Activity {

    VideoView videoview;
    TextView PreviewDone;
    String VideoURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.casting_video_preview);

        videoview = (VideoView) findViewById(R.id.VideoView);
        PreviewDone=(TextView) findViewById(R.id.PreviewDone);

        PreviewDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        Intent intent = getIntent();
        VideoURL = intent.getStringExtra("videoRecordingPath");

        try {

            MediaController mediacontroller = new MediaController(CastingVideoPreview.this);
            mediacontroller.setAnchorView(videoview);

            Uri video = Uri.parse(VideoURL);
            videoview.setMediaController(mediacontroller);
            videoview.setVideoURI(video);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        videoview.requestFocus();
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mp) {
                videoview.start();
            }
        });

    }
}

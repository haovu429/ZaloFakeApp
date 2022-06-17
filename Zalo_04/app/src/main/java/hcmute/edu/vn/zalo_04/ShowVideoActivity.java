package hcmute.edu.vn.zalo_04;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class ShowVideoActivity extends AppCompatActivity {
//    private ActionBar actionBar;
    private VideoView videoView;

    private Uri videoUri;
    private String videoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video);

//        actionBar = getSupportActionBar();
//        actionBar.setTitle("Video file");

        videoView = findViewById(R.id.showVideoView);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.videoUrl = (String) bundle.get("videoUrl");
            if (this.videoUrl == null) {
                Log.d("loi", "Khong nhan duoc videoUrl cho video");
                finish();
            } else {
                videoUri = Uri.parse(videoUrl);
                SetVideoToVideoView();
            }
        }
    }

    private void SetVideoToVideoView() {
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        videoView.setMediaController(mediaController);
        videoView.setVideoURI(videoUri);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.bringToFront();
                videoView.setFocusable(true);
                videoView.start();
            }
        });
    }
}
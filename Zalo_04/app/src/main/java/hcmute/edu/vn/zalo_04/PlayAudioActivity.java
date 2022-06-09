package hcmute.edu.vn.zalo_04;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayAudioActivity extends AppCompatActivity {
    private TextView title, time;
    private Button btn_pick_file, btn_play, btn_exit;
    private String duration;
    private SeekBar progress;

    private MediaPlayer mediaPlayer;
    private ScheduledExecutorService timer;

    private ActivityResultLauncher<String> pick_audio;

    private String audioUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_audio);

        btn_pick_file = findViewById(R.id.btn_pickFile);
        btn_play = findViewById(R.id.btn_play);
        title = findViewById(R.id.title);
        time = findViewById(R.id.time);
        progress = findViewById(R.id.progress);
        btn_exit = findViewById(R.id.btn_exit);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null){
            this.audioUrl = (String) bundle.get("audioUrl");
            if(this.audioUrl == null){
                Log.d("loi", "Khong nhan duoc userId de gui audio");
                finish();
            }

        }
        //default
        Uri link = Uri.parse(audioUrl);

        createMediaPlayer(link);

        Log.d("link",link.toString());

        //open audio from device
        pick_audio = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                Uri uri = result;
                createMediaPlayer(uri);
            }
        });

        btn_pick_file.setOnClickListener(View -> {
            pick_audio.launch("audio/*");
        });


        btn_play.setOnClickListener(View ->{
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    btn_play.setText("PLAY");
                    timer.shutdown();
                } else {
                    mediaPlayer.start();
                    btn_play.setText("PAUSE");
                    timer = Executors.newScheduledThreadPool(1);
                    timer.scheduleAtFixedRate(new Runnable() {
                        @Override
                        public void run() {
                            if (mediaPlayer != null){
                                if (!progress.isPressed()){
                                    progress.setProgress(mediaPlayer.getCurrentPosition());
                                }
                            }
                        }
                    }, 10, 10, TimeUnit.MILLISECONDS);
                }
            }
        });

        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mediaPlayer != null){
                    int millis = mediaPlayer.getCurrentPosition();
                    long total_secs = TimeUnit.SECONDS.convert(millis, TimeUnit.MILLISECONDS);
                    long mins = TimeUnit.MINUTES.convert(total_secs, TimeUnit.SECONDS);
                    long secs = total_secs - (mins*60);
                    time.setText(mins + ":" + secs + "/" + duration);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if( mediaPlayer != null){
                    mediaPlayer.seekTo(progress.getProgress());
                }

            }
        });
        //btn_play.setEnabled(false);

        btn_exit.setOnClickListener(View ->{
            releaseMediaPlayer();
            finish();
        });

    }

    private void createMediaPlayer(Uri uri) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );

        try {
            mediaPlayer.setDataSource(getApplicationContext(), uri);
            mediaPlayer.prepare();

            //title.setText(getNameFromUri(uri));
            title.setText("Audio");
            Log.d("ok","OK on");
            btn_play.setEnabled(true);

            int millis = mediaPlayer.getDuration();
            long total_secs = TimeUnit.SECONDS.convert(millis, TimeUnit.MILLISECONDS);
            long mins = TimeUnit.MINUTES.convert(total_secs, TimeUnit.SECONDS);
            long secs = total_secs - (mins*60);

            duration = mins + ":" + secs;
            progress.setMax(millis);
            progress.setProgress(0);

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    //releaseMediaPlayer();
                    btn_play.setText("PLAY");
                }
            });


        } catch (Exception e) {
            if (title != null){
                title.setText(e.toString());
            }
        }
    }

    @SuppressLint("Range")
    private String getNameFromUri(Uri uri) {
        String fileName = "";
        Cursor cursor = null;
        cursor = getContentResolver().query(uri, new String[]{
                MediaStore.Images.ImageColumns.DISPLAY_NAME
        }, null, null, null);

        if (cursor != null && cursor.moveToFirst()){
            fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
        }

        if (cursor != null){
            cursor.close();
        }

        return fileName;
    }


    public void releaseMediaPlayer(){
        if (time != null) {
            timer.shutdown();
        }

        if (mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
        btn_play.setEnabled(false);
        title.setText("Title");
        time.setText("00:00 / 00:00");
        progress.setMax(100);
        progress.setProgress(0);
    }
}
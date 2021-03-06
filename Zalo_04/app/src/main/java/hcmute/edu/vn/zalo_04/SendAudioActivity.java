package hcmute.edu.vn.zalo_04;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ContentResolver;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import hcmute.edu.vn.zalo_04.model.Audio;
import hcmute.edu.vn.zalo_04.model.Chat;
import hcmute.edu.vn.zalo_04.util.TimeUtil;

public class SendAudioActivity extends AppCompatActivity {
    //Casc nút tương tác với audio, ghi, dừng ghi, phát, dừng phát, chọn audio từ thư viện (không dùng tới)
    private Button buttonStart, buttonStop, buttonPlayLastRecordAudio,
            buttonStopPlayingRecording, btn_send_audio;
    private String AudioSavePathInDevice = null; //Đường dẫn file audio từ thiết bị (Không dùng tới)
    private MediaRecorder mediaRecorder ; //trình ghi âm
    private Random random ; //Biến ngầu nhiên (tạo tên)
    private String RandomAudioFileName = "ABCDEFGHIJKLMNOP"; //Ký tự ngẫu nhiên để tạo tên
    public static final int RequestPermissionCode = 1; // Mã yêu cầu quyền
    private MediaPlayer mediaPlayer ; //Trình phát audio
    private FirebaseDatabase db; //Khai báo liên kết Database
    private DatabaseReference reference; //Biến tham chiếu Database
    private StorageReference storageReference; //Tham chiếu Storage trên firebase

    private Uri download; //link audio

    private FirebaseUser firebaseUser; //Biến lưu người dùng hiện tại
    private String userId; //Biến lưu id tài khoản chat cùng

    @Override
    //Khỏi tạo giao diện
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_audio);
        buttonStart = (Button) findViewById(R.id.button);
        buttonStop = (Button) findViewById(R.id.button2);
        buttonPlayLastRecordAudio = (Button) findViewById(R.id.button3);
        buttonStopPlayingRecording = (Button)findViewById(R.id.button4);
        btn_send_audio = (Button) findViewById(R.id.btn_pick_audio);

        btn_send_audio.setEnabled(false);
        buttonStop.setEnabled(false);
        buttonPlayLastRecordAudio.setEnabled(false);
        buttonStopPlayingRecording.setEnabled(false);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null){
            this.userId = (String) bundle.get("userId");
            if(this.userId == null){
                Log.d("loi", "Khong nhan duoc userId de gui audio");
                finish();
            }

        }

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseDatabase.getInstance();
        reference =db.getReference();

        random = new Random();

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onClick(View view) {

                if(checkPermission()) {

                    AudioSavePathInDevice =
                            Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                    CreateRandomAudioFileName(5) + "AudioRecording.3gp";

                    MediaRecorderReady();

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    buttonStart.setEnabled(false);
                    buttonStop.setEnabled(true);

                    Toast.makeText(SendAudioActivity.this, "Recording started",
                            Toast.LENGTH_LONG).show();
                } else {
                    requestPermission();
                }
                //System.out.println("Done");

                //reference.setValue(AudioSavePathInDevice);

            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                buttonStop.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);
                btn_send_audio.setEnabled(true);

                Toast.makeText(SendAudioActivity.this, "Recording Completed",
                        Toast.LENGTH_LONG).show();
            }
        });

        buttonPlayLastRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {

                buttonStop.setEnabled(false);
                buttonStart.setEnabled(false);
                buttonStopPlayingRecording.setEnabled(true);

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(getFilePath());
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
                Toast.makeText(SendAudioActivity.this, "Recording Playing",
                        Toast.LENGTH_LONG).show();
            }
        });

        buttonStopPlayingRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonStop.setEnabled(false);
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);

                if(mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    MediaRecorderReady();
                }
            }

        });

        btn_send_audio.setOnClickListener(View -> {
            upLoad();
            //onClickSendAudio();

        });

    }

    /*private void onClickSendAudio() {

        Intent intent = new Intent(this, MessageActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putSerializable("urlAudio", download.toString());
        intent.putExtras(bundle);
        this.startActivity(intent);

    }*/

    //Chuẩn bị trình ghi âm
    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(getFilePath());
    }

    //Tạo tên file ngẫu nhiên
    public String CreateRandomAudioFileName(int string){
        StringBuilder stringBuilder = new StringBuilder( string );
        int i = 0 ;
        while(i < string ) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++ ;
        }
        return stringBuilder.toString();
    }

    //yêu cầu quyền truy cập kho lưu trữ trên thiết bị
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void requestPermission() {
        ActivityCompat.requestPermissions(SendAudioActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO, READ_EXTERNAL_STORAGE}, RequestPermissionCode);//, WRITE_EXTERNAL_STORAGE
    }

    //Kiểm tra cấp quyền
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean ReadStorePermission = grantResults[2] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission && ReadStorePermission) {
                        Toast.makeText(SendAudioActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SendAudioActivity.this, "Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    //Kiểm tra cấp quyền
    @RequiresApi(api = Build.VERSION_CODES.R)
    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(),
                READ_EXTERNAL_STORAGE);
        System.out.println("quyen ql:"+result);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED &&
                result2 == PackageManager.PERMISSION_GRANTED;
    }

    //Lấy đường dẫn file
    private String getFilePath(){
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File recordAudio = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(recordAudio,"AudioRecording.3gp");
        return file.getPath();
    }

    //Upload  audio lên firebase
    private void upLoad(){
        String downloadLink = "";
        /*final DatabaseReference reference = FirebaseDatabase.getInstance(
                "https://record-b3083-default-rtdb.firebaseio.com").getReference();*/
        //HashMap<String,Object> hashMap = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        Uri uri = Uri.fromFile(new File(getFilePath()));

        String fileNameInFirebase = "audio"+calendar.getTimeInMillis()+ ".3gp";
        String idFile = "audio"+calendar.getTimeInMillis();

        StorageReference n = storageReference.child("audio").child(fileNameInFirebase);

        Log.d("BBB","uri ="+uri.toString());
        UploadTask uploadTask = n.putFile(uri);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()){
                    throw task.getException();
                }
                return n.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Uri uri = task.getResult();

                    String timeNow = TimeUtil.getTimeNow();
                    //updateDownloadLink(uri);
                    Chat chat = new Chat(firebaseUser.getUid(),userId, false);
                    chat.setAudio(uri.toString());
                    chat.setTime(timeNow);
                    chat.setIdfile(idFile);
                    sendMessage(chat);

                    Audio audio = new Audio(idFile, firebaseUser.getUid(), userId, timeNow, fileNameInFirebase);
                    saveInfoAudio(audio);

                    Toast.makeText(SendAudioActivity.this,"Done",Toast.LENGTH_SHORT).show();
                    //Log.d("BBB",download.toString());

                }
                else{
                    Log.d("BBB","cant upload");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("BBB","cant upload");
            }
        });
    }

   /* private String getFileExtension(Uri uri){
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }*/

    //Gửi tin nhắn dạng audio
    private void sendMessage(Chat chat){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", chat.getSender());
        hashMap.put("receiver", chat.getReceiver());
        hashMap.put("message", chat.getMessage());
        hashMap.put("isseen", chat.isIsseen());
        hashMap.put("video", chat.getVideo());
        hashMap.put("audio", chat.getAudio());
        hashMap.put("image", chat.getImage());
        hashMap.put("time", chat.getTime());
        hashMap.put("idfile", chat.getIdfile());

        reference.child("Chats").push().setValue(hashMap);

        //add user to message fragment
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(userId);

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRef.child("id").setValue(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(userId)
                .child(firebaseUser.getUid());

        chatRefReceiver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    chatRefReceiver.child("id").setValue(firebaseUser.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void saveInfoAudio(Audio audio){

        DatabaseReference audioRef = FirebaseDatabase.getInstance().getReference("AudioList")
                .child(firebaseUser.getUid())
                .child(audio.getId());

        audioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("id", audio.getId());
                    hashMap.put("sender", audio.getSender());
                    hashMap.put("receiver", audio.getReceiver());
                    hashMap.put("time", audio.getTime());
                    hashMap.put("filename", audio.getFilename());

                    audioRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(SendAudioActivity.this, "saved info Audio", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateDownloadLink(Uri url){
        download = url;
    }
}
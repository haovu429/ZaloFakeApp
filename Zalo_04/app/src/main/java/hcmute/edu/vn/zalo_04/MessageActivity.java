package hcmute.edu.vn.zalo_04;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.edu.vn.zalo_04.MyInterface.IReleaseStorage;
import hcmute.edu.vn.zalo_04.adapter.MessageAdapter;
import hcmute.edu.vn.zalo_04.model.Chat;
import hcmute.edu.vn.zalo_04.model.Image;
import hcmute.edu.vn.zalo_04.model.User;
import hcmute.edu.vn.zalo_04.util.TimeUtil;

public class MessageActivity extends AppCompatActivity implements IReleaseStorage {

    private CircleImageView profile_image;
    private TextView username;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private MessageAdapter messageAdapter;

    private Intent intent;
    private String userId;

    private ImageButton btn_send;

    private EditText txt_send;

    private List<Chat> chatList;

    private RecyclerView rcv_chat;

    private ValueEventListener seenListener;

    private ImageView img_video, img_audio;
    private String urlAudio;

    private ProgressBar progressBar;

    private Uri imageUri;
    private StorageTask uploadTask;
    private StorageReference storageReference;

    private RelativeLayout layout;

    private ActivityResultLauncher<Intent> takePhoto;
    private ActivityResultLauncher<String> uploadPhoto;

    private ActivityResultLauncher<Intent> pickVideoFromCamera;
    private ActivityResultLauncher<Intent> pickVideoFromGallery;

    private boolean refresh = false;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private Bitmap bitmap;

    private static final int CAMERA_REQUEST_CODE = 102;
    private String[] cameraPermissions;

    private static final int VIDEO_PICK_GALLERY_CODE = 100;
    private static final int VIDEO_PICK_CAMERA_CODE = 101;
    private static final int VIDEO_CAMERA_REQUEST_CODE = 102;
    private String[] videoCameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private Uri videoUri;
    private ProgressDialog videoProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null){
            this.urlAudio = (String) bundle.get("urlAudio");

        }

        intent = getIntent();
        userId = intent.getStringExtra("userId");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MessageActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Log.d("loi", "Back");
            }
        });

        rcv_chat = findViewById(R.id.rcv_chat);
        rcv_chat.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rcv_chat.setLayoutManager(linearLayoutManager);


        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        txt_send = findViewById(R.id.txt_send);
        img_video = findViewById(R.id.img_video);
        img_audio = findViewById(R.id.img_audio);

        layout = findViewById(R.id.layout);  //specify here Root layout I

        img_audio.setOnClickListener(View ->{
            PopupMenu popupMenu = new PopupMenu(this, View);
            popupMenu.getMenuInflater().inflate(R.menu.menu_audio, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.item_audio_exists:
                            //test choi
                            //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UC5GCMUwYXZTMKVc8OBvLbvQ")));
                            return true;
                        case R.id.item_create_audio:
                            Intent intent = new Intent(MessageActivity.this, SendAudioActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("userId", userId);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            return true;
                        default:
                            return false;
                    }
                }
            });
            popupMenu.show();
        });

        videoProgressDialog = new ProgressDialog(this);
        videoProgressDialog.setTitle("Vui lòng đợi...");
        videoProgressDialog.setMessage("Uploading video...");
        videoProgressDialog.setCanceledOnTouchOutside(false);

        img_video.setOnClickListener(View -> {
            PopupMenu popupMenu = new PopupMenu(this, View);
            popupMenu.getMenuInflater().inflate(R.menu.menu_video, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.item_video_exists: // Pick video from gallery
                            Intent intent = new Intent();
                            intent.setType("video/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            pickVideoFromGallery.launch(Intent.createChooser(intent, "Chọn video"));
                            return true;
                        case R.id.item_create_video: // Pick video from camera
                            if (!CheckCameraPermission()) {
                                Video_RequestCameraPermission();
                            } else {
                                Intent intent1 = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                                pickVideoFromCamera.launch(intent1);
                            }
                            return true;
                        case R.id.item_photo_exists:
                            uploadPhoto.launch("image/*");
                            return true;
                        case R.id.item_take_photo:
                            //Uri uri = Uri.parse("image/*");
                            RequestCameraPermission();
                            Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (intent1.resolveActivity(getPackageManager()) != null){
                                takePhoto.launch(intent1);
                            } else {
                                Toast.makeText(MessageActivity.this, "Not support", Toast.LENGTH_SHORT).show();
                            }

                            return true;
                        default:
                            return false;
                    }
                }
            });
            popupMenu.show();
        });




        btn_send.setOnClickListener(View -> {
            String msg = txt_send.getText().toString().trim();
            if (!msg.equals("")){
                Chat chat = new Chat(firebaseUser.getUid(), userId, msg, false);
                chat.setTime(TimeUtil.getTimeNow());
                sendMessage(chat);
            } else {
                Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
            }
            txt_send.setText("");
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.drawable.user_hao2);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }

                readMessages(firebaseUser.getUid(), userId, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        seenMessage(userId);

        pickVideoFromCamera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    videoUri = result.getData().getData();
                    if (videoUri != null) {
                        Video_UploadToFirebase();
                        videoUri = null; // Reset
                    } else {
                        Toast.makeText(MessageActivity.this, "Không có video được chọn", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        pickVideoFromGallery = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    videoUri = result.getData().getData();
                    if (videoUri != null) {
                        Video_UploadToFirebase();
                        videoUri = null; // Reset
                    } else {
                        Toast.makeText(MessageActivity.this, "Không có video được chọn", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        takePhoto = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK && result.getData() != null){
                    Bundle bundle1 = result.getData().getExtras();
                    bitmap = (Bitmap) bundle1.get("data");
                    saveImage(bitmap);
                    uploadImage();

                }
            }
        });

        uploadPhoto = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        imageUri = result;

                        if (uploadTask != null && uploadTask.isInProgress()) {
                            Toast.makeText(MessageActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                        } else {
                            uploadImage();
                        }
                    }
                });
    }

    private void Video_RequestCameraPermission() {
        ActivityCompat.requestPermissions(this, videoCameraPermissions, VIDEO_CAMERA_REQUEST_CODE);
    }
    private void RequestCameraPermission() {
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }
    /*private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }*/
    private void saveImage(Bitmap bitmap){
        OutputStream fos;
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q){
                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "Image" + ".jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
                imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                Objects.requireNonNull(fos);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    private boolean CheckCameraPermission() {
        boolean r1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean r2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED;
        return r1 && r2;
    }
    /*private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            (takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }*/


    private void seenMessage(final String userId){
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Chờ class ReleaseStorage cập nhât tín hiệu giải phóng bộ nhớ
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (refresh){
                    return;
                }
                for (DataSnapshot snapshot_index : snapshot.getChildren()){
                    Chat chat = snapshot_index.getValue(Chat.class);

                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot_index.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

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

    private void readMessages(String myId, String userId, String imageURL){
        chatList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot snapshot_index : snapshot.getChildren()){
                    Chat chat = snapshot_index.getValue(Chat.class);

                    //Log.d("loi", chat.getSender());
                    if (chat.getReceiver().equals(myId) && chat.getSender().equals(userId) ||
                            chat.getReceiver().equals(userId) && chat.getSender().equals(myId)){
                        chatList.add(chat);
                    }
                }
                messageAdapter= new MessageAdapter(MessageActivity.this, chatList, imageURL);
                rcv_chat.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");
    }


    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(progressBar, params);

        if (imageUri != null){

            storageReference = FirebaseStorage.getInstance().getReference("uploads");


            Calendar calendar = Calendar.getInstance();
            //String idFile = String.valueOf(calendar.getTimeInMillis());
            String idFile = "image"+calendar.getTimeInMillis();
            String fileNameInFirebase = idFile + "." + getFileExtension(imageUri);
            StorageReference n = storageReference.child("image").child(fileNameInFirebase);
            //deleteOldImage(currentUser.getImageURL());

            uploadTask = n.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return n.getDownloadUrl();
                    //System.out.println("loi");
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        String timeNow = TimeUtil.getTimeNow();

                        //updateDownloadLink(uri);
                        Chat chat = new Chat(firebaseUser.getUid(),userId, false);
                        chat.setImage(downloadUri.toString());
                        chat.setTime(timeNow);
                        chat.setIdfile(idFile);
                        sendMessage(chat);

                        Image image = new Image(idFile, firebaseUser.getUid(), userId, timeNow, fileNameInFirebase);
                        saveInfoImage(image);

                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(MessageActivity.this,"False!", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            Toast.makeText(MessageActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void saveInfoImage(Image image){

        DatabaseReference audioRef = FirebaseDatabase.getInstance().getReference("ImageList")
                .child(firebaseUser.getUid())
                .child(image.getId());

        audioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("id", image.getId());
                    hashMap.put("sender", image.getSender());
                    hashMap.put("receiver", image.getReceiver());
                    hashMap.put("time", image.getTime());
                    hashMap.put("filename", image.getFilename());

                    audioRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MessageActivity.this, "saved info Image", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void getTimelineOK() {
        this.refresh = true;
    }

    @Override
    public void releaseFinish() {
        this.refresh = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case VIDEO_CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                    } else {
                        Toast.makeText(this, "Camera & Storage permission are required", Toast.LENGTH_LONG).show();
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void Video_UploadToFirebase() {
        videoProgressDialog.show();
        String fileName = String.format("video%d", System.currentTimeMillis());
        String filePath = String.format("uploads/videos/%s", fileName);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePath);
        storageReference.putFile(videoUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        Uri downloadUri = uriTask.getResult();
                        String timeNow = TimeUtil.getTimeNow();
                        if (uriTask.isSuccessful()) {
                            // Send chat message for video
                            Chat chat = new Chat(firebaseUser.getUid(), userId, false);
                            chat.setVideo(downloadUri.toString());
                            chat.setTime(timeNow);
                            chat.setIdfile(fileName);
                            sendMessage(chat);

                            // Add video detail to firebase database (Realtime Database)
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", fileName);
                            hashMap.put("sender", firebaseUser.getUid());
                            hashMap.put("receiver", userId);
                            hashMap.put("time", timeNow);
                            hashMap.put("filename", downloadUri);

                            // TODO: Error when add video detail to database
//                            DatabaseReference reference = FirebaseDatabase.getInstance("https://zalo-04-default-rtdb.firebaseio.com/").getReference("VideoList")
//                                    .child(firebaseUser.getUid())
//                                    .child(fileName);
//                            reference.setValue(hashMap)
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void unused) {
//                                            videoProgressDialog.dismiss();
//                                            Toast.makeText(MessageActivity.this, "Video uploaded.", Toast.LENGTH_SHORT).show();
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            videoProgressDialog.dismiss();
//                                            Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
//                                        }
//                                    });
                            videoProgressDialog.dismiss();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        videoProgressDialog.dismiss();
                        Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

}
package hcmute.edu.vn.zalo_04;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.edu.vn.zalo_04.adapter.MessageAdapter;
import hcmute.edu.vn.zalo_04.model.Audio;
import hcmute.edu.vn.zalo_04.model.Chat;
import hcmute.edu.vn.zalo_04.model.Image;
import hcmute.edu.vn.zalo_04.model.User;
import hcmute.edu.vn.zalo_04.util.TimeUtil;

public class MessageActivity extends AppCompatActivity {

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

    private ActivityResultLauncher<String> takePhoto;
    private ActivityResultLauncher<String> uploadPhoto;


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

        layout = findViewById(R.id.layout);  //specify here Root layout Id

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


        img_video.setOnClickListener(View -> {
            PopupMenu popupMenu = new PopupMenu(this, View);
            popupMenu.getMenuInflater().inflate(R.menu.menu_video, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.item_video_exists:
                            //test choi
                            //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UC5GCMUwYXZTMKVc8OBvLbvQ")));
                            return true;
                        case R.id.item_create_video:
                            Intent intent = new Intent(MessageActivity.this, SendAudioActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("userId", userId);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            return true;
                        case R.id.item_photo_exists:
                            uploadPhoto.launch("image/*");
                            return true;
                        case R.id.item_take_photo:
                            //test choi
                            //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UC5GCMUwYXZTMKVc8OBvLbvQ")));
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
                    profile_image.setImageResource(R.drawable.user_hao);
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

        takePhoto = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        //profile_img.setImageURI(result);
                        uploadPhoto.launch("image/*");
                    }
                });

        uploadPhoto = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        imageUri = result;

                        if (uploadTask != null && uploadTask.isInProgress()){
                            Toast.makeText(MessageActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                        } else {
                            uploadImage();
                        }
                    }
                });
    }


    private void seenMessage(final String userId){
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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
            String idFile = String.valueOf(calendar.getTimeInMillis());
            final StorageReference fileReference = storageReference.child(idFile
                    +"."+ getFileExtension(imageUri));

            String fileNameInFirebase = "image"+calendar.getTimeInMillis() + "." + getFileExtension(imageUri);
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
                        sendMessage(chat);

                        Image image = new Image(idFile, firebaseUser.getUid(), userId, timeNow);
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

}
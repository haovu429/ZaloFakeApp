package hcmute.edu.vn.zalo_04.task;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.zalo_04.MyInterface.IReleaseStorage;
import hcmute.edu.vn.zalo_04.model.Audio;
import hcmute.edu.vn.zalo_04.model.Chat;
import hcmute.edu.vn.zalo_04.model.FriendList;
import hcmute.edu.vn.zalo_04.model.Image;
import hcmute.edu.vn.zalo_04.model.Timeline;
import hcmute.edu.vn.zalo_04.util.TimeUtil;

public class ReleaseStorage implements IReleaseStorage{

    private String timeNow;

    private int day;
    private int month;
    private int year;

    private Timeline timeline_root;

    private FirebaseUser firebaseUser;
    private StorageReference storageReference;

    private Context context;

    public ReleaseStorage(FirebaseUser firebaseUser, Context context) {
        this.firebaseUser = firebaseUser;
        this.context = context;

    }
    public void readTimeline(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("TimelineList").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                timeline_root = snapshot.getValue(Timeline.class);

                if (timeline_root != null){
                    Log.d("TIMELINE", "not null");
                    getTimelineOK();
                } else {
                    //timeline = new Timeline(firebaseUser.getUid(), "0", "0", "0");
                    return;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void getTimelineOK() {
        Log.d("TIMELINE", "apply");
        if(timeline_root == null){
            return;
        }
        deleteImage(timeline_root);
        deleteAudio(timeline_root);
        releaseFinish();
    }

    @Override
    public void releaseFinish() {
        Log.d("TIMELINE", "release finish!");
    }

    public void deleteResource(){
        readTimeline();

        if (timeline_root == null){
            Log.d("TIMELINE", " null");
            //return;
        }
    }

    public void deleteImage(Timeline timeline){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ImageList").child(firebaseUser.getUid());
        List<Image> fileList = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                fileList.clear();
                for (DataSnapshot snapshot_index : snapshot.getChildren()){
                    Image image = snapshot_index.getValue(Image.class);
                    fileList.add(image);
                }
                Log.d("fileList","size fileList:"+fileList.size());
                for (Image image : fileList){
                    TimeUtil timeUtil = new TimeUtil();

                    day = Integer.parseInt(timeline.getDay_num());
                    month = Integer.parseInt(timeline.getMonth_num());
                    year = Integer.parseInt(timeline.getYear_num());

                    Log.d("TIMELINE","day:"+day +"--month:+"+month+"--year:"+year);

                    if (timeUtil.is_OutOfDate(image.getTime(), timeUtil.up_downTime(TimeUtil.getTimeNow(),day ,month,year))){
                        deleteImageFile(image.getFilename());
                        deleteChatInfoImage(image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void deleteAudio(Timeline timeline){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("AudioList").child(firebaseUser.getUid());
        List<Audio> fileList = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                fileList.clear();
                for (DataSnapshot snapshot_index : snapshot.getChildren()){
                    Audio audio = snapshot_index.getValue(Audio.class);
                    fileList.add(audio);
                }
                Log.d("fileList","size fileList:"+fileList.size());
                for (Audio audio : fileList){
                    TimeUtil timeUtil = new TimeUtil();

                    day = Integer.parseInt(timeline.getDay_num());
                    month = Integer.parseInt(timeline.getMonth_num());
                    year = Integer.parseInt(timeline.getYear_num());

                    if (timeUtil.is_OutOfDate(audio.getTime(), timeUtil.up_downTime(TimeUtil.getTimeNow(),day ,month,year))){
                        deleteAudioFile(audio.getFilename());
                        deleteChatInfoAudio(audio);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void deleteChatInfoImage(Image image){

        DatabaseReference reference_info = FirebaseDatabase.getInstance().getReference().child("ImageList")
                .child(firebaseUser.getUid())
                .child(image.getId());
        reference_info.removeValue();

        DatabaseReference reference_chat = FirebaseDatabase.getInstance().getReference().child("Chats");
        reference_chat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot_index : snapshot.getChildren()){
                    Chat chat = snapshot_index.getValue(Chat.class);
                    if (chat.getIdfile().equals(image.getId())){
                        snapshot_index.getRef().removeValue();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteChatInfoAudio(Audio audio){
        DatabaseReference reference_info = FirebaseDatabase.getInstance().getReference().child("AudioList")
                .child(firebaseUser.getUid())
                .child(audio.getId());
        reference_info.removeValue();

        DatabaseReference reference_chat = FirebaseDatabase.getInstance().getReference().child("Chats");
        reference_chat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshot_index : snapshot.getChildren()){
                    Chat chat = snapshot_index.getValue(Chat.class);
                    if (chat.getIdfile().equals(audio.getId())){
                        snapshot_index.getRef().removeValue();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void deleteImageFile(String filename){
        storageReference = FirebaseStorage.getInstance().getReference("uploads").child("image");

        StorageReference desertRef = storageReference.child(filename);

        // Delete the file
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                /*reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                HashMap<String, Object> map = new HashMap<>();
                map.put("imageURL", "default");
                reference.updateChildren(map);*/
                Toast.makeText(context, "Deleted image ok", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Toast.makeText(context, "We want to delete image, but an error occurred!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void deleteAudioFile(String filename){
        storageReference = FirebaseStorage.getInstance().getReference("uploads").child("audio");

        StorageReference desertRef = storageReference.child(filename);

        // Delete the file
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                /*reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                HashMap<String, Object> map = new HashMap<>();
                map.put("imageURL", "default");
                reference.updateChildren(map);*/
                Toast.makeText(context, "Deleted audio ok", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Toast.makeText(context, "We want to delete audio, but an error occurred!", Toast.LENGTH_SHORT).show();
            }
        });

    }


}


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
// Class đảm nhiệm nhiệm vụ giải phóng tài nguyên dựa vào thời gian do người dùng đặt
public class ReleaseStorage implements IReleaseStorage{

    //Biến thời gian hiện tại
    private String timeNow;

    //Biến lấy khoảng thời gian xoá tính bằng ngày do người dùng đặt >0 và <31
    private int day;

    //Biến lấy khoảng thời gian xoá tính bằng tháng do người dùng đặt >0 và <13
    private int month;

    //Biến lấy khoảng thời gian xoá tính bằng năm do người dùng đặt >0
    private int year;

    //Dạng dữ liệu chứa 3 thông tin ngày tháng năm ở trên từ Firebase
    private Timeline timeline_root;

    //Biến lưu người dùng hiện tại
    private FirebaseUser firebaseUser;

    //Ánh xạ Storage của firebase
    private StorageReference storageReference;

    //Dùng để hiển thị thông báo
    private Context context;

    //Hàm khai báo
    public ReleaseStorage(FirebaseUser firebaseUser, Context context) {
        this.firebaseUser = firebaseUser;
        this.context = context;

    }

    //Hàm lấy thông tin thời gian xoá dữ liệu do người dùng đặt được lưu trên Firebase
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
    //Hàm thực thi các công việc xoá dữu liệu khi đã lấy được thời gian do người dùng đặt trên firebase
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
    //Hàm ghi log lại khi đã hoàn thành việc giải phóng tài nguyên
    public void releaseFinish() {
        Log.d("TIMELINE", "release finish!");
    }

    //Hàm chỉ định đọc thời gian xoá tài nguyên của người dùng đặt, kiêm tra và ghi log thông tin Timeline nếu bị null
    public void deleteResource(){
        readTimeline();

        if (timeline_root == null){
            Log.d("TIMELINE", " null");
            //return;
        }
    }

    //Hàm xoá ảnh
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

                day = Integer.parseInt(timeline.getDay_num());
                month = Integer.parseInt(timeline.getMonth_num());
                year = Integer.parseInt(timeline.getYear_num());

                for (Image image : fileList){
                    TimeUtil timeUtil = new TimeUtil();

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
    //Hàm xoá Audio
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

                day = Integer.parseInt(timeline.getDay_num());
                month = Integer.parseInt(timeline.getMonth_num());
                year = Integer.parseInt(timeline.getYear_num());

                for (Audio audio : fileList){
                    TimeUtil timeUtil = new TimeUtil();

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

    //Hàm xoá thông tin của ảnh, tin nhắn trong realtime-database (Mục ImageList và Chats)
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

    //Hàm xoá thông tin của audio, tin nhắn trong realtime-database (Mục AudioList và Chats)
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

    //Xoá file ảnh trong storage của Firebase
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

    //Xoá file audio trong storage của Firebase
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


package hcmute.edu.vn.zalo_04.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.edu.vn.zalo_04.MessageActivity;
import hcmute.edu.vn.zalo_04.MyInterface.IClickAddFriend;
import hcmute.edu.vn.zalo_04.R;
import hcmute.edu.vn.zalo_04.model.Chat;
import hcmute.edu.vn.zalo_04.model.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    //Lưu id người dùng hiện tại
    private String currentUserId;

    //Sử dụng activity để laod ảnh bằng thư viện Glide
    private Activity activity;
    private List<User> userList; //Danh sách người dùng trong adapter
    private boolean ischat; //Trạng thái có đang xem boxchat hay không
    private DatabaseReference reference; //Ánh xạ tới Firebase database
    private String theLastMessage; //Tin nhắn cuối cùng trong box chat
    private boolean isfriend; //Kiểm tra tài khoản có phải là bạn với người dùng hay chưa

    private IClickAddFriend iClickAddFriend; // Khai báo sự kiện kết bạn

    //Hàm khởi tạo adapter
    public UserAdapter(Activity activity, List<User> userList, boolean ischat, boolean isfriend) {
        //this.context = context;
        this.activity = activity;
        this.userList = userList;
        this.ischat = ischat;
        this.isfriend = isfriend;
        notifyDataSetChanged();
    }
    //Hàm set dữ liệu cho adapter
    public void setData(List<User> userList){
        this.userList = userList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    //Hàm khởi tạo view
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    //Hàm đổ dữ liệu vào view
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        if (user == null){
            return;
        }

        //System.out.println("Adapter: " + user.getUsername());

        if (user.getImageURL().equals("default")){
            holder.profile_image.setImageResource(R.drawable.user_hao2);
        } else {
            Glide.with(activity).load(user.getImageURL()).into(holder.profile_image);
        }

        if (ischat){
            try{
                lastMessage(user, holder.last_msg);
            } catch (Exception e){
                activity.finish();
                activity.startActivity(activity.getIntent());
            }

        } else {
            holder.last_msg.setVisibility(View.GONE);
        }

        holder.username.setText(user.getUsername());

        if (ischat){
            if(user.getStatus() != null){
                if (user.getStatus().equals("online")){
                    holder.img_online.setVisibility(View.VISIBLE);
                    holder.img_offline.setVisibility(View.GONE);
                } else {
                    holder.img_online.setVisibility(View.GONE);
                    holder.img_offline.setVisibility(View.VISIBLE);
                }
            } else {
                reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getId());

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id", user.getId());
                hashMap.put("username", user.getUsername());
                hashMap.put("phone_number", user.getPhone_number());
                hashMap.put("imageURL", user.getImageURL());
                hashMap.put("status", "offline"); // add more

                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(activity, "Refresh status", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } else {
            holder.img_online.setVisibility(View.GONE);
            holder.img_offline.setVisibility(View.GONE);
        }

        if (isfriend){
            holder.img_friend.setVisibility(View.GONE);
        } else {
            holder.img_friend.setVisibility(View.VISIBLE);
            holder.img_friend.setOnClickListener(View ->{
                try {
                    iClickAddFriend.clickAddFriend();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                addFriendList(user.getId());
            });
        }

        holder.itemView.setOnClickListener(View -> {
            Intent intent = new Intent(activity, MessageActivity.class);
            intent.putExtra("userId", user.getId());
            activity.startActivity(intent);
        });

    }
    //Hàm load lại activity nếu như xảy ra lỗi khi kiêm tra tin nhắn cuối cùng
    private void refreshActivity(){
        activity.finish();
        activity.startActivity(activity.getIntent());
    }


    @Override
    //Lấy số lượng danh sách dữ liệu của adapter
    public int getItemCount() {
        if (userList != null){
            return  userList.size();
        }
        return 0;
    }
    //Tạo một lớp view holder tuỳ chỉnh để hiển thị User
    class UserViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView profile_image;
        private TextView username;
        private CircleImageView img_online;
        private CircleImageView img_offline;
        private TextView last_msg;
        private ImageView img_friend;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image = (CircleImageView) itemView.findViewById(R.id.profile_image);
            username = (TextView) itemView.findViewById(R.id.username);
            img_online = (CircleImageView) itemView.findViewById(R.id.img_online);
            img_offline = (CircleImageView) itemView.findViewById(R.id.img_offline);
            last_msg = (TextView) itemView.findViewById(R.id.last_message);
            img_friend = (ImageView) itemView.findViewById(R.id.img_add_friend);

        }
    }

    //Check for last massage
    private void lastMessage (User user, TextView last_msg){
        String userId = user.getId();
        theLastMessage = "default";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        try{
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot snapshot_index : snapshot.getChildren()){
                        Chat chat = snapshot_index.getValue(Chat.class);
                        if (chat == null){
                            return;
                        }

                        try{
                            if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId)){
                                theLastMessage = user.getUsername() + ": " + getLastMess(chat.getMessage());
                            } else {
                                if (chat.getReceiver().equals(userId) && chat.getSender().equals(firebaseUser.getUid())){
                                    theLastMessage = "Bạn: " + getLastMess(chat.getMessage());
                                }
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                            continue;
                            //snapshot_index.getRef().removeValue();
                        }

                    }

                    last_msg.setText(theLastMessage);


                /*switch (theLastMessage){
                    case "default":
                        last_msg.setText("No Message");
                        break;
                    case "None":
                        last_msg.setText("File");
                        break;
                    default:
                        last_msg.setText(theLastMessage);
                        break;
                }*/
                    //theLastMessage = "default";

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e){
            refreshActivity();
        }

    }

    //Lấy ra tin nhắn cuối cùng, nếu như là hình ảnh hay audio thì sẽ hiện "File", còn text thì sẽ hiện text
    private String getLastMess(String input){
        if (input.equals("default")){
            return "No Message";
        } else {
            if (input.equals("None")){
                return "File";
            } else {
                return input;
            }
        }
    }

    //Thêm một người vào danh sách bạn bè
    private void addFriendList(String added_userId){
        //add user to message fragment
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("FriendList")
                .child(currentUserId)
                .child(added_userId);

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRef.child("id").setValue(added_userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("FriendList")
                .child(added_userId)
                .child(currentUserId);

        chatRefReceiver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    chatRefReceiver.child("id").setValue(currentUserId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //Lấy id của người dùng hiện tại
    public String getCurrentUserId() {
        return currentUserId;
    }

    //hàm set Id người dùng hiện tại cho adapter
    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    //Hàm lấy ra sự kiện kết bạn (Interface)
    public IClickAddFriend getiClickAddFriend() {
        return iClickAddFriend;
    }

    //Hàm set sự kiện kết bạn( Interface)
    public void setiClickAddFriend(IClickAddFriend iClickAddFriend) {
        this.iClickAddFriend = iClickAddFriend;
    }
}

package hcmute.edu.vn.zalo_04.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.edu.vn.zalo_04.MainActivity;
import hcmute.edu.vn.zalo_04.MessageActivity;
import hcmute.edu.vn.zalo_04.R;
import hcmute.edu.vn.zalo_04.RegisterActivity;
import hcmute.edu.vn.zalo_04.model.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<User> userList;
    private boolean ischat;
    DatabaseReference reference;

    public UserAdapter(Context context, List<User> userList, boolean ischat) {
        this.context = context;
        this.userList = userList;
        this.ischat = ischat;
        notifyDataSetChanged();
    }

    public void setData(List<User> userList){
        this.userList = userList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        if (user == null){
            return;
        }

        //System.out.println("Adapter: " + user.getUsername());

        if (user.getImageURL().equals("default")){
            holder.profile_image.setImageResource(R.drawable.user_hao);
        } else {
            Glide.with(context).load(user.getImageURL()).into(holder.profile_image);
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
                        Toast.makeText(context, "Refresh status", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } else {
            holder.img_online.setVisibility(View.GONE);
            holder.img_offline.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(View -> {
            Intent intent = new Intent(context, MessageActivity.class);
            intent.putExtra("userId", user.getId());
            context.startActivity(intent);
        });

    }


    @Override
    public int getItemCount() {
        if (userList != null){
            return  userList.size();
        }
        return 0;
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView profile_image;
        private TextView username;
        private CircleImageView img_online;
        private CircleImageView img_offline;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image = (CircleImageView) itemView.findViewById(R.id.profile_image);
            username = (TextView) itemView.findViewById(R.id.username);
            img_online = (CircleImageView) itemView.findViewById(R.id.img_online);
            img_offline = (CircleImageView) itemView.findViewById(R.id.img_offline);
        }
    }
}

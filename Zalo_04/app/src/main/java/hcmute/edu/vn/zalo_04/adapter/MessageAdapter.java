package hcmute.edu.vn.zalo_04.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.edu.vn.zalo_04.MessageActivity;
import hcmute.edu.vn.zalo_04.R;
import hcmute.edu.vn.zalo_04.model.Chat;
import hcmute.edu.vn.zalo_04.model.User;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT= 1;

    private Context context;
    private List<Chat> chatList;
    private String imageURL;

    private FirebaseUser firebaseUser;

    public MessageAdapter(Context context, List<Chat> chatList, String imageURL) {
        this.context = context;
        this.chatList = chatList;
        this.imageURL = imageURL;
        notifyDataSetChanged();
    }

    public void setData(List<Chat> userList,String imageURL){
        this.chatList = userList;
        this.imageURL = imageURL;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_right, parent, false);
            return new MessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_left, parent, false);
            return new MessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        if (chat == null){
            return;
        }
        holder.show_message.setText(chat.getMessage());
        if (imageURL.equals("default")){
            holder.profile_image.setImageResource(R.drawable.user_hao);
        } else {
            Glide.with(context).load(imageURL).into(holder.profile_image);
        }

    }

    @Override
    public int getItemCount() {
        if (chatList != null){
            return  chatList.size();
        }
        return 0;
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView profile_image;
        private TextView show_message;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image = (CircleImageView) itemView.findViewById(R.id.profile_image);
            show_message = (TextView) itemView.findViewById(R.id.show_message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
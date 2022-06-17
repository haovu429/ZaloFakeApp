package hcmute.edu.vn.zalo_04.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.edu.vn.zalo_04.PlayAudioActivity;
import hcmute.edu.vn.zalo_04.R;
import hcmute.edu.vn.zalo_04.ShowImageActivity;
import hcmute.edu.vn.zalo_04.ShowVideoActivity;
import hcmute.edu.vn.zalo_04.model.Chat;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    //Khai báo các kiểu text để phân biệt khởi tạo giao diện, tái sủ dụng adapter
    public static final int MSG_TYPE_LEFT = 0; //Kiểu tin nhắn của đối tượng tài khoản đang chat cùng
    public static final int MSG_TYPE_RIGHT= 1; //Kiểu tin nhắn do người dùng gửi
    public static final int MSG_TYPE_IMAGE = 1; // Kiểu tin nhắn dạng hình ảnh
    public static final int MSG_TYPE_AUDIO= 2; //Kiểu tin nhắn dạng Audio
    public static final int MSG_TYPE_VIDEO = 3; //Kiểu tin nhắn dạng video
    public static final int MSG_TYPE_TEXT= 4; //Kiểu tin nhắn dạng text
    public static final int MSG_TYPE_DELETED= 5; //Kiểu tin nhắn bị xoá (Kiểu này khai báo dự trù, chưa dùng tới)

    private Context context; //sử dụng context để load ảnh bằng thư viện Glide
    private List<Chat> chatList; // Danh sách tin nhắn
    private String imageURL; //Link hình ảnh của người nhắn tin cùng

    private MediaPlayer mediaPlayer; //Chạy trực tiếp những đoạn audio có trong đoạn chat (chưa dùng tới)

    private FirebaseUser firebaseUser; //Khởi tạo đối tượng người dùng hiện tại (Firebase hỗ trợ)

    String testLink = "https://media5.sgp1.digitaloceanspaces.com/wp-content/uploads/2021/10/13142119/Stylish-Girls-Wallpapers.jpg";


    //Hàm khởi tao adapter
    public MessageAdapter(Context context, List<Chat> chatList, String imageURL) {
        this.context = context;
        this.chatList = chatList;
        this.imageURL = imageURL;
        notifyDataSetChanged();
    }

    //Hàm khởi tao adapter dùng vào trường hợp khác
    public void setData(List<Chat> userList,String imageURL){
        this.chatList = userList;
        this.imageURL = imageURL;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    //Khỏi tạo view, chọn layout dự vào kiểu tin nhắn trái hay phải
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
    //Đổ dữ liệu vào view theo từng kiểu tin nhắn, mỗi tin nhắn sẽ chỉ có 1 kiểu.
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int position) {
        Chat chat = chatList.get(position);

        if (chat == null){
            return;
        }

        switch (getType(chat)){
            case MSG_TYPE_TEXT:
                holder.layout_type_text.setVisibility(View.VISIBLE);
                holder.layout_type_image.setVisibility(View.GONE);
                holder.layout_type_audio.setVisibility(View.GONE);
                holder.layout_type_video.setVisibility(View.GONE);
                setUI_TextType(chat, holder);
                break;
            case MSG_TYPE_IMAGE:
                holder.layout_type_text.setVisibility(View.GONE);
                holder.layout_type_image.setVisibility(View.VISIBLE);
                holder.layout_type_audio.setVisibility(View.GONE);
                holder.layout_type_video.setVisibility(View.GONE);
                setUI_ImageType(chat, holder);
                break;
            case MSG_TYPE_AUDIO:
                holder.layout_type_text.setVisibility(View.GONE);
                holder.layout_type_image.setVisibility(View.GONE);
                holder.layout_type_audio.setVisibility(View.VISIBLE);
                holder.layout_type_video.setVisibility(View.GONE);
                setUI_AudioType(chat, holder);
                break;
            case MSG_TYPE_VIDEO:
                holder.layout_type_text.setVisibility(View.GONE);
                holder.layout_type_image.setVisibility(View.GONE);
                holder.layout_type_audio.setVisibility(View.GONE);
                holder.layout_type_video.setVisibility(View.VISIBLE);
                setUI_VideoType(chat, holder);
                break;
            default:
                holder.layout_type_text.setVisibility(View.VISIBLE);
                holder.layout_type_image.setVisibility(View.GONE);
                holder.layout_type_audio.setVisibility(View.GONE);
                holder.show_message.setText("The message has been deleted");

        }


        //check for last message
        if (position == chatList.size() - 1){

            if (chat.isIsseen()){
                holder.txt_seen.setText("Seen");
            } else {
                holder.txt_seen.setText("Delivered");
            }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }

    }

    @Override
    //Lấy số lượng tin nhắn
    public int getItemCount() {
        if (chatList != null){
            return  chatList.size();
        }
        return 0;
    }

    //Load giao diện (hàm phụ)
    private void setUI_ImageType(Chat chat, MessageViewHolder holder){

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);

        //hien thi anh dai dien
        if (imageURL.equals("default")){
            holder.profile_image2.setImageResource(R.drawable.user_hao2);
        } else {
            Glide.with(context).load(imageURL).into(holder.profile_image2);
        }

        //show file anh

        //Glide.with(context).load(options).into(holder.image_sent);
        try {
            Glide.with(context).load(chat.getImage()).apply(options).into(holder.image_sent);
        } catch (Exception e){
            //loi duong link sai
            e.printStackTrace();
        }

        holder.image_sent.setOnClickListener(View -> {
            Intent intent = new Intent(context, ShowImageActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("imageUrl", chat.getImage());
            intent.putExtras(bundle);
            context.startActivity(intent);
        });

    }

    //Set hình ảnh người gửi nếu là đối tượng chat cùng (trường hợp tin nhắn bên trái)
    private void setUI_TextType(Chat chat, MessageViewHolder holder){
        holder.show_message.setText(chat.getMessage());
        if (imageURL.equals("default")){
            holder.profile_image.setImageResource(R.drawable.user_hao2);
        } else {
            Glide.with(context).load(imageURL).into(holder.profile_image);
        }
    }

    //Cài đặt hiển thị cho dạng tin nhắn là Audio
    private void setUI_AudioType(Chat chat, MessageViewHolder holder){

        if (imageURL.equals("default")){
            holder.profile_image1.setImageResource(R.drawable.user_hao2);
        } else {
            Glide.with(context).load(imageURL).into(holder.profile_image1);
        }

        holder.type_file1.setText("Audio");

        holder.itemView.setOnClickListener(View -> {
            Intent intent = new Intent(context, PlayAudioActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("audioUrl", chat.getAudio());
            intent.putExtras(bundle);
            context.startActivity(intent);
        });
    }

    //Cài đặt hiển thị cho dạng tin nhắn là video
    private void setUI_VideoType(Chat chat, MessageViewHolder holder) {
        if (imageURL.equals("default")){
            holder.profile_image3.setImageResource(R.drawable.user_hao2);
        } else {
            Glide.with(context).load(imageURL).into(holder.profile_image3);
        }

        holder.type_file1.setText("Video");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ShowVideoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("videoUrl", chat.getVideo());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    //Tạo ViewHolder được tuỳ chỉnh theo đối tượng Message
    class MessageViewHolder extends RecyclerView.ViewHolder {

        //Các vị trí hình ảnh đại diện của tài khản chat cùng trong giao diện một tin nhắn
        private CircleImageView profile_image, profile_image1, profile_image2, profile_image3;
        //Kiểu tin nhắn text
        private TextView show_message;

        //Kiểu tin nhắn text
        public TextView txt_seen, type_file1, type_file2;

        //Khai báo dự phòng trường hợp cải thiện cách chạy audio trực tiếp trong màn hình chat
        private ImageView control_audio;
        private SeekBar progress;
        private TextView title, time;

        //Trường hợp tin nhắn là ảnh, khởi tạo một frame ảnh để hiển thị
        private ImageView image_sent;

        //Tách view thành 4 thành phần audio, video, text, image để dễ quản lý
        private LinearLayout layout_type_audio;
        private LinearLayout layout_type_video;
        private RelativeLayout layout_type_text;
        private LinearLayout layout_type_image;


        //Ánh xạ các thành phần của một MassageViewHolder
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image = (CircleImageView) itemView.findViewById(R.id.profile_image);
            show_message = (TextView) itemView.findViewById(R.id.show_message);
            txt_seen = (TextView) itemView.findViewById(R.id.txt_seen);
            control_audio = (ImageView) itemView.findViewById(R.id.control_audio);
            progress = (SeekBar) itemView.findViewById(R.id.progress);
            time = (TextView) itemView.findViewById(R.id.time);

            layout_type_image = (LinearLayout) itemView.findViewById(R.id.layout_type_image);
            layout_type_text = (RelativeLayout) itemView.findViewById(R.id.layout_type_text);
            layout_type_audio = (LinearLayout) itemView.findViewById(R.id.layout_item_audio);
            layout_type_video = (LinearLayout) itemView.findViewById(R.id.layout_item_video);

            type_file1 = (TextView) itemView.findViewById(R.id.type_file1);
            profile_image1 = (CircleImageView) itemView.findViewById(R.id.profile_image1);
            profile_image2 = (CircleImageView) itemView.findViewById(R.id.profile_image2);
            profile_image3 = (CircleImageView) itemView.findViewById(R.id.profile_image3);

            image_sent = (ImageView) itemView.findViewById(R.id.image_sent);
        }
    }

    @Override
    //Lấy ra kiểu tin nhắn bên trái hay bên phải dự vào nhận dạng người dùng hiện tại là người gửi hay
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    //Lấy ra dạng của tin nhắn
    private int getType(Chat chat){
        if (!chat.getMessage().equals("None")){
            return MSG_TYPE_TEXT;
        }
        if (!chat.getImage().equals("None")){
            return MSG_TYPE_IMAGE;
        }
        if (!chat.getAudio().equals("None")){
            return MSG_TYPE_AUDIO;
        }
        if (!chat.getVideo().equals("None")){
            return MSG_TYPE_VIDEO;
        }
        return MSG_TYPE_DELETED;
    }


}
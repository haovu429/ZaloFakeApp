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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.edu.vn.zalo_04.MessageActivity;
import hcmute.edu.vn.zalo_04.PlayAudioActivity;
import hcmute.edu.vn.zalo_04.R;
import hcmute.edu.vn.zalo_04.ShowImageActivity;
import hcmute.edu.vn.zalo_04.model.Chat;
import hcmute.edu.vn.zalo_04.model.User;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT= 1;
    public static final int MSG_TYPE_IMAGE = 1;
    public static final int MSG_TYPE_AUDIO= 2;
    public static final int MSG_TYPE_VIDEO = 3;
    public static final int MSG_TYPE_TEXT= 4;
    public static final int MSG_TYPE_DELETED= 5;

    private Context context;
    private List<Chat> chatList;
    private String imageURL;

    private MediaPlayer mediaPlayer;

    private FirebaseUser firebaseUser;

    String testLink = "https://media5.sgp1.digitaloceanspaces.com/wp-content/uploads/2021/10/13142119/Stylish-Girls-Wallpapers.jpg";

    private String duration;
    //private SeekBar progress;

    private ScheduledExecutorService timer;

    private ActivityResultLauncher<String> pick_audio;

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

        switch (getType(chat)){
            case MSG_TYPE_TEXT:
                holder.layout_type_text.setVisibility(View.VISIBLE);
                holder.layout_type_image.setVisibility(View.GONE);
                holder.layout_type_audio.setVisibility(View.GONE);
                setUI_TextType(chat, holder);
                break;
            case MSG_TYPE_IMAGE:
                holder.layout_type_text.setVisibility(View.GONE);
                holder.layout_type_image.setVisibility(View.VISIBLE);
                holder.layout_type_audio.setVisibility(View.GONE);
                setUI_ImageType(chat, holder);
                break;
            case MSG_TYPE_AUDIO:
                holder.layout_type_text.setVisibility(View.GONE);
                holder.layout_type_image.setVisibility(View.GONE);
                holder.layout_type_audio.setVisibility(View.VISIBLE);

                setUI_AudioType(chat, holder);
                break;
            case MSG_TYPE_VIDEO:
                holder.layout_type_text.setVisibility(View.GONE);
                holder.layout_type_image.setVisibility(View.GONE);
                holder.layout_type_audio.setVisibility(View.GONE);
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
    public int getItemCount() {
        if (chatList != null){
            return  chatList.size();
        }
        return 0;
    }

    private void setUI_ImageType(Chat chat, MessageViewHolder holder){

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);

        //hien thi anh dai dien
        if (imageURL.equals("default")){
            holder.profile_image2.setImageResource(R.drawable.user_hao);
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

    private void setUI_TextType(Chat chat, MessageViewHolder holder){
        holder.show_message.setText(chat.getMessage());
        if (imageURL.equals("default")){
            holder.profile_image.setImageResource(R.drawable.user_hao);
        } else {
            Glide.with(context).load(imageURL).into(holder.profile_image);
        }
    }

    private void setUI_AudioType(Chat chat, MessageViewHolder holder){

        if (imageURL.equals("default")){
            holder.profile_image1.setImageResource(R.drawable.user_hao);
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

    class MessageViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView profile_image, profile_image1, profile_image2, profile_image3;
        private TextView show_message;

        public TextView txt_seen, type_file1, type_file2;

        private ImageView control_audio;
        private SeekBar progress;

        private ImageView image_sent;
        private LinearLayout layout_type_audio;
        private RelativeLayout layout_type_text;
        private LinearLayout layout_type_image;

        private TextView title, time;

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

            type_file1 = (TextView) itemView.findViewById(R.id.type_file1);
            profile_image1 = (CircleImageView) itemView.findViewById(R.id.profile_image1);
            profile_image2 = (CircleImageView) itemView.findViewById(R.id.profile_image2);

            image_sent = (ImageView) itemView.findViewById(R.id.image_sent);
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

    private void createMediaPlayer(Uri uri, MessageViewHolder holder) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );

        try {
            mediaPlayer.setDataSource(this.context, uri);
            mediaPlayer.prepare();


            int millis = mediaPlayer.getDuration();
            long total_secs = TimeUnit.SECONDS.convert(millis, TimeUnit.MILLISECONDS);
            long mins = TimeUnit.MINUTES.convert(total_secs, TimeUnit.SECONDS);
            long secs = total_secs - (mins*60);

            duration = mins + ":" + secs;
            holder.progress.setMax(millis);
            holder.progress.setProgress(0);

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    releaseMediaPlayer(holder);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("Range")
    private String getNameFromUri(Uri uri) {
        String fileName = "";
        Cursor cursor = null;
        cursor = context.getContentResolver().query(uri, new String[]{
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

/*    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }*/

    public void releaseMediaPlayer(MessageViewHolder holder){
        if (holder.time != null) {
            timer.shutdown();
        }

        if (mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
        holder.control_audio.setEnabled(false);
        //title.setText("Title");
        holder.time.setText("00:00 / 00:00");
        holder.progress.setMax(100);
        holder.progress.setProgress(0);
    }
}
package hcmute.edu.vn.zalo_04.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hcmute.edu.vn.zalo_04.R;
import hcmute.edu.vn.zalo_04.model.ItemChatUI;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<ItemChatUI> itemChatUIList;


    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ItemChatUI itemChatUI = itemChatUIList.get(position);
        if( itemChatUI == null)
            return;

        holder.img.setImageResource(itemChatUI.getImg_id());
        holder.title.setText(itemChatUI.getTitle());
        if(itemChatUI.getLast_time() != null){
            holder.last_time.setText(itemChatUI.getLast_time());
        }

        if(itemChatUI.getLast_message() != null){
            holder.last_message.setText(itemChatUI.getLast_message());
        }

    }

    @Override
    public int getItemCount() {
        if(itemChatUIList != null){
            return itemChatUIList.size();
        }
        return 0;
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private TextView title;
        private TextView last_time;
        private TextView last_message;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img_chat);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            last_time = (TextView) itemView.findViewById(R.id.tv_last_time);
            last_message = (TextView) itemView.findViewById(R.id.tv_last_message);

        }
    }

    public void setData(List<ItemChatUI> itemChatUIList){
        this.itemChatUIList = itemChatUIList;
        System.out.println(itemChatUIList.size());
        notifyDataSetChanged();
    }

}

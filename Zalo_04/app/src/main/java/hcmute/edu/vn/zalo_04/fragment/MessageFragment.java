package hcmute.edu.vn.zalo_04.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hcmute.edu.vn.zalo_04.R;
import hcmute.edu.vn.zalo_04.adapter.ChatAdapter;
import hcmute.edu.vn.zalo_04.adapter.UserAdapter;
import hcmute.edu.vn.zalo_04.model.Chat;
import hcmute.edu.vn.zalo_04.model.ChatList;
import hcmute.edu.vn.zalo_04.model.ItemChatUI;
import hcmute.edu.vn.zalo_04.model.User;


public class MessageFragment extends Fragment {

    private View view;

    private ImageView img_find, img_qr_scan, img_add;
    private TextView tv_find;
    private RecyclerView rcv_chat;

    private UserAdapter userAdapter;
    private List<User> userList;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    //upgrade
    //private List<String> str_usersList;
    private List<ChatList> chatL_usersList;


    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_message, container, false);

        setupUI();

        return view;
    }

    private void setupUI(){
        AnhXa();

        //ChatAdapter chatAdapter = new ChatAdapter();


        //List<ItemChatUI> itemChatUIList = new ArrayList<>();


        //chatAdapter.setData(getData());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        rcv_chat.setLayoutManager(linearLayoutManager);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        chatL_usersList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatL_usersList.clear();
                for (DataSnapshot snapshot_index : snapshot.getChildren()){
                    ChatList chatList = snapshot_index.getValue(ChatList.class);
                    chatL_usersList.add(chatList);
                }
                addChatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /*//upgrade
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                str_usersList.clear();

                for (DataSnapshot snapshot_index : snapshot.getChildren()){
                    Chat chat = snapshot_index.getValue(Chat.class);

                    if (chat.getSender().equals(firebaseUser.getUid())){
                        str_usersList.add(chat.getReceiver());
                    }

                    if (chat.getReceiver().equals(firebaseUser.getUid())){
                        str_usersList.add(chat.getSender());
                    }
                }
                
                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        //rcv_chat.setAdapter(chatAdapter);

    }

    private void addChatList() {
        userList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot snapshot_index : snapshot.getChildren()){
                    User user = snapshot_index.getValue(User.class);
                    for (ChatList chatList : chatL_usersList){
                        if (user.getId().equals(chatList.getId())){
                            userList.add(user);
                        }
                    }
                }
                userAdapter = new UserAdapter(getActivity(), userList, true, true);
                rcv_chat.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //upgrade
   /* private void readChats(){

        userList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();

                for(DataSnapshot snapshot_index : snapshot.getChildren()){
                    User user = snapshot_index.getValue(User.class);

                    //System.out.println(user.getUsername());

                    //Display 1 user from chats
                    for (String id : str_usersList){
                        if (user.getId().equals(id)){
                            if (userList.size() != 0){
                                boolean exists = false;
                                // If not exists then add
                                for (User user1 : userList){
                                    if (user.getId().equals(user1.getId()))   {
                                        exists = true;
                                    }
                                }
                                if (!exists){
                                    userList.add(user);
                                }
                            } else {
                                userList.add(user);
                            }
                        }
                    }
                }

                userAdapter = new UserAdapter(getContext(), userList, true);
                rcv_chat.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }*/

    private void AnhXa(){
        img_find = view.findViewById(R.id.img_find);
        img_qr_scan = view.findViewById(R.id.img_qr_scan);
        img_add = view.findViewById(R.id.img_add);
        tv_find = view.findViewById(R.id.tv_find);
        rcv_chat =view.findViewById(R.id.rcv_chat);
    }

    private List<ItemChatUI> getData(){
        List<ItemChatUI> itemChatUIList = new ArrayList<>();
        ItemChatUI itemChatUI1 = new ItemChatUI(R.drawable.user_hao,"Hao dep trai dang doi ban rep.");
        ItemChatUI itemChatUI2 =new ItemChatUI(R.drawable.user_hao, "Bảo mật web cô Châu.");

        itemChatUIList.add(itemChatUI1);
        itemChatUIList.add(itemChatUI2);

        return itemChatUIList;
    }


}
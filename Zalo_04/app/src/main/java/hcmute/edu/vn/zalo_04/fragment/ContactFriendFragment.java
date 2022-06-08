package hcmute.edu.vn.zalo_04.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.zalo_04.R;
import hcmute.edu.vn.zalo_04.adapter.UserAdapter;
import hcmute.edu.vn.zalo_04.model.ChatList;
import hcmute.edu.vn.zalo_04.model.Contact;
import hcmute.edu.vn.zalo_04.model.FriendList;
import hcmute.edu.vn.zalo_04.model.User;


public class ContactFriendFragment extends Fragment {

    private View view;

    private ImageView img_find, img_qr_scan, img_add;
    private TextView tv_find;
    private RecyclerView rcv_friended;

    private UserAdapter userAdapter;
    private List<User> userList;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    //upgrade
    //private List<String> str_usersList;
    private List<FriendList> friendL_usersList;

    private EditText search_users;

    public ContactFriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_contact_friend, container, false);

        setupUI();

        return view;
    }

    private void setupUI(){
        AnhXa();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        userList = new ArrayList<>();

        rcv_friended.setLayoutManager(linearLayoutManager);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        friendL_usersList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("FriendList").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendL_usersList.clear();
                for (DataSnapshot snapshot_index : snapshot.getChildren()){
                    FriendList friendList = snapshot_index.getValue(FriendList.class);
                    friendL_usersList.add(friendList);
                }
                addFriendList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        search_users = view.findViewById(R.id.search_users);
        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void searchUsers(String toString) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(toString)
                .endAt(toString + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot snapshot_index : snapshot.getChildren()) {
                    User user = snapshot_index.getValue(User.class);

                    assert user != null;
                    assert firebaseUser != null;
                    if (!user.getId().equals(firebaseUser.getUid())) {
                        userList.add(user);
                    }
                }

                List<User> user_selected = new ArrayList<>();
                for (User user : userList){
                    for (FriendList friend :  friendL_usersList){
                        if( user.getId().equals(friend.getId())){
                            user_selected.add(user);
                        }
                    }
                }

                userAdapter.setData(user_selected);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addFriendList() {
        userList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot snapshot_index : snapshot.getChildren()){
                    User user = snapshot_index.getValue(User.class);
                    for (FriendList friendList : friendL_usersList){
                        if (user.getId().equals(friendList.getId())){
                            userList.add(user);
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(), userList, false, true);
                rcv_friended.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void AnhXa(){
        img_find = view.findViewById(R.id.img_find);
        img_qr_scan = view.findViewById(R.id.img_qr_scan);
        img_add = view.findViewById(R.id.img_add);
        tv_find = view.findViewById(R.id.tv_find);
        rcv_friended =view.findViewById(R.id.rcv_users);
    }

}
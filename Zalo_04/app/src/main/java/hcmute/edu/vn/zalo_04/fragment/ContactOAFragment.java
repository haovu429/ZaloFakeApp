package hcmute.edu.vn.zalo_04.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.zalo_04.MyInterface.IClickAddFriend;
import hcmute.edu.vn.zalo_04.R;
import hcmute.edu.vn.zalo_04.adapter.UserAdapter;
import hcmute.edu.vn.zalo_04.model.FriendList;
import hcmute.edu.vn.zalo_04.model.User;

public class ContactOAFragment extends Fragment {

    private View view;

    private ImageView img_find, img_qr_scan, img_add;
    private TextView tv_find;
    private RecyclerView rcv_unfriended;

    private UserAdapter userAdapter;
    private List<User> userList;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    //upgrade
    //private List<String> str_usersList;
    private List<FriendList> friendL_usersList;

    private EditText search_users;


    public ContactOAFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_contact_o_a, container, false);

        setupUI();

        return view;
    }
    private void setupUI(){

        AnhXa();

        userList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        rcv_unfriended.setLayoutManager(linearLayoutManager);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        friendL_usersList = new ArrayList<>();

        userAdapter = new UserAdapter(getActivity(), userList, false, false);
        userAdapter.setCurrentUserId(firebaseUser.getUid());
        userAdapter.setiClickAddFriend(new IClickAddFriend() {
            @Override
            public void clickAddFriend() throws InterruptedException {
                //setupUI();
                //userAdapter.setData(contactHasJoined);
            }
        });
        rcv_unfriended.setAdapter(userAdapter);


        search_users = view.findViewById(R.id.search_users);
        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchUsers(editable.toString());
            }
        });

    }

    private void searchUsers(String toString) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot snapshot_index : snapshot.getChildren()) {
                    User user = snapshot_index.getValue(User.class);

                    assert user != null;
                    assert firebaseUser != null;
                    if (!user.getId().equals(firebaseUser.getUid())) {
                        if (user.getPhone_number().equals(toString)){
                            userList.add(user);
                        }
                    }
                }

                //System.out.println(userList.size());
                userAdapter.setData(userList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addFriendList(String added_userId) {
        //add user to message fragment
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("FriendList")
                .child(firebaseUser.getUid())
                .child(added_userId);

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    chatRef.child("id").setValue(added_userId);
                }
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
        rcv_unfriended =view.findViewById(R.id.rcv_users);
    }
}
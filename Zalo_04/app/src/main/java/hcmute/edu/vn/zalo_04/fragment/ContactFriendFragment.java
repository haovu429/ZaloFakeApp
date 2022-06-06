package hcmute.edu.vn.zalo_04.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.UnknownServiceException;
import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.zalo_04.R;
import hcmute.edu.vn.zalo_04.adapter.UserAdapter;
import hcmute.edu.vn.zalo_04.model.User;


public class ContactFriendFragment extends Fragment {

    private View view;
    private UserAdapter userAdapter;
    private List<User> userList;

    private RecyclerView rcv_users;

    public ContactFriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_contact_friend, container, false);

        rcv_users = view.findViewById(R.id.rcv_users);
        rcv_users.setHasFixedSize(true);
        rcv_users.setLayoutManager(new LinearLayoutManager(getContext()));

        userList = new ArrayList<>();

        userAdapter = new UserAdapter(getContext(), userList, false);
        readUsers();
        rcv_users.setAdapter(userAdapter);

        return view;
    }

    private void readUsers() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot snapshot_index : snapshot.getChildren()){
                    User user = snapshot_index.getValue(User.class);

                    if(!user.getId().equals(firebaseUser.getUid())){
                        userList.add(user);
                    }
                }
                userAdapter.setData(userList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
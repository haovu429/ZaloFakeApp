package hcmute.edu.vn.zalo_04.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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

import hcmute.edu.vn.zalo_04.MyInterface.IClickAddFriend;
import hcmute.edu.vn.zalo_04.R;
import hcmute.edu.vn.zalo_04.adapter.UserAdapter;
import hcmute.edu.vn.zalo_04.model.Contact;
import hcmute.edu.vn.zalo_04.model.FriendList;
import hcmute.edu.vn.zalo_04.model.User;


public class ContactGroupFragment extends Fragment {

    public static final int RequestPermissionCode = 1;

    private Activity activity;

    private View view;
    private UserAdapter userAdapter;
    private List<User> userList;

    private List<User> contactHasJoined;

    private RecyclerView rcv_contact_unfriended;

    private List<Contact> contactList;
    private List<FriendList> friendL_usersList;

    private EditText search_users;

    private ActivityResultLauncher activityResultLauncher;

    //https://stackoverflow.com/questions/66475027/activityresultlauncher-with-requestmultiplepermissions-contract-doesnt-show-per
    final String[] PERMISSIONS = {

            Manifest.permission.READ_CONTACTS

    };
    private ActivityResultContracts.RequestMultiplePermissions multiplePermissionsContract;
    private ActivityResultLauncher<String[]> multiplePermissionLauncher;


    public ContactGroupFragment() {
        // Required empty public constructor
    }


    //Cấp quyền
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_contact_group, container, false);

        multiplePermissionsContract = new ActivityResultContracts.RequestMultiplePermissions();
        multiplePermissionLauncher = registerForActivityResult(multiplePermissionsContract, isGranted -> {
            Log.d("PERMISSIONS", "Launcher result: " + isGranted.toString());
            if (isGranted.containsValue(false)) {
                Log.d("PERMISSIONS", "At least one of the permissions was not granted, launching again...");
                multiplePermissionLauncher.launch(PERMISSIONS);
            }
        });

        askPermissions(multiplePermissionLauncher);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback() {
            @Override
            public void onActivityResult(Object result) {
                ActivityResult AcResult = (ActivityResult) result;
                if (AcResult.getResultCode() == RESULT_OK && AcResult.getData() != null) {
                    Bundle bundle = AcResult.getData().getExtras();

                }
            }

        });

        userAdapter = new UserAdapter(getContext(), userList, false, false);
        userAdapter.setiClickAddFriend(new IClickAddFriend() {
            @Override
            public void clickAddFriend() throws InterruptedException {
                //Thread.sleep(2000);
                Log.d("loi", "Bat dau click" );
                setupUI();
                Log.d("loi", "setup UI + " + contactHasJoined.size());
                //userAdapter.setData(contactHasJoined);
            }
        });

        friendL_usersList = new ArrayList<>();
        userList = new ArrayList<>();
        contactHasJoined = new ArrayList<>();

        setupUI();

        return view;
    }

    private void setupUI(){
        activity = getActivity();

        rcv_contact_unfriended = view.findViewById(R.id.rcv_users);
        rcv_contact_unfriended.setHasFixedSize(true);
        rcv_contact_unfriended.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //for add friend
        userAdapter.setCurrentUserId(firebaseUser.getUid());

        rcv_contact_unfriended.setAdapter(userAdapter);
        readUsers();


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

    private void getFriendList(){

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null){
            return;
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("FriendList").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendL_usersList.clear();
                for (DataSnapshot snapshot_index : snapshot.getChildren()){
                    FriendList friendList = snapshot_index.getValue(FriendList.class);
                    friendL_usersList.add(friendList);
                }
                //addFriendList();
                Log.d("loi", "Size1: "+friendL_usersList.size());
                Log.d("loi", "Size contact2: "+contactHasJoined.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                contactHasJoined.clear();
                contactList = getPhoneContacts();
                getFriendList();
                //Log.d("loi", "Ban: "+friendL_usersList.size());

                // Danh sach nguoi dung ung dung
                for (User user : userList){
                    //loc theo danh sach dien thoai
                    for (Contact contact : contactList){
                        if (user.getPhone_number().equals(contact.getPhone_number())){
                            //Loc theo danh sach ban be
                            boolean isFriend = false;
                            if ( friendL_usersList.size()>0 ){
                                for (FriendList friend : friendL_usersList){
                                    if (user.getId().equals(friend.getId())){
                                        //Log.d("loi", "Ban: "+friend.getId());
                                        isFriend = true;
                                        break;
                                    }
                                }
                                if (!isFriend){
                                    contactHasJoined.add(user);
                                }
                            } else {
                                contactHasJoined.add(user);
                            }
                        }
                    }
                }
                Log.d("loi", "Size contact: "+contactHasJoined.size());
                userAdapter.setData(contactHasJoined);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void readUsers() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (search_users.getText().toString().equals("")) {
                    userList.clear();
                    for (DataSnapshot snapshot_index : snapshot.getChildren()){
                        User user = snapshot_index.getValue(User.class);

                        assert firebaseUser != null;
                        assert user != null;
                        if(!user.getId().equals(firebaseUser.getUid())){
                            userList.add(user);
                        }
                    }

                    contactHasJoined.clear();
                    contactList = getPhoneContacts();
                    getFriendList();
                    Log.d("loi", "Ban size: "+friendL_usersList.size());
                    // Danh sach nguoi dung ung dung
                    for (User user : userList){
                        //loc theo danh sach dien thoai
                        for (Contact contact : contactList){
                            if (user.getPhone_number().equals(contact.getPhone_number())){
                                //Loc theo danh sach ban be
                                boolean isFriend = false;
                                if (friendL_usersList.size()>0 ){
                                    for (FriendList friend : friendL_usersList){
                                        if (user.getId().equals(friend.getId())){
                                            //Log.d("loi", "Ban: "+friend.getId());
                                            isFriend = true;
                                            break;
                                        }
                                    }
                                    if (!isFriend){
                                        contactHasJoined.add(user);
                                    }
                                } else {
                                    contactHasJoined.add(user);
                                }
                            }
                        }
                    }
                    Log.d("loi", "Size contact: "+contactHasJoined.size());
                    userAdapter.setData(contactHasJoined);
                    //userAdapter.setData(userList);
                }
                //userAdapter = new UserAdapter(getContext(), userList, false);
                //rcv_users.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("Range")
    private List<Contact> getPhoneContacts() {

        List<Contact> contacts = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            //ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.READ_CONTACTS}, 1);
            askPermissions(multiplePermissionLauncher);
        }

        ContentResolver contentResolver = activity.getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        @SuppressLint("Recycle") Cursor cursor = contentResolver.query(uri, null, null, null, null);
        Log.i("Contact_Provider_demo", "Total # of contact :" + Integer.toString(cursor.getCount()));
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()){
                Contact contact = new Contact();
                contact.setName(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                String phone_number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phone_number.replace(" ","");
                contact.setPhone_number(phone_number);

                //Log.i("Contact_Provider_demo", "Name :" + contact.getName() + "phone: " + contact.getPhone_number());
                contacts.add(contact);
            }
        }

        return contacts;
    }

    //Test get permission
    /*@RequiresApi(api = Build.VERSION_CODES.R)
    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO, READ_EXTERNAL_STORAGE}, 1);//, WRITE_EXTERNAL_STORAGE
    }*/

    /*@Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean ReadStorePermission = grantResults[2] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission && ReadStorePermission) {
                        Toast.makeText(getContext().getApplicationContext(), "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext().getApplicationContext(), "Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }*/

    /*@RequiresApi(api = Build.VERSION_CODES.R)
    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext().getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getContext().getApplicationContext(),
                RECORD_AUDIO);
        int result2 = ContextCompat.checkSelfPermission(getContext().getApplicationContext(),
                READ_EXTERNAL_STORAGE);
        System.out.println("quyen ql:"+result);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED &&
                result2 == PackageManager.PERMISSION_GRANTED;
    }*/

    private boolean hasPermissions(String[] permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSIONS", "Permission is not granted: " + permission);
                    return false;
                }
                Log.d("PERMISSIONS", "Permission already granted: " + permission);
            }
            return true;
        }
        return false;
    }

    private void askPermissions(ActivityResultLauncher<String[]> multiplePermissionLauncher) {
        if (!hasPermissions(PERMISSIONS)) {
            Log.d("PERMISSIONS", "Launching multiple contract permission launcher for ALL required permissions");
            multiplePermissionLauncher.launch(PERMISSIONS);
            hasPermissions(PERMISSIONS);
        } else {
            Log.d("PERMISSIONS", "All permissions are already granted");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setupUI();
    }
}
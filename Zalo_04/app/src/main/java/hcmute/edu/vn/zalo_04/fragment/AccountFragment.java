package hcmute.edu.vn.zalo_04.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.edu.vn.zalo_04.R;
import hcmute.edu.vn.zalo_04.SetDeleteTimelineActivity;
import hcmute.edu.vn.zalo_04.model.Timeline;
import hcmute.edu.vn.zalo_04.model.User;
import hcmute.edu.vn.zalo_04.util.TimeUtil;

public class AccountFragment extends Fragment {

    private static final int IMAGE_REQUEST = 1; // Request code cho openImage()

    private View view; // View của AccountFragment
    private TextView tv_userName, tv_address, edit_email, release_time; // TextView cho UserName, Địa chỉ, Email, Thời gian release resources
    private EditText edit_phone, edit_address; // EditText cho SĐT, Địa chỉ
    private Button btnUpdate, btn_change_pass, btn_edit_info;

    private ImageView img_logout; // ImageView cho đăng xuất / logout
    private CircleImageView profile_img; // CircleImageView cho hình avatar

    private DatabaseReference reference; // DatabaseReference cho Firebase
    private FirebaseUser firebaseUser; // FirebaseUser hiện tại đang đăng nhập
    private StorageReference storageReference; // StorageReference cho Firebase

    private Uri imageUri; // Uri cho hình avatar
    private StorageTask uploadTask; // StorageTask để upload hình avatar

    private RelativeLayout layout; // RelativeLayout của AccountFragment

    private ProgressBar progressBar; // ProgressBar của layout hiện tại

    private ActivityResultLauncher<String> takePhoto; // ActivityResultLauncher để lấy image từ gallery
    private ActivityResultLauncher<String> uploadPhoto; // ActivityResultLauncher để upload image lên Firebase

    private User currentUser; // User hiện tại

    private Timeline timeline_root; // Timeline để xoá resources quá hạn

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override // Override hàm onCreateView() của Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_account, container, false);
        //UserService userService = new UserService();
        //user = userService.getOne(idUser);

        AnhXa();
        setupUI();

        return view;
    }

    // TODO: Unused function
    private void onClickGoToChangePass(User user) {
//        Intent intent = new Intent(getContext(), ChangePassActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("object_user", user);
//        intent.putExtras(bundle);
//        getContext().startActivity(intent);
    }

    // Setup UI cho AccountFragment
    void setupUI() {
        // Lấy storageReference từ FirebaseStorage
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        // Lấy user hiện tại từ FirebaseAuth
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // Lấy DatabaseReference từ FirebaseDatabase
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { // Event listener khi data thay đổi
                currentUser = snapshot.getValue(User.class);
                //tv_userName.setText(currentUser.getUsername());
                setInfoAccount(currentUser, firebaseUser.getEmail());
                if (currentUser.getImageURL().equals("default")) { // Nếu image avatar chưa có
                    profile_img.setImageResource(R.drawable.user_hao2);
                } else { // Đã có image avatar
                    if (getContext() == null) {
                        return;
                    }
                    Glide.with(getContext()).load(currentUser.getImageURL()).into(profile_img);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { // Event listener khi cancel

            }
        });

        profile_img.setOnClickListener(View -> {
            //openImage();
            //takePhoto.launch("image/*");
            uploadPhoto.launch("image/*");
        });

        //tv_userName.setText(user.getUsername());
        //tv_address.setText(user.getAddress());
        //edit_address.setText(user.getAddress());
        //edit_phone.setText(user.getPhone());

        //Vô hiệu hoá sửa địa chỉ
        edit_address.setFocusable(false);
        edit_address.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        edit_address.setClickable(false); // user navigates with wheel and selects widget

        //Vô hiệu hoá sửa phone
        edit_phone.setFocusable(false);
        edit_phone.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        edit_phone.setClickable(false); // user navigates with wheel and selects widget


        btnUpdate.setOnClickListener(View -> {
            //CreateData createData = new CreateData();
            //createData.createDB();
            startActivity(new Intent(getContext(), SetDeleteTimelineActivity.class));
        });

        btn_change_pass.setOnClickListener(View -> {
            /*String email = send_email.getText().toString();

            if (email.equals("")){
                Toast.makeText(ResetPasswordActivity.this, "All fileds are required", Toast.LENGTH_SHORT).show();
            } else {
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ResetPasswordActivity.this, "Please check your Email", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(ResetPasswordActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }*/
        });

        btn_edit_info.setOnClickListener(View -> {
            //onClickEdit();
        });

        img_logout.setOnClickListener(View -> {
            getActivity().finish();
        });

        takePhoto = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        profile_img.setImageURI(result);
                        uploadPhoto.launch("image/*");
                    }
                });

        uploadPhoto = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        imageUri = result;

                        if (uploadTask != null && uploadTask.isInProgress()) {
                            Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
                        } else {
                            uploadImage();
                        }
                    }
                });
        readTimeline();
    }

    // Đọc Timeline hiện tại được set để release resources
    public void readTimeline() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("TimelineList").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                timeline_root = snapshot.getValue(Timeline.class);

                if (timeline_root != null) {
                    TimeUtil timeUtil = new TimeUtil();
                    int day_num = Integer.parseInt(timeline_root.getDay_num());
                    int month_num = Integer.parseInt(timeline_root.getMonth_num());
                    int year_num = Integer.parseInt(timeline_root.getYear_num());
                    String delete_time_point = timeUtil.up_downTime(TimeUtil.getTimeNow(),
                            day_num, month_num, year_num);
                    release_time.setText("Release point: " + delete_time_point);
                } else {
                    //timeline = new Timeline(firebaseUser.getUid(), "0", "0", "0");
                    release_time.setText("You have not set resource release time");
                    return;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // TODO: Unused function
    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST); // Switched to ActivityResultLauncher
    }

    //Bắt sự kiện chỉnh sửa profile
    void onClickEdit() {
        if (btn_edit_info.getText().toString().equals("Edit")) {
            btn_edit_info.setText("Save");
            edit_address.setFocusable(true);
            edit_address.setFocusableInTouchMode(true);
            edit_address.setClickable(true);

            edit_phone.setFocusable(true);
            edit_phone.setFocusableInTouchMode(true);
            edit_phone.setClickable(true);
        } else {
            btn_edit_info.setText("Edit");
            //Vô hiệu hoá sửa địa chỉ
            edit_address.setFocusable(false);
            edit_address.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
            edit_address.setClickable(false); // user navigates with wheel and selects widget
            //user.setAddress(edit_address.getText().toString());
            tv_address.setText(edit_address.getText().toString());

            //Vô hiệu hoá sửa phone
            edit_phone.setFocusable(false);
            edit_phone.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
            edit_phone.setClickable(false); // user navigates with wheel and selects widget
            //user.setPhone(edit_phone.getText().toString());

            //UserService userService = new UserService();
            //userService.update(user);
        }
    }

    // SetText các thông tin tài khoản user
    public void setInfoAccount(User user, String email) {

        tv_userName.setText(user.getUsername());
        edit_email.setText(email);
        edit_phone.setText(user.getPhone_number());
//        us = sv;
//        if(txtHoTen != null){
//            setInfoSinhVien2();
//        }
        //System.out.println("Day la ham setInfo-");
        //System.out.println("Đây la giá trị txtHoTen khi bắt đầu vào setInfo " + tv_userName);
    }

    // Mapping
    public void AnhXa() {

        //Log.d("loi","Bat dau task1");


        profile_img = view.findViewById(R.id.profile_image);

        btnUpdate = view.findViewById(R.id.btn_update);
        btn_change_pass = view.findViewById(R.id.btn_change_pass);
        tv_userName = view.findViewById(R.id.tv_username_profile);
        tv_address = view.findViewById(R.id.tv_address);
        btn_edit_info = view.findViewById(R.id.btn_edit_info_profile);
        edit_email = view.findViewById(R.id.email_profile);
        edit_phone = view.findViewById(R.id.edit_phone_number_profile);
        edit_address = view.findViewById(R.id.edit_address_profile);
        img_logout = view.findViewById(R.id.img_logout);

        release_time = view.findViewById(R.id.release_time);

        layout = view.findViewById(R.id.rellay1);  //specify here Root layout Id
    }


    // TODO: Unused function
    private void getReleaseTime() {

    }

    // Get file extension for resources (audio, image, video,...)
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    // Upload image lên Firebase
    private void uploadImage() {
        progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(progressBar, params);

        if (imageUri != null) { // Đã chọn image để upload
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            deleteOldImage(currentUser.getImageURL());

            uploadTask = fileReference.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                    //System.out.println("loi");
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", mUri);
                        reference.updateChildren(map);

                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(getContext(), "False!", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT);
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    // Xoá image của theo URL
    private void deleteOldImage(String oldImageURL) {

        String oldImagePath;

        // oldImageURL like as https://firebasestorage.googleapis.com/v0/b/zalo-04.appspot.com/o/uploads%2F1654462115466.jpg?alt=media&token=4b978b01-6b01-4ad3-9f79-c02e556302ed
        int startIndexPath, endIndexPath;
        startIndexPath = oldImageURL.indexOf("%2F") + "%2F".length();
        endIndexPath = oldImageURL.indexOf(".jpg") + ".jpg".length();

        oldImagePath = oldImageURL.substring(startIndexPath, endIndexPath);
        //Log.d("old_path", oldImagePath);
        //System.out.println(oldImagePath);

        // Create a storage reference from our app
        // is storageReference;

        // Create a reference to the file to delete
        StorageReference desertRef = storageReference.child(oldImagePath);

        // Delete the file
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                /*reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                HashMap<String, Object> map = new HashMap<>();
                map.put("imageURL", "default");
                reference.updateChildren(map);*/
                Toast.makeText(getContext(), "Deleted old image", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Toast.makeText(getContext(), "We want to delete old image, but an error occurred!", Toast.LENGTH_SHORT).show();
            }
        });

    }


}
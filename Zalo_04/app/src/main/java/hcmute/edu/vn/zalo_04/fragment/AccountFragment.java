package hcmute.edu.vn.zalo_04.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
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
import hcmute.edu.vn.zalo_04.model.User;

public class AccountFragment extends Fragment {

    private View view;
    private TextView tv_userName, tv_address;
    private EditText edit_phone, edit_address;
    private Button btnUpdate, btn_change_pass, btn_edit_info;

    private ImageView img_logout;
    private CircleImageView profile_img;

    private DatabaseReference reference;
    private FirebaseUser firebaseUser;

    private StorageReference storageReference;

    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    private RelativeLayout layout;

    private ProgressBar progressBar;

    private ActivityResultLauncher<String> takePhoto;
    private ActivityResultLauncher<String> uploadPhoto;

    private User currentUser;

    public AccountFragment() {

    }


    @Override
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

    private void onClickGoToChangePass(User user) {
//        Intent intent = new Intent(getContext(), ChangePassActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("object_user", user);
//        intent.putExtras(bundle);
//        getContext().startActivity(intent);
    }

    void setupUI(){

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUser = snapshot.getValue(User.class);
                tv_userName.setText(currentUser.getUsername());
                if (currentUser.getImageURL().equals("default")){
                    profile_img.setImageResource(R.drawable.user_hao);
                } else {
                    Glide.with(getContext()).load(currentUser.getImageURL()).into(profile_img);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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


        btnUpdate.setOnClickListener(View ->{
            //CreateData createData = new CreateData();
            //createData.createDB();
        });

        btn_change_pass.setOnClickListener(View -> {
            //CreateData createData = new CreateData();
            //createData.deleteDB();
            //onClickGoToChangePass(user);
        });

        btn_edit_info.setOnClickListener(View ->{
            //onClickEdit();
        });

        img_logout.setOnClickListener(View ->{
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

                        if (uploadTask != null && uploadTask.isInProgress()){
                            Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
                        } else {
                            uploadImage();
                        }
                    }
                });
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    void onClickEdit(){
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

    public void setInfoAccount(String username) {

        tv_userName.setText(username);
//        us = sv;
//        if(txtHoTen != null){
//            setInfoSinhVien2();
//        }
        System.out.println("Day la ham setInfo-");
        System.out.println("Đây la giá trị txtHoTen khi bắt đầu vào setInfo " + tv_userName);
    }


    public void AnhXa(){

        //Log.d("loi","Bat dau task1");


        profile_img = view.findViewById(R.id.profile_image);

        btnUpdate = view.findViewById(R.id.btn_update);
        btn_change_pass = view.findViewById(R.id.btn_change_pass);
        tv_userName= view.findViewById(R.id.tv_username_profile);
        tv_address = view.findViewById(R.id.tv_address);
        btn_edit_info = view.findViewById(R.id.btn_edit_info_profile);
        edit_phone = view.findViewById(R.id.edit_phone_number_profile);
        edit_address = view.findViewById(R.id.edit_address_profile);
        img_logout = view.findViewById(R.id.img_logout);

        layout = view.findViewById(R.id.rellay1);  //specify here Root layout Id
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(progressBar, params);

        if (imageUri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
            +"."+ getFileExtension(imageUri));

            deleteOldImage(currentUser.getImageURL());

            uploadTask = fileReference.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                    //System.out.println("loi");
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", mUri);
                        reference.updateChildren(map);

                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(getContext(),"False!", Toast.LENGTH_SHORT).show();
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

    private void deleteOldImage(String oldImageURL){

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
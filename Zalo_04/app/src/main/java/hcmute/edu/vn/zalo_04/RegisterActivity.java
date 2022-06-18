package hcmute.edu.vn.zalo_04;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hcmute.edu.vn.zalo_04.adapter.UserAdapter;
import hcmute.edu.vn.zalo_04.model.FriendList;
import hcmute.edu.vn.zalo_04.model.User;

//Activity đăng ký tài khoản
public class RegisterActivity extends AppCompatActivity {

    //Các trường thông tin cần nhập khi đăng ký: tên tài khoản, số điện thoại, email, password, re_password
    private MaterialEditText username, phone_number, email, password, re_password;

    private Button btn_register; //Nút đăng ký

    private FirebaseAuth auth; //Quyền người dùng từ firebase
    private DatabaseReference reference; //Ánh xạ tới database firebase

    //Biến kiểm tra số điện thoại đã tồn tại hay chưa
    private boolean phone_exists = false;

    @Override
    //Khởi tạo giao diện
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.username);
        phone_number = findViewById(R.id.phone_number);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        re_password = findViewById(R.id.re_password);
        btn_register = findViewById(R.id.btn_register);

        auth = FirebaseAuth.getInstance();

        phone_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkPhoneExists(editable.toString());
            }
        });

        btn_register.setOnClickListener(View -> {
            String txt_username = username.getText().toString();
            String txt_phone_number = phone_number.getText().toString();
            String txt_email = email.getText().toString();
            String txt_password = password.getText().toString();
            String txt_re_password = re_password.getText().toString();
            //checkPhoneExists(txt_phone_number);
            if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_phone_number) || TextUtils.isEmpty(txt_email) ||
                    TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_re_password)){
                Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            } else if (txt_password.length() < 6){
                Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            } else if (phone_exists){
                Toast.makeText(RegisterActivity.this, "Phone number existed", Toast.LENGTH_SHORT).show();
            } else if (!txt_password.equals(txt_re_password)){
                Toast.makeText(RegisterActivity.this, "Re-password not match", Toast.LENGTH_SHORT).show();
            } else {
                register(txt_username, txt_phone_number, txt_email, txt_password);
            }
        });
    }

    //Hàm đăng ký người dùng, lưu dữ liệu lên firebase
    private void register(String username, String phone_number, String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userId = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userId);
                            hashMap.put("username", username);
                            hashMap.put("phone_number", phone_number);
                            hashMap.put("imageURL", "default");
                            hashMap.put("status", "offline"); // add more
                            hashMap.put("search", username.toLowerCase());

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RegisterActivity.this, "You can't register wroth this email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    //Hàm kiểm tra số điện thoại đã tồn tại hay chưa
    private boolean checkPhoneExists(String phone_number) {
        phone_exists = false;
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot_index : snapshot.getChildren()){
                    User user = snapshot_index.getValue(User.class);
                    if (user.getPhone_number().equals(phone_number)){
                        phone_exists = true;
                        Log.d("errorP",phone_number);
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Log.d("errorPLast",String.valueOf(phone_exists));
        return  phone_exists;
    }
}
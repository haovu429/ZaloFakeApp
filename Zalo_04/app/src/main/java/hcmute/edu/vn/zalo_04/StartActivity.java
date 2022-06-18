package hcmute.edu.vn.zalo_04;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//Activity bắt đầu ứng dụng
public class StartActivity extends AppCompatActivity {

    //Nút lựa chọn đăng nhập, đăng ký
    Button btn_login, btn_register;

    //Biến lưu người người dùng hiện tại nếu đã đăng nhập trước đó
    FirebaseUser firebaseUser;

    @Override
    //Hàm kiêm tra xem đã có người dùng đăng nhập trước đó hay chưa
    protected void onStart() {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //Check if user is null
        if (firebaseUser != null){
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    //hàm khởi tạo giao diện
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);

        btn_login.setOnClickListener(View ->{
            startActivity(new Intent(StartActivity.this, LoginActivity.class));
        });

        btn_register.setOnClickListener(View ->{
            startActivity(new Intent(StartActivity.this, RegisterActivity.class));
        });
    }
}
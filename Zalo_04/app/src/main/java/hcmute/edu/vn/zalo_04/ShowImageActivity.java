package hcmute.edu.vn.zalo_04;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

//Activity show hình ảnh trong boxchat
public class ShowImageActivity extends AppCompatActivity {

    private ImageView image_sent; //View hiển thị hình ảnh
    private String imageUrl; // Link hình ảnh

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        image_sent = findViewById(R.id.image_sent);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null){
            this.imageUrl = (String) bundle.get("imageUrl");
            if(this.imageUrl == null){
                Log.d("loi", "Khong nhan duoc userId de gui audio");
                finish();
            }

        }

        RequestOptions options = new RequestOptions()
                .fitCenter()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);

        //hien thi anh dai dien
        try {
            Glide.with(this).load(imageUrl).apply(options).into(image_sent);
        } catch (Exception e){
            //loi duong link sai
            e.printStackTrace();
        }

    }
}
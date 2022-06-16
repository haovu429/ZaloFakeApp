package hcmute.edu.vn.zalo_04;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.StringTokenizer;

import hcmute.edu.vn.zalo_04.model.Timeline;
import hcmute.edu.vn.zalo_04.model.User;
import hcmute.edu.vn.zalo_04.task.ReleaseStorage;
import hcmute.edu.vn.zalo_04.util.TimeUtil;

public class SetDeleteTimelineActivity extends AppCompatActivity {

    private EditText day, month, year; //set
    private Button set_time, exit, apply_now;
    private int day_num = 0, month_num = 0, year_num = 0;

    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    //private Reference reference;

    private Timeline timeline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_delete_timeline);

        day = findViewById(R.id.day);
        month = findViewById(R.id.month);
        year = findViewById(R.id.year);

        set_time = findViewById(R.id.set_time);
        exit = findViewById(R.id.exit);
        apply_now = findViewById(R.id.apply_now);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        readTimeline();

        set_time.setOnClickListener(View -> {
            try {
                day_num = Integer.parseInt(day.getText().toString());
                month_num = Integer.parseInt(month.getText().toString());
                year_num =Integer.parseInt(year.getText().toString());
            } catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, "Invalue input",Toast.LENGTH_SHORT).show();
                return;
            }


            if (day_num < 0 || day_num > 31|| month_num < 0 || month_num > 12 || year_num < 0){
                Toast.makeText(this, "Invalid input, 0<=day<31 ; 0<=month<13 ; year>0", Toast.LENGTH_SHORT).show();
            } else {
                TimeUtil timeUtil = new TimeUtil();
                try {
                    String delete_time_point = timeUtil.up_downTime(TimeUtil.getTimeNow(), day_num, month_num, year_num);
                    Toast.makeText(this, "Time point: " + delete_time_point,Toast.LENGTH_SHORT).show();
                    if (!timeUtil.is_OutOfDate(TimeUtil.getTimeNow(), delete_time_point)){
                        Timeline timeline = new Timeline(firebaseUser.getUid(), String.valueOf(day_num),
                                String.valueOf(month_num), String.valueOf(year_num));
                        saveInfoTimeline(timeline);
                    } else {
                        Toast.makeText(this, "Invalue input1",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this, "Invalue input",Toast.LENGTH_SHORT).show();
                    return;
                }
            }

        });

        exit.setOnClickListener(View -> {
            finish();
        });

        apply_now.setOnClickListener(View ->{
            ReleaseStorage releaseStorage = new ReleaseStorage(firebaseUser, this);
            releaseStorage.deleteResource();
        });
    }

    private void saveInfoTimeline(Timeline timeline){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", timeline.getId());
        hashMap.put("day_num", timeline.getDay_num());
        hashMap.put("month_num", timeline.getMonth_num());
        hashMap.put("year_num", timeline.getYear_num());

        reference.child("TimelineList").child(firebaseUser.getUid()).setValue(hashMap);

    }

    public void readTimeline(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("TimelineList").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                timeline = snapshot.getValue(Timeline.class);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (timeline != null){
                    day.setText(timeline.getDay_num());
                    month.setText(timeline.getMonth_num());
                    year.setText(timeline.getYear_num());
                } else {
                    day.setText("30");
                    month.setText("0");
                    year.setText("0");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
package hcmute.edu.vn.zalo_04;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.edu.vn.zalo_04.adapter.MainPagerAdapter;
import hcmute.edu.vn.zalo_04.fragment.AccountFragment;
import hcmute.edu.vn.zalo_04.fragment.ContactFragment;
import hcmute.edu.vn.zalo_04.fragment.MessageFragment;
import hcmute.edu.vn.zalo_04.model.User;

public class MainActivity extends AppCompatActivity {

    private static final int FRAGMENT_MESSAGE = 0;
    private static final int FRAGMENT_CONTACT = 1;
    private static final int FRAGMENT_ACCOUNT = 2;

    private int currentFragment = FRAGMENT_MESSAGE;

    private int idUser;

    CircleImageView profile_image;
    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    ViewPager2 viewPager;
    MainPagerAdapter mainPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AnhXa();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.drawable.user_hao);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                    //loadImageToUI(user); // Load UI before init MainActivity -> crash
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.tab_1, R.drawable.baseline_chat_24, R.color.color_tab_1);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.tab_2, R.drawable.baseline_portrait_24, R.color.color_tab_2);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.tab_3, R.drawable.baseline_person_outline_24, R.color.color_tab_3);

        // Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);

        // Use colored navigation with circle reveal effect
        //bottomNavigation.setColored(true);

        bottomNavigation.setDefaultBackgroundColor(ContextCompat.getColor(this, R.color.white));
        bottomNavigation.setAccentColor(ContextCompat.getColor(this, R.color.zalo_blue));
        bottomNavigation.setInactiveColor(ContextCompat.getColor(this, R.color.gray));

        // Add or remove notification for each item
        //bottomNavigation.setNotification("1", 3);
        // OR
        AHNotification notification = new AHNotification.Builder()
                .setText("5")
                .setBackgroundColor(ContextCompat.getColor(this, R.color.red))
                .setTextColor(ContextCompat.getColor(this, R.color.black))
                .build();
        bottomNavigation.setNotification(notification, 0);

        // Manage titles
        //bottomNavigation.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);
        //bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        //bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_HIDE);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE_FORCE);


        replaceFragment(new MessageFragment());

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position){
                    case FRAGMENT_MESSAGE:
                        openMessageFragment();
                        break;
                    case FRAGMENT_CONTACT:
                        openContactFragment();
                        break;
                    case FRAGMENT_ACCOUNT:
                        openAccountFragment();
                        break;
                    default:
                        openMessageFragment();
                }
                return true;
            }
        });


        //Setup View Pager2
        /*viewPager = findViewById(R.id.view_pager);
        mainPagerAdapter = new MainPagerAdapter(this, idUser);

        viewPager.setAdapter(mainPagerAdapter);*/
    }
    private void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }

    private void openMessageFragment(){
        if(currentFragment != FRAGMENT_MESSAGE){
            replaceFragment(new MessageFragment());
            currentFragment = FRAGMENT_MESSAGE;
        }
    }

    private void openContactFragment(){
        if(currentFragment != FRAGMENT_CONTACT){
            replaceFragment(new ContactFragment());
            currentFragment = FRAGMENT_CONTACT;
        }
    }

    private void openAccountFragment(){
        if(currentFragment != FRAGMENT_ACCOUNT){
            replaceFragment(new AccountFragment());
            currentFragment = FRAGMENT_ACCOUNT;
        }
    }

    private void AnhXa(){
        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                //change this code because your app will cash
                startActivity(new Intent(MainActivity.this, StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
        }
        return false;
    }

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("loi", "destroy");
    }

    private void loadImageToUI(User user){
        while (true){
            try {
                Glide.with(MainActivity.this).load(user.getImageURL()).into(profile_image);
                break;
            } catch (Exception e){
                e.printStackTrace();
            }
        }

    }

}
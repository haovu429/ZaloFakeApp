package hcmute.edu.vn.zalo_04.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import hcmute.edu.vn.zalo_04.fragment.AccountFragment;
import hcmute.edu.vn.zalo_04.fragment.ContactFragment;
import hcmute.edu.vn.zalo_04.fragment.MessageFragment;


public class MainPagerAdapter extends FragmentStateAdapter {

    private Context context;
    private final int idUser;

    public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity, int idUser, Context context) {
        super(fragmentActivity);
        this.idUser = idUser;
        this.context = context;
    }

    @NonNull
    @Override
    //Khời tạo fragment cho trang chính
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return new MessageFragment();
            case 2:
                return new ContactFragment();
            default:
                return new AccountFragment();//0
        }
    }

    @Override
    public int getItemCount() {
        return 3; // có 4 mục
    }
}

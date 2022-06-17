package hcmute.edu.vn.zalo_04.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.Map;

import hcmute.edu.vn.zalo_04.fragment.ContactFriendFragment;
import hcmute.edu.vn.zalo_04.fragment.ContactGroupFragment;
import hcmute.edu.vn.zalo_04.fragment.ContactOAFragment;

public class ContactPagerAdapter extends FragmentStateAdapter {

    private Map<Integer, String> mFragmentTags;
    private FragmentManager fragmentManager;
    private final Context context;

    public ContactPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        context = fragmentActivity;
    }

    @NonNull
    @Override
    // Khỏi tạo fragment cho tab Contact
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return new ContactGroupFragment();//0
            case 2:
                return new ContactOAFragment();
            default:
                return new ContactFriendFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

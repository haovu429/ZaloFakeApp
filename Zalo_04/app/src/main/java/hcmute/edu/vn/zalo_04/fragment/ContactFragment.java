package hcmute.edu.vn.zalo_04.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import hcmute.edu.vn.zalo_04.R;
import hcmute.edu.vn.zalo_04.adapter.ContactPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1"; // Testing: Temporary
    private static final String ARG_PARAM2 = "param2"; // Testing: Temporary

    // TODO: Rename and change types of parameters
    private String mParam1; // Testing: Temporary
    private String mParam2; // Testing: Temporary

    TabLayout tabLayout; // TabLayout cho ContactFragment
    ViewPager2 viewPager; // ViewPager để setAdapter ContactPagerAdapter
    ContactPagerAdapter contactPagerAdapter; // Adapter của Contact liên lạc / danh bạ

    View view; // View của ContactFragment

    public ContactFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactFragment newInstance(String param1, String param2) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_contact, container, false);
        tabLayout = view.findViewById(R.id.tab_layout_contact);
        viewPager = view.findViewById(R.id.viewpager_contact);
        contactPagerAdapter = new ContactPagerAdapter(this.getActivity());

        viewPager.setAdapter(contactPagerAdapter);

        //Hàm thay thế cho setupWithViewPager(..)
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 1:
                    tab.setText("Your contact"); // posittion = 0
                    break;
                case 2:
                    tab.setText("Find");
                    break;
                default:
                    tab.setText("Friend");
                    break;
            }
        }).attach();

        return view;
    }
}
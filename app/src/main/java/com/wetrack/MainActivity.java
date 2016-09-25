package com.wetrack;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.wetrack.utils.ConstantValues;
import com.wetrack.friends.FriendsFragment;
import com.wetrack.map.MapFragment;
import com.wetrack.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends FragmentActivity
        implements MapFragment.OnFragmentInteractionListener,
        FriendsFragment.OnFragmentInteractionListener {

    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments = new ArrayList<>();

    private ArrayList<Integer> bottomButtonIds = new ArrayList<>(Arrays.asList(R.id.friends_button, R.id.map_button));
    private Button bottomButton[] = new Button[bottomButtonIds.size()];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initButton();

        initViewPager();

        mViewPager.setCurrentItem(0);
        onViewPagerSelected(0);
    }

    private void initButton() {
        for (int i = 0; i < bottomButton.length; i++) {
            bottomButton[i] = (Button)findViewById(bottomButtonIds.get(i));
            bottomButton[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pageIndex = bottomButtonIds.indexOf(v.getId());
                    mViewPager.setCurrentItem(pageIndex);
                    onViewPagerSelected(pageIndex);
                }
            });
        }
    }

    private void initViewPager() {

        mFragments.add(FriendsFragment.newInstance());
        mFragments.add(MapFragment.newInstance(this));

        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return mFragments.get(arg0);
            }
        };

        mViewPager.setAdapter(mAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                onViewPagerSelected(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    private void onViewPagerSelected(int pageIndex) {
        for (int i = 0; i < bottomButton.length; i++) {
            if (i == pageIndex) {
                bottomButton[i].setTextColor(Color.BLACK);
            } else {
                bottomButton[i].setTextColor(Color.GRAY);
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d(ConstantValues.debugTab, "onFragmentInteraction: " + uri.toString());
    }
}

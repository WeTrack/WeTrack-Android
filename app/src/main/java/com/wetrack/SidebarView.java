package com.wetrack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wetrack.utils.ConstantValues;

public class SidebarView extends RelativeLayout {

    private UserInfoUpdateReceiver mUserInfoUpdateReceiver = null;
    private ImageView portraitImageView;
    private ImageView genderImageView;
    private TextView usernameTextView;
    private Button changeInfoButton;
    private Button settingButton;
    private Button logoutButton;


    public SidebarView(Context context) {
        super(context);
        init();
    }
    public SidebarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public SidebarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
//    public SidebarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        init();
//    }

    public void init() {
        initBroadcastReceiver();

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        RelativeLayout sidebarLayout = (RelativeLayout)layoutInflater.inflate(R.layout.sidebar, null);
        addView(sidebarLayout);

        setVisibility(GONE);

        portraitImageView = (ImageView) findViewById(R.id.portrait_imageview);
        usernameTextView = (TextView) findViewById(R.id.username_textview);
        genderImageView = (ImageView) findViewById(R.id.gender_imageview);
        changeInfoButton = (Button) findViewById(R.id.changeinfo_button);
        settingButton = (Button) findViewById(R.id.setting_button);
        logoutButton = (Button) findViewById(R.id.logout_button);

//        String username = PreferenceUtils.getStringValue(BaseApplication.getContext(), PreferenceUtils.KEY_USERNAME);
        String username = "ken";
        usernameTextView.setText(username);

//        UserDataFormat userDataFormat = new UserDataFormat(username);
//        userDataFormat.getDataByUsername();

//        if (userDataFormat.getValueByName(UserDataFormat.ATTRI_GENDER).equals("male")) {
            genderImageView.setImageResource(R.drawable.gender_male);
//        } else {
//            genderImageView.setImageResource(R.drawable.gender_female);
//        }
    }

    public void close() {
        int width = getWidth();

        Animation am = new TranslateAnimation(0f, -width * 1f, 0f, 0f);
        am.setDuration(500);
        am.setInterpolator(new AccelerateInterpolator());
        startAnimation(am);
        am.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void open() {
        int width = getWidth();
        setVisibility(View.VISIBLE);
        Animation am = new TranslateAnimation(-width * 1f, 0f, 0f, 0f);
        am.setDuration(500);
        am.setInterpolator(new AccelerateInterpolator());
        startAnimation(am);
    }

    public void setLogoutListener(View.OnClickListener onClickListener) {
        logoutButton.setOnClickListener(onClickListener);
    }

    private void initBroadcastReceiver() {
        mUserInfoUpdateReceiver = new UserInfoUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter(ConstantValues.ACTION_UPDATE_USER_INFO);
        getContext().registerReceiver(mUserInfoUpdateReceiver, intentFilter);
    }

    public void destroy() {
        if (mUserInfoUpdateReceiver != null) {
            getContext().unregisterReceiver(mUserInfoUpdateReceiver);
            mUserInfoUpdateReceiver = null;
        }
    }

    private class UserInfoUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //reload
        }
    }
}

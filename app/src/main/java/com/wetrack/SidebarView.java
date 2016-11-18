package com.wetrack;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wetrack.login.LoginActivity;
import com.wetrack.utils.PreferenceUtils;

/**
 * Created by moziliang on 16/11/17.
 */
public class SidebarView extends RelativeLayout {

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
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        RelativeLayout sidebarLayout = (RelativeLayout)layoutInflater.inflate(R.layout.sidebar, null);
        addView(sidebarLayout);

        setVisibility(INVISIBLE);

        portraitImageView = (ImageView) findViewById(R.id.portrait_imageview);
        usernameTextView = (TextView) findViewById(R.id.username_textview);
        genderImageView = (ImageView) findViewById(R.id.gender_imageview);
        changeInfoButton = (Button) findViewById(R.id.changeinfo_button);
        settingButton = (Button) findViewById(R.id.setting_button);
        logoutButton = (Button) findViewById(R.id.logout_button);

        String username = PreferenceUtils.getStringValue(BaseApplication.getContext(), PreferenceUtils.KEY_USERNAME);
        usernameTextView.setText(username);


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
                setVisibility(View.INVISIBLE);
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
}

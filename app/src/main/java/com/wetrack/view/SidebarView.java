package com.wetrack.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
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
import android.widget.Toast;

import com.wetrack.R;
import com.wetrack.client.EntityCallback;
import com.wetrack.client.EntityCallbackWithLog;
import com.wetrack.client.WeTrackClient;
import com.wetrack.model.Message;
import com.wetrack.model.User;
import com.wetrack.utils.Tools;
import com.wetrack.utils.PreferenceUtils;

public class SidebarView extends RelativeLayout {

    private WeTrackClient client = WeTrackClient.singleton();

    private ImageView portraitImageView;
    private ImageView genderImageView;
    private TextView nicknameTextView;
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

    public void init() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                Tools.getScreenW() * 2 / 3,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        setLayoutParams(layoutParams);
        setGravity(Gravity.LEFT);

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        RelativeLayout sidebarLayout = (RelativeLayout) layoutInflater.inflate(R.layout.sidebar, null);
        addView(sidebarLayout);

        portraitImageView = (ImageView) findViewById(R.id.portrait_imageview);
        nicknameTextView = (TextView) findViewById(R.id.username_textview);
        genderImageView = (ImageView) findViewById(R.id.gender_imageview);
        changeInfoButton = (Button) findViewById(R.id.changeinfo_button);
        settingButton = (Button) findViewById(R.id.setting_button);
        logoutButton = (Button) findViewById(R.id.logout_button);

        String username = PreferenceUtils.getCurrentUsername();

        // Fetch user's latest information from server
        client.getUserInfo(username, new EntityCallbackWithLog<User>(getContext()) {
            @Override
            protected void onReceive(User receivedUser) {
                nicknameTextView.setText(receivedUser.getNickname());
                if (receivedUser.getGender() == User.Gender.Male) {
                    genderImageView.setImageResource(R.drawable.gender_male);
                } else {
                    genderImageView.setImageResource(R.drawable.gender_female);
                }
            }
        });
        client.getUserPortrait(username, true, new EntityCallbackWithLog<Bitmap>(getContext()) {
            @Override
            protected void onReceive(Bitmap bitmap) {
                portraitImageView.setImageBitmap(bitmap);
            }

            @Override
            protected void onErrorMessage(Message response) {
                if (response.getStatusCode() == 404)
                    portraitImageView.setImageResource(R.drawable.portrait_boy);
            }
        });
    }

    public void updateUserInfo() {
        String username = PreferenceUtils.getCurrentUsername();
        // Fetch user's latest information from server
        client.getUserInfo(username, new EntityCallbackWithLog<User>(getContext()) {
            @Override
            protected void onReceive(User receivedUser) {
                nicknameTextView.setText(receivedUser.getNickname());
                if (receivedUser.getGender() == User.Gender.Male) {
                    genderImageView.setImageResource(R.drawable.gender_male);
                } else {
                    genderImageView.setImageResource(R.drawable.gender_female);
                }
            }
        });
    }

    public void updateUserPortrait() {
        String username = PreferenceUtils.getCurrentUsername();

        client.getUserPortrait(username, true, new EntityCallbackWithLog<Bitmap>(getContext()) {
            @Override
            protected void onReceive(Bitmap bitmap) {
                portraitImageView.setImageBitmap(bitmap);
            }

            @Override
            protected void onErrorMessage(Message response) {
                if (response.getStatusCode() == 404)
                    portraitImageView.setImageResource(R.drawable.portrait_boy);
            }
        });
    }

    public void setLogoutListener(View.OnClickListener onClickListener) {
        logoutButton.setOnClickListener(onClickListener);
    }

    public void setUserInfoClickListener(View.OnClickListener onClickListener) {
        changeInfoButton.setOnClickListener(onClickListener);
    }

}

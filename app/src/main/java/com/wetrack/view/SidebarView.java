package com.wetrack.view;

import android.content.Context;
import android.graphics.Bitmap;
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

    //false means close, true means open
    private boolean sidebarState;
    final static public boolean CLOSE_STATE = false;
    final static public boolean OPEN_STATE = true;

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
        sidebarState = CLOSE_STATE;

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                Tools.getScreenW() * 2 / 3,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(-Tools.getScreenW() * 2 / 3, 0, 0, 0);
        layoutParams.addRule(RelativeLayout.BELOW, R.id.menu_bar);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        setLayoutParams(layoutParams);

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

    public boolean getSidebarState() {
        return sidebarState;
    }

    public void close() {
        sidebarState = CLOSE_STATE;
        int width = getWidth();
        Animation am = new TranslateAnimation(0f, -width * 1f, 0f, 0f);
        am.setDuration(200);
        am.setInterpolator(new AccelerateInterpolator());
        startAnimation(am);
        am.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams layoutParams = ((RelativeLayout.LayoutParams) getLayoutParams());
                layoutParams.setMargins(-Tools.getScreenW() * 2 / 3, 0, 0, 0);
                setLayoutParams(layoutParams);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    public void open() {
        sidebarState = OPEN_STATE;

        RelativeLayout.LayoutParams layoutParams = ((RelativeLayout.LayoutParams) getLayoutParams());
        layoutParams.setMargins(0, 0, 0, 0);
        setLayoutParams(layoutParams);

        int width = getWidth();
        Animation am = new TranslateAnimation(-width * 1f, 0f, 0f, 0f);
        am.setDuration(200);
        am.setInterpolator(new AccelerateInterpolator());
        startAnimation(am);
    }

    public void setLogoutListener(View.OnClickListener onClickListener) {
        logoutButton.setOnClickListener(onClickListener);
    }
}

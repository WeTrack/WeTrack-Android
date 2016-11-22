package com.wetrack;

import android.content.Context;
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

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.wetrack.client.EntityCallback;
import com.wetrack.database.WeTrackDatabaseHelper;
import com.wetrack.model.User;
import com.wetrack.utils.ConstantValues;
import com.wetrack.utils.PreferenceUtils;

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

    public void init() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        RelativeLayout sidebarLayout = (RelativeLayout) layoutInflater.inflate(R.layout.sidebar, null);
        addView(sidebarLayout);

        setVisibility(GONE);

        portraitImageView = (ImageView) findViewById(R.id.portrait_imageview);
        usernameTextView = (TextView) findViewById(R.id.username_textview);
        genderImageView = (ImageView) findViewById(R.id.gender_imageview);
        changeInfoButton = (Button) findViewById(R.id.changeinfo_button);
        settingButton = (Button) findViewById(R.id.setting_button);
        logoutButton = (Button) findViewById(R.id.logout_button);

        String username = PreferenceUtils.getStringValue(BaseApplication.getContext(), PreferenceUtils.KEY_USERNAME);

        // Load user information from local cache
        RuntimeExceptionDao<User, String> userDao =
                OpenHelperManager.getHelper(this.getContext(), WeTrackDatabaseHelper.class).getUserDao();
        final User user = userDao.queryForId(username);
        OpenHelperManager.releaseHelper();
        if (user != null) {
            usernameTextView.setText(user.getNickname());
            if (user.getGender() == User.Gender.Male) {
                portraitImageView.setImageResource(R.drawable.portrait_boy);
                genderImageView.setImageResource(R.drawable.gender_male);
            } else {
                portraitImageView.setImageResource(R.drawable.portrait_girl);
                genderImageView.setImageResource(R.drawable.gender_female);
            }
        } else {
            usernameTextView.setText("User Nickname");
            portraitImageView.setImageResource(R.drawable.portrait_boy);
            genderImageView.setImageResource(R.drawable.gender_male);
        }

        // Fetch user's latest information from server
        ConstantValues.client().getUserInfo(username, new EntityCallback<User>() {
            @Override
            protected void onReceive(User receivedUser) {
                if (!receivedUser.equals(user)) { // Update local cache if necessary
                    RuntimeExceptionDao<User, String> userDao =
                            OpenHelperManager.getHelper(
                                    SidebarView.this.getContext(),
                                    WeTrackDatabaseHelper.class
                            ).getUserDao();
                    if (user == null)
                        userDao.create(receivedUser);
                    else
                        userDao.update(receivedUser);
                    OpenHelperManager.releaseHelper();
                    if (receivedUser.getGender() == User.Gender.Male) {
                        portraitImageView.setImageResource(R.drawable.portrait_boy);
                        genderImageView.setImageResource(R.drawable.gender_male);
                    } else {
                        portraitImageView.setImageResource(R.drawable.portrait_girl);
                        genderImageView.setImageResource(R.drawable.gender_female);
                    }
                }
            }
        });
    }

    public void close() {
        int width = getWidth();

        Animation am = new TranslateAnimation(0f, -width * 1f, 0f, 0f);
        am.setDuration(500);
        am.setInterpolator(new AccelerateInterpolator());
        startAnimation(am);
        am.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
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

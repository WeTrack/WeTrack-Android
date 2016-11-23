package com.wetrack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
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

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.wetrack.client.EntityCallback;
import com.wetrack.client.WeTrackClient;
import com.wetrack.database.WeTrackDatabaseHelper;
import com.wetrack.model.User;
import com.wetrack.utils.ConstantValues;
import com.wetrack.utils.Tools;
import com.wetrack.utils.PreferenceUtils;

public class SidebarView extends RelativeLayout {
    private static final String TAG = SidebarView.class.getCanonicalName();

    private WeTrackDatabaseHelper helper;

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
                ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(-Tools.getScreenW() * 2 / 3, 0, 0, 0);
        setLayoutParams(layoutParams);
        helper = OpenHelperManager.getHelper(getContext(), WeTrackDatabaseHelper.class);

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        RelativeLayout sidebarLayout = (RelativeLayout) layoutInflater.inflate(R.layout.sidebar, null);
        addView(sidebarLayout);

        portraitImageView = (ImageView) findViewById(R.id.portrait_imageview);
        nicknameTextView = (TextView) findViewById(R.id.username_textview);
        genderImageView = (ImageView) findViewById(R.id.gender_imageview);
        changeInfoButton = (Button) findViewById(R.id.changeinfo_button);
        settingButton = (Button) findViewById(R.id.setting_button);
        logoutButton = (Button) findViewById(R.id.logout_button);

        String username = PreferenceUtils.getStringValue(PreferenceUtils.KEY_USERNAME);

        // Load user information from local cache
        RuntimeExceptionDao<User, String> userDao =
                OpenHelperManager.getHelper(this.getContext(), WeTrackDatabaseHelper.class).getUserDao();
        final User user = userDao.queryForId(username);
        OpenHelperManager.releaseHelper();
        if (user != null) {
            nicknameTextView.setText(user.getNickname());
            if (user.getGender() == User.Gender.Male) {
                portraitImageView.setImageResource(R.drawable.portrait_boy);
                genderImageView.setImageResource(R.drawable.gender_male);
            } else {
                portraitImageView.setImageResource(R.drawable.portrait_girl);
                genderImageView.setImageResource(R.drawable.gender_female);
            }
        } else {
            nicknameTextView.setText("User Nickname");
            portraitImageView.setImageResource(R.drawable.portrait_boy);
            genderImageView.setImageResource(R.drawable.gender_male);
        }

        // Fetch user's latest information from server
        WeTrackClient.getInstance().getUserInfo(username, new EntityCallback<User>() {
            @Override
            protected void onReceive(User receivedUser) {
                Log.d(TAG, "Received user `" + receivedUser.toString() + "` from server.");
                if (receivedUser.equals(user)) {
                    Log.d(TAG, "Received user is same as cached user. Nothing to update.");
                }
                // Update local cache if necessary
                helper.getUserDao().createOrUpdate(receivedUser);
                nicknameTextView.setText(receivedUser.getNickname());
                if (receivedUser.getGender() == User.Gender.Male) {
                    portraitImageView.setImageResource(R.drawable.portrait_boy);
                    genderImageView.setImageResource(R.drawable.gender_male);
                } else {
                    portraitImageView.setImageResource(R.drawable.portrait_girl);
                    genderImageView.setImageResource(R.drawable.gender_female);
                }
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
        am.setDuration(500);
        am.setInterpolator(new AccelerateInterpolator());
        startAnimation(am);
        am.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams layoutParams = ((RelativeLayout.LayoutParams)getLayoutParams());
                layoutParams.setMargins(-Tools.getScreenW() * 2 / 3,0,0,0);
                setLayoutParams(layoutParams);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    public void open() {
        sidebarState = OPEN_STATE;

        RelativeLayout.LayoutParams layoutParams = ((RelativeLayout.LayoutParams)getLayoutParams());
        layoutParams.setMargins(0,0,0,0);
        setLayoutParams(layoutParams);

        int width = getWidth();
        Animation am = new TranslateAnimation(-width * 1f, 0f, 0f, 0f);
        am.setDuration(500);
        am.setInterpolator(new AccelerateInterpolator());
        startAnimation(am);
    }

    public void setLogoutListener(View.OnClickListener onClickListener) {
        logoutButton.setOnClickListener(onClickListener);
    }
}

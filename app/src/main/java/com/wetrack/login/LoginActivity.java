package com.wetrack.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.wetrack.BaseApplication;
import com.wetrack.MainActivity;
import com.wetrack.R;
import com.wetrack.client.CreatedMessageCallback;
import com.wetrack.client.EntityCallback;
import com.wetrack.client.WeTrackClient;
import com.wetrack.database.WeTrackDatabaseHelper;
import com.wetrack.model.Message;
import com.wetrack.model.User;
import com.wetrack.model.UserToken;
import com.wetrack.utils.ConstantValues;
import com.wetrack.utils.PreferenceUtils;

import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getCanonicalName();

    private WeTrackClient client = WeTrackClient.getInstance(ConstantValues.serverBaseUrl, ConstantValues.timeoutSeconds);
    private RuntimeExceptionDao<User, String> userDao;

    private EditText usernameInput;
    private EditText passwordInput;
    private EditText passwordConfirmInput;
    private Button logButton;
    private Button toggleButton;
    private LinearLayout linearLayout;
    private RadioGroup genderRadioGroup;
    private EditText nicknameInput;

    // `true` for sign-in mode, `false` for sign-up mode
    private boolean isSignIn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userDao = OpenHelperManager.getHelper(this, WeTrackDatabaseHelper.class).getUserDao();

        usernameInput = (EditText) findViewById(R.id.input_username);
        passwordInput = (EditText) findViewById(R.id.input_password);
        passwordConfirmInput = (EditText) findViewById(R.id.input_password_confirm);
        nicknameInput = (EditText) findViewById(R.id.input_nickname);
        genderRadioGroup = (RadioGroup) findViewById(R.id.gender_radio_group);
        logButton = (Button) findViewById(R.id.log_button);
        toggleButton = (Button) findViewById(R.id.option_toggle_button);

        logButton.setOnClickListener(new LogBtnOnClickListener());
        toggleButton.setOnClickListener(new ToggleBtnOnClickListener());

        linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
    }

    private class LogBtnOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            disableAll();
            final String username = usernameInput.getText().toString();
            final String password = passwordInput.getText().toString();
            if (username.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Username cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isSignIn) { // Sign in
                client.userLogin(username, password, new EntityCallback<UserToken>() {
                    @Override
                    protected void onResponse(Response<UserToken> response) {
                        enableAll();
                    }

                    @Override
                    protected void onReceive(UserToken token) {
                        PreferenceUtils.saveStringValue(BaseApplication.getContext(), PreferenceUtils.KEY_USERNAME, token.getUsername());
                        PreferenceUtils.saveStringValue(BaseApplication.getContext(), PreferenceUtils.KEY_TOKEN, token.getToken());
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    }

                    @Override
                    protected void onErrorMessage(Message response) {
                        if (response.getStatusCode() == 401)
                            Toast.makeText(LoginActivity.this, "Username or password is incorrect.", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(LoginActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected void onException(Throwable ex) {
                        Toast.makeText(LoginActivity.this, "Exception occurred during the connection: " + ex.getClass().getName(), Toast.LENGTH_SHORT).show();
                        Log.w(TAG, ex);
                    }
                });
            } else { // Sign up
                final String passwordConfirm = passwordConfirmInput.getText().toString();
                if (passwordConfirm.isEmpty() || !password.equals(passwordConfirm)) {
                    Toast.makeText(LoginActivity.this, "Password confirmation is not the same as the provided password.", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String nickname = nicknameInput.getText().toString();
                if (nickname.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Nickname cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                int genderId = genderRadioGroup.getCheckedRadioButtonId();
                if (genderId == -1) {
                    Toast.makeText(LoginActivity.this, "You must select your gender.", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String gender = ((RadioButton) findViewById(genderId)).getText().toString();

                final User user = new User(username, password, nickname);
                user.setGender(User.Gender.valueOf(gender));
                client.createUser(user, new CreatedMessageCallback() {
                    @Override
                    protected void onSuccess(String username, String message) {
                        enableAll();
                        userDao.create(user);
                        Toast.makeText(LoginActivity.this, "You have successfully signed up. Please try to sign in again with your new username and password.", Toast.LENGTH_SHORT).show();
                        User savedUser = userDao.queryForId(username);
                        Log.d(TAG, "Saved User{username=" + savedUser.getUsername() + "}");
                        toggleButton.callOnClick();
                    }

                    @Override
                    protected void onFail(String message, int failedStatusCode) {
                        enableAll();
                        if (failedStatusCode == 403)
                            Toast.makeText(LoginActivity.this, "User with the same username already exists.", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected void onError(Throwable ex) {
                        enableAll();
                        Toast.makeText(LoginActivity.this, "Exception occurred during the connection: " + ex.getClass().getName(), Toast.LENGTH_SHORT).show();
                        Log.w(TAG, ex);
                    }
                });
            }
        }
    }

    private class ToggleBtnOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            disableAll();

            if (isSignIn) {
                isSignIn = false;

                final float moveOffset = -50f;
                int currentPaddingTop = linearLayout.getPaddingTop();
                linearLayout.setPadding(0, (int) (currentPaddingTop + moveOffset), 0, 0);
                Animation am = new TranslateAnimation(0f, 0f, -moveOffset, 0f);
                am.setDuration(500);
                linearLayout.startAnimation(am);
                am.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        passwordConfirmInput.setVisibility(View.VISIBLE);
                        nicknameInput.setVisibility(View.VISIBLE);
                        genderRadioGroup.setVisibility(View.VISIBLE);

                        logButton.setText(R.string.signup);
                        toggleButton.setText(R.string.loginInSign);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            } else {
                isSignIn = true;

                passwordConfirmInput.setVisibility(View.GONE);
                nicknameInput.setVisibility(View.GONE);
                genderRadioGroup.setVisibility(View.GONE);

                logButton.setText(R.string.login);
                toggleButton.setText(R.string.signupInLogin);

                final float moveOffset = 50f;

                int currentPaddingTop = linearLayout.getPaddingTop();
                linearLayout.setPadding(0, (int) (currentPaddingTop + moveOffset), 0, 0);

                Animation am = new TranslateAnimation(0f, 0f, -moveOffset, 0f);
                am.setDuration(500);
                linearLayout.startAnimation(am);
            }
            enableAll();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userDao = null;
        OpenHelperManager.releaseHelper();
    }

    /** Enables all input controls */
    private void enableAll() {
        usernameInput.setEnabled(true);
        passwordInput.setEnabled(true);
        passwordConfirmInput.setEnabled(true);
        nicknameInput.setEnabled(true);
        genderRadioGroup.setEnabled(true);
        logButton.setEnabled(true);
        toggleButton.setEnabled(true);
    }

    /** Disables all input controls */
    private void disableAll() {
        usernameInput.setEnabled(false);
        passwordInput.setEnabled(false);
        passwordConfirmInput.setEnabled(false);
        nicknameInput.setEnabled(false);
        genderRadioGroup.setEnabled(false);
        logButton.setEnabled(false);
        toggleButton.setEnabled(false);
    }
}


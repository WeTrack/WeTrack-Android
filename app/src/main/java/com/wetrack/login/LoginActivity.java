package com.wetrack.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.wetrack.BaseApplication;
import com.wetrack.MainActivity;
import com.wetrack.R;
import com.wetrack.database.UserDataFormat;
import com.wetrack.utils.PreferenceUtils;

public class LoginActivity extends AppCompatActivity {

    private ImageView logoImageView;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText passwordReinput;
    private Button firstButton;
    private Button secondButton;
    private LinearLayout linearLayout;
    private RadioGroup gender_radiogroup;
    private EditText nicknameInput;

    //true means login mode, false means signup mode
    private boolean mode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        logoImageView = (ImageView) findViewById(R.id.logo_imageview);
        emailInput = (EditText) findViewById(R.id.input_email);
        passwordInput = (EditText) findViewById(R.id.input_password);
        passwordReinput = (EditText) findViewById(R.id.reinput_password);
        nicknameInput = (EditText) findViewById(R.id.input_nickname);
        gender_radiogroup = (RadioGroup) findViewById(R.id.gender_radiogroup);
        firstButton = (Button) findViewById(R.id.first_button);
        secondButton = (Button) findViewById(R.id.second_button);

        firstButton.setOnClickListener(new MyFirstOnClickListener());
        secondButton.setOnClickListener(new MySecondOnClickListener());

        linearLayout = (LinearLayout) findViewById(R.id.linearlayout);

//        gender_radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//
//            }
//        });
    }

    private class MyFirstOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            disableAll();
            if (mode == true) {
                //login
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                if (email != null && !email.equals("") &&
                        password != null) {
                    PreferenceUtils.saveStringValue(BaseApplication.getContext(), PreferenceUtils.KEY_USERNAME, email);

                    Intent intent = new Intent(LoginActivity.this,
                            MainActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                } else {
                    enableAll();
                    Toast.makeText(LoginActivity.this, "wrong login message", Toast.LENGTH_SHORT).show();
                }
            } else {
                //signup
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                String rePassword = passwordInput.getText().toString();
                String nickname = nicknameInput.getText().toString();
                int genderId = gender_radiogroup.getCheckedRadioButtonId();
                String gender = ((RadioButton)findViewById(genderId)).getText().toString();
                if (email != null && !email.equals("") &&
                        password != null && rePassword != null && password.equals(rePassword) &&
                        nickname != null && !nickname.equals("") &&
                        gender != null && !gender.equals("")) {
                    PreferenceUtils.saveStringValue(BaseApplication.getContext(), PreferenceUtils.KEY_USERNAME, email);

                    UserDataFormat userDataFormat = new UserDataFormat(email, password, nickname, null, email, gender,null);
                    userDataFormat.addUser();

                    Intent intent = new Intent(LoginActivity.this,
                            MainActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                } else {
                    enableAll();
                    Toast.makeText(LoginActivity.this, "wrong signup message", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class MySecondOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            disableAll();

            if (mode == true) {
                mode = false;
                final float moveOffset = -50f;

                int currentPaddingTop = linearLayout.getPaddingTop();
                linearLayout.setPadding(0, (int) (currentPaddingTop + moveOffset), 0, 0);

                Animation am = new TranslateAnimation(0f, 0f, -moveOffset, 0f);
                am.setDuration(500);
//            am.setInterpolator(new AccelerateInterpolator());
                linearLayout.startAnimation(am);
                am.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        passwordReinput.setVisibility(View.VISIBLE);
                        nicknameInput.setVisibility(View.VISIBLE);
                        gender_radiogroup.setVisibility(View.VISIBLE);

                        firstButton.setText(R.string.signup);
                        secondButton.setText(R.string.loginInSign);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            } else {
                mode = true;

                passwordReinput.setVisibility(View.GONE);
                nicknameInput.setVisibility(View.GONE);
                gender_radiogroup.setVisibility(View.GONE);

                firstButton.setText(R.string.login);
                secondButton.setText(R.string.signupInLogin);

                final float moveOffset = 50f;

                int currentPaddingTop = linearLayout.getPaddingTop();
                linearLayout.setPadding(0, (int) (currentPaddingTop + moveOffset), 0, 0);

                Animation am = new TranslateAnimation(0f, 0f, -moveOffset, 0f);
                am.setDuration(500);
//            am.setInterpolator(new AccelerateInterpolator());
                linearLayout.startAnimation(am);
            }
            enableAll();
        }
    }

    private void enableAll() {
        emailInput.setEnabled(true);
        passwordInput.setEnabled(true);
        passwordReinput.setEnabled(true);
        nicknameInput.setEnabled(true);
        gender_radiogroup.setEnabled(true);
        firstButton.setEnabled(true);
        secondButton.setEnabled(true);
    }

    private void disableAll() {
        emailInput.setEnabled(false);
        passwordInput.setEnabled(false);
        passwordReinput.setEnabled(false);
        nicknameInput.setEnabled(false);
        gender_radiogroup.setEnabled(false);
        firstButton.setEnabled(false);
        secondButton.setEnabled(false);
    }
}

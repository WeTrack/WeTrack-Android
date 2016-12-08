package com.wetrack;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wetrack.client.EntityCallback;
import com.wetrack.client.EntityCallbackWithLog;
import com.wetrack.client.MessageCallback;
import com.wetrack.client.WeTrackClient;
import com.wetrack.model.Message;
import com.wetrack.model.User;
import com.wetrack.utils.ConstantValues;
import com.wetrack.utils.PreferenceUtils;

import java.io.InputStream;

import retrofit2.Response;

public class UserInfoActivity extends AppCompatActivity {

    private RelativeLayout container;
    private LinearLayout linear;
    private ImageButton backButton = null;
    private Button editButton = null;
    private ImageButton commitButton = null;
    private ImageButton portraitButton = null;
    private EditText nicknameEdit = null;
    private TextView genderText = null;
    private RadioGroup genderRadioGroup;

    private String stringGenderMale = null;
    private String stringGenderFemale = null;

    private WeTrackClient client = WeTrackClient.singleton();
    private InputMethodManager imeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        setResult(RESULT_CANCELED);

        stringGenderMale = getResources().getString(R.string.gender_male);
        stringGenderFemale = getResources().getString(R.string.gender_female);

        container = (RelativeLayout) findViewById(R.id.user_info_container);
        linear = (LinearLayout) findViewById(R.id.user_info_list);

        backButton = (ImageButton) findViewById(R.id.user_info_back_button);
        editButton = (Button) findViewById(R.id.user_info_edit_button);
        commitButton = (ImageButton) findViewById(R.id.user_info_commit_button);
        portraitButton = (ImageButton) findViewById(R.id.user_info_portrait);
        nicknameEdit = (EditText) findViewById(R.id.user_info_nickname_edit);
        genderText = (TextView) findViewById(R.id.user_info_gender_text);
        genderRadioGroup = (RadioGroup) findViewById(R.id.user_info_gender_radio);

        updateUserPortrait();

        container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });
        linear.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfoActivity.this.finish();
            }
        });

        portraitButton.setEnabled(false);
        portraitButton.setOnClickListener(new PortraitOnClickListener());

        genderRadioGroup.setOnCheckedChangeListener(new GenderOnCheckedChangeListener());

        editButton.setOnClickListener(new EditOnClickListener());
        commitButton.setOnClickListener(new CommitOnClickListener());

        String currentUserName = PreferenceUtils.getCurrentUsername();
        client.getUserInfo(currentUserName, new ReceivedUserInfoCallback());

        imeManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private class EditOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            editButton.setVisibility(View.GONE);
            commitButton.setVisibility(View.VISIBLE);
            nicknameEdit.setEnabled(true);
            portraitButton.setEnabled(true);
            genderText.setVisibility(View.INVISIBLE);
            genderRadioGroup.setVisibility(View.VISIBLE);
        }
    }

    private class CommitOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final String nickname = nicknameEdit.getText().toString();
            if (nickname.equals("")) {
                Toast.makeText(UserInfoActivity.this,
                        "Please input nickname", Toast.LENGTH_SHORT).show();
                return;
            }

            int genderId = genderRadioGroup.getCheckedRadioButtonId();
            if (genderId == -1) {
                Toast.makeText(UserInfoActivity.this,
                        "You must select your gender.", Toast.LENGTH_SHORT).show();
                return;
            }
            final String gender = ((RadioButton) findViewById(genderId)).getText().toString();

            String currentUsername = PreferenceUtils.getCurrentUsername();
            final User user = new User(currentUsername, null, nickname);
            user.setGender(User.Gender.valueOf(gender));

            client.updateUser(currentUsername, PreferenceUtils.getCurrentToken(), user, new CommitUserInfoMessageCallback());

//            client.uploadUserPortrait();


        }
    }

    private class PortraitOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_APP_GALLERY);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), ConstantValues.CHOOSE_FILE_REQUEST_CODE);
        }
    }

    private class GenderOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.user_info_radio_male) {
                genderText.setText(stringGenderMale);
            } else {
                genderText.setText(stringGenderFemale);
            }
        }
    }

    private void updateUserPortrait() {
        String username = PreferenceUtils.getCurrentUsername();

        client.getUserPortrait(username, true, new EntityCallbackWithLog<Bitmap>(this) {
            @Override
            protected void onReceive(Bitmap bitmap) {
                portraitButton.setImageBitmap(bitmap);
            }

            @Override
            protected void onErrorMessage(Message response) {
                if (response.getStatusCode() == 404)
                    portraitButton.setImageResource(R.drawable.portrait_boy);
            }
        });
    }

    private class ReceivedUserInfoCallback extends EntityCallback<User> {
        @Override
        protected void onReceive(User value) {
            super.onReceive(value);
            nicknameEdit.setText(value.getNickname());
            if (value.getGender().equals(User.Gender.Male)) {
                genderText.setText(stringGenderMale);
                genderRadioGroup.check(R.id.user_info_radio_male);
            } else {
                genderText.setText(stringGenderFemale);
                genderRadioGroup.check(R.id.user_info_radio_female);
            }
        }

        @Override
        protected void onResponse(Response<User> response) {
            super.onResponse(response);
        }

        @Override
        protected void onException(Throwable ex) {
            super.onException(ex);
            Toast.makeText(UserInfoActivity.this,
                    "getting user info exception", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onErrorMessage(Message response) {
            super.onErrorMessage(response);
            Toast.makeText(UserInfoActivity.this,
                    "getting user info error", Toast.LENGTH_SHORT).show();
        }
    }

    private class CommitUserInfoMessageCallback extends MessageCallback {
        @Override
        protected void onSuccess(String message) {
            super.onSuccess(message);

            Intent intent = new Intent();
            intent.putExtra("USER_INFO", true);
            setResult(RESULT_OK, intent);

            hideKeyboard();

            commitButton.setVisibility(View.GONE);
            editButton.setVisibility(View.VISIBLE);
            nicknameEdit.setEnabled(false);
            portraitButton.setEnabled(false);
            genderText.setVisibility(View.VISIBLE);
            genderRadioGroup.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onFail(String message, int failedStatusCode) {
            super.onFail(message, failedStatusCode);
            Toast.makeText(UserInfoActivity.this,
                    "committing user info fails", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onError(Throwable ex) {
            super.onError(ex);
            Toast.makeText(UserInfoActivity.this,
                    "committing user info gets error", Toast.LENGTH_SHORT).show();
        }

        @Override
        public int getSuccessfulStatusCode() {
            return super.getSuccessfulStatusCode();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantValues.CHOOSE_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(UserInfoActivity.this,
                        "You didn't select any picture", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void hideKeyboard(){
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                imeManager.hideSoftInputFromWindow(getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}

package com.wetrack;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.wetrack.view.AddFriendItemView;
import com.wetrack.client.EntityCallbackWithLog;
import com.wetrack.client.MessageCallbackWithLog;
import com.wetrack.client.WeTrackClient;
import com.wetrack.model.User;
import com.wetrack.utils.PreferenceUtils;

import retrofit2.Response;

public class AddFriendActivity extends AppCompatActivity {

    private WeTrackClient client = WeTrackClient.singleton();

    private String username;
    private String token;

    private EditText searchText;
    private ImageButton searchButton;
    private LinearLayout searchResultLayout;

    private ScrollView scrollview;
    private InputMethodManager imeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        username = PreferenceUtils.getCurrentUsername();
        token = PreferenceUtils.getCurrentToken();

        setResult(RESULT_CANCELED);

        ImageButton backButton = (ImageButton) findViewById(R.id.add_friend_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFriendActivity.this.finish();
            }
        });

        searchButton = (ImageButton) findViewById(R.id.add_friend_search_button);
        searchButton.setOnClickListener(new SearchOnClickListener());

        searchText = (EditText) findViewById(R.id.add_friend_search_text);
        searchResultLayout = (LinearLayout) findViewById(R.id.add_friend_list);

        scrollview = (ScrollView) findViewById(R.id.add_friend_layout);

        imeManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        scrollview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });

    }

    private class SearchOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            searchButton.setEnabled(false);
            String searchUsername = searchText.getText().toString();
            if (searchUsername.trim().isEmpty()) {
                Toast.makeText(AddFriendActivity.this, "Username for search cannot be empty.", Toast.LENGTH_SHORT).show();
                searchButton.setEnabled(true);
                return;
            }
            client.getUserInfo(searchUsername, new EntityCallbackWithLog<User>(AddFriendActivity.this) {
                @Override
                protected void onResponse(Response<User> response) {
                    searchButton.setEnabled(true);
                }

                @Override
                protected void onReceive(User user) {
                    AddFriendItemView addFriendItemView = new AddFriendItemView(AddFriendActivity.this);
                    addFriendItemView.setUser(user);
                    searchResultLayout.removeAllViews();
                    searchResultLayout.addView(addFriendItemView);
                    addFriendItemView.setOnAddFriend(new MyOnAddFriendListener());
                }
            });
        }
    }

    private class MyOnAddFriendListener implements AddFriendItemView.OnAddFriendListener {
        @Override
        public void onAddFriend(String friendName) {
            client.addFriend(username, token, friendName, new MessageCallbackWithLog(AddFriendActivity.this){
                @Override
                protected void onSuccess(String message) {
                    Toast.makeText(AddFriendActivity.this, message, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    AddFriendActivity.this.finish();
                }
            });
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

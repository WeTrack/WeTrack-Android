package com.wetrack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wetrack.view.AddFriendItemView;
import com.wetrack.client.EntityCallbackWithLog;
import com.wetrack.client.MessageCallbackWithLog;
import com.wetrack.client.WeTrackClient;
import com.wetrack.client.WeTrackClientWithDbCache;
import com.wetrack.model.User;
import com.wetrack.utils.PreferenceUtils;

import retrofit2.Response;

public class AddFriendActivity extends AppCompatActivity {

    private WeTrackClient client = WeTrackClientWithDbCache.singleton();

    private String username;
    private String token;

    private EditText searchText;
    private ImageButton searchButton;
    private LinearLayout searchResultLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        username = PreferenceUtils.getStringValue(PreferenceUtils.KEY_USERNAME);
        token = PreferenceUtils.getStringValue(PreferenceUtils.KEY_TOKEN);

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
}

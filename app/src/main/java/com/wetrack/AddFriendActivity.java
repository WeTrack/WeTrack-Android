package com.wetrack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class AddFriendActivity extends AppCompatActivity {

    private ImageButton backButton;
    private ImageButton searchButton;
    private EditText searchText;
    private LinearLayout listLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        setResult(RESULT_CANCELED);

        backButton = (ImageButton) findViewById(R.id.add_friend_back);
        searchButton = (ImageButton) findViewById(R.id.add_friend_search_button);
        searchText = (EditText) findViewById(R.id.add_friend_search_text);
        listLinearLayout = (LinearLayout) findViewById(R.id.add_friend_list);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.fade_in, R.anim.slide_out_up);
                AddFriendActivity.this.finish();
            }
        });

        searchButton.setOnClickListener(new SearchOnClickListener());
    }

    private class SearchOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //TODO use searchText to get the name-list of users,
            //TODO then add users into listLinearLayout, in the same way as following:
            AddFriendItemView addFriendItemView = new AddFriendItemView(AddFriendActivity.this);
            addFriendItemView.setContact("ken", "male");
            listLinearLayout.addView(addFriendItemView);
            addFriendItemView.setOnAddFriend(new MyOnAddFriendListener());
        }
    }

    private class MyOnAddFriendListener implements AddFriendItemView.OnAddFriendListener {
        @Override
        public void onAddFriend(String friendName) {
            //TODO add friend by friendName

            // TODO put the information into intent, then get it in 'onActivityResult' in MainActivity
            Intent intent = new Intent();

            setResult(RESULT_OK, intent);

            overridePendingTransition(R.anim.fade_in, R.anim.slide_out_up);
            AddFriendActivity.this.finish();
        }
    }
}

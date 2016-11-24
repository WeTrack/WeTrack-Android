package com.wetrack;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.wetrack.utils.ConstantValues;

import java.util.ArrayList;

public class CreateGroupActivity extends AppCompatActivity {


    private ImageButton backButton;
    private Button configButton;
    private LinearLayout listLinearLayout;

    private ArrayList<String> allFriendNamesToCreateGroup = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        setResult(RESULT_CANCELED);

        backButton = (ImageButton) findViewById(R.id.create_group_back);
        configButton = (Button) findViewById(R.id.create_group_config);
        listLinearLayout = (LinearLayout) findViewById(R.id.create_group_list);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.fade_in, R.anim.slide_out_up);
                CreateGroupActivity.this.finish();
            }
        });

        configButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO use the data stored in 'allFriendNamesToCreateGroup' to create a new group

                // TODO put the information into intent, then get it in 'onActivityResult' in MainActivity
                Intent intent = new Intent();

                setResult(RESULT_OK, intent);

                overridePendingTransition(R.anim.fade_in, R.anim.slide_out_up);
                CreateGroupActivity.this.finish();
            }
        });

        //TODO add all friends into listLinearLayout, in the same way as following:
        CreateGroupItemView createGroupItemView = new CreateGroupItemView(this);
        createGroupItemView.setContact("ken", "male");
        listLinearLayout.addView(createGroupItemView);
        createGroupItemView.setOnMyCheckBoxChangeListener(new ItemCheckedChangedListener());
    }

    private class ItemCheckedChangedListener implements CreateGroupItemView.OnMyCheckBoxChangedListener {
        @Override
        public void addFriendToGroup(String friendName) {
            if (!allFriendNamesToCreateGroup.contains(friendName)) {
                allFriendNamesToCreateGroup.add(friendName);
            }
            refreshConfigButton();
        }

        @Override
        public void removeFriendFromGroup(String friendName) {
            if (allFriendNamesToCreateGroup.contains(friendName)) {
                allFriendNamesToCreateGroup.remove(friendName);
            }
            refreshConfigButton();
        }

        private void refreshConfigButton() {
            if (allFriendNamesToCreateGroup.isEmpty()) {
                configButton.setEnabled(false);
                configButton.setTextColor(Color.LTGRAY);
            } else {
                configButton.setEnabled(true);
                configButton.setTextColor(Color.BLACK);
            }
        }
    }
}

package com.wetrack.contact;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wetrack.BaseApplication;
import com.wetrack.R;
import com.wetrack.database.DataFormat;
import com.wetrack.database.FriendDataFormat;
import com.wetrack.database.GroupDataFormat;
import com.wetrack.utils.ConstantValues;
import com.wetrack.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by moziliang on 16/11/18.
 */
public class ContactView extends RelativeLayout {

    private ImageButton backButton;
    private TextView titleTextView;
    private Button configButton;
    private ImageButton searchButton;
    private LinearLayout listLinearLayout;
    private RelativeLayout searchRelativeLayout;

    private int mode;
    String username;

    public ContactView(Context context) {
        super(context);
        init();
    }
    public ContactView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public ContactView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        username = PreferenceUtils.getStringValue(BaseApplication.getContext(), PreferenceUtils.KEY_USERNAME);

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        RelativeLayout contactLayout = (RelativeLayout)layoutInflater.inflate(R.layout.contact_view, null);
        addView(contactLayout);

        listLinearLayout = (LinearLayout) findViewById(R.id.list_linearlayout);
        backButton = (ImageButton) findViewById(R.id.back_button);
        titleTextView = (TextView) findViewById(R.id.title_textview);
        configButton = (Button) findViewById(R.id.config_button);
        searchButton = (ImageButton) findViewById(R.id.search_button);

        searchRelativeLayout = (RelativeLayout) findViewById(R.id.search_relativelayout);

        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });

        searchButton.setOnClickListener(new SearchOnClickListener());

        configButton.setOnClickListener(new ConfigOnClickListner());
    }

    private class SearchOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            ContactItemView.setMode(ConstantValues.CONTACT_MODE_ADD_FRIEND);

            ContactItemView contactItemView1 = new ContactItemView(getContext());
            contactItemView1.setContact(new FriendDataFormat(username, "Cherry", "female"));
            listLinearLayout.addView(contactItemView1);
            contactItemView1.setOnAddFriendSucceed(new MyOnAddFriendSucceedListener());

            ContactItemView contactItemView2 = new ContactItemView(getContext());
            contactItemView2.setContact(new FriendDataFormat(username, "Jerry", "male"));
            listLinearLayout.addView(contactItemView2);
            contactItemView2.setOnAddFriendSucceed(new MyOnAddFriendSucceedListener());

            ContactItemView contactItemView3 = new ContactItemView(getContext());
            contactItemView3.setContact(new FriendDataFormat(username, "Manyee", "female"));
            listLinearLayout.addView(contactItemView3);
            contactItemView3.setOnAddFriendSucceed(new MyOnAddFriendSucceedListener());

            ContactItemView contactItemView4 = new ContactItemView(getContext());
            contactItemView4.setContact(new FriendDataFormat(username, "Mike", "male"));
            listLinearLayout.addView(contactItemView4);
            contactItemView4.setOnAddFriendSucceed(new MyOnAddFriendSucceedListener());
        }
    }

    private class MyOnAddFriendSucceedListener implements ContactItemView.OnAddFriendSucceedListener {
        @Override
        public void onAddFriendSucceed() {
            listLinearLayout.removeAllViews();
            hide();
        }
    }

    private class ConfigOnClickListner implements OnClickListener {
        @Override
        public void onClick(View v) {
            ArrayList<String>groupMembers = new ArrayList<>();
            for (DataFormat dataFormat : dataAndIsChecked.keySet()) {
                groupMembers.add(dataFormat.getValueByName(FriendDataFormat.ATTRI_FRIEND_NAME));
            }
            if ((new GroupDataFormat("new group", groupMembers)).addGroup()) {
                hide();
            }
        }
    }
    private Map<DataFormat, Boolean> dataAndIsChecked = new HashMap();
    private void listAllFriend() {
        for(DataFormat dataFormat : FriendDataFormat.getAllFriend()) {
            ContactItemView contactItemView = new ContactItemView(getContext());
            contactItemView.setContact(new FriendDataFormat(dataFormat));
            listLinearLayout.addView(contactItemView);
            contactItemView.setOnMyCheckedChangeListener(new ItemCheckedChangedListener());
        }
    }
    private class ItemCheckedChangedListener implements ContactItemView.OnMyCheckedChangedListener {
        @Override
        public void onMyCheckedChanged(FriendDataFormat friendDataFormat, boolean isChecked) {
            dataAndIsChecked.put(friendDataFormat, isChecked);
            if (isChecked == true) {
                configButton.setEnabled(true);
            }
        }
    }

    public void setMode(int mode) {
        this.mode = mode;
        switch (this.mode) {
            case ConstantValues.CONTACT_MODE_NEW_GROUP:
                titleTextView.setText(R.string.newGroup);
                configButton.setVisibility(VISIBLE);
                configButton.setEnabled(false);
                searchRelativeLayout.setVisibility(GONE);
                ContactItemView.setMode(ConstantValues.CONTACT_MODE_NEW_GROUP);
                listLinearLayout.removeAllViews();
                listAllFriend();
                break;
            case ConstantValues.CONTACT_MODE_ADD_FRIEND:
                titleTextView.setText(R.string.addFriend);
                configButton.setVisibility(GONE);
                searchRelativeLayout.setVisibility(VISIBLE);
                ContactItemView.setMode(ConstantValues.CONTACT_MODE_ADD_FRIEND);
                listLinearLayout.removeAllViews();
                break;
        }
    }
    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }
}

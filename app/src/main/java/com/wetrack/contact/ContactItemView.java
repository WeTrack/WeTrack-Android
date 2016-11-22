package com.wetrack.contact;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wetrack.BaseApplication;
import com.wetrack.R;
import com.wetrack.database.FriendDataFormat;
import com.wetrack.database.GroupDataFormat;
import com.wetrack.utils.PreferenceUtils;

import java.util.ArrayList;

/**
 * Created by moziliang on 16/11/19.
 */
public class ContactItemView extends RelativeLayout {

    private ImageView portraitImageView;
    private TextView nameTextView;
    private ImageView genderImageView;
    private Button addButton;
    private CheckBox checkBox;

    private String username;
    private String friendName;
    private String friendGender;
    private FriendDataFormat friendDataFormat;
    static private int mode;

    public ContactItemView(Context context) {
        super(context);
        init();
    }
    public ContactItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public ContactItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        RelativeLayout contactItemLayout = (RelativeLayout)layoutInflater.inflate(R.layout.contact_item, null);
        addView(contactItemLayout);

        portraitImageView = (ImageView) findViewById(R.id.item_portrait);
        nameTextView = (TextView)findViewById(R.id.item_name);
        genderImageView = (ImageView)findViewById(R.id.item_gender);
        addButton = (Button) findViewById(R.id.item_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (friendDataFormat.addFriend()) {
                    ArrayList<String> groupMembers = new ArrayList<>();
                    String friendName = friendDataFormat.getValueByName(FriendDataFormat.ATTRI_FRIEND_NAME);
                    groupMembers.add(friendName);
                    if ((new GroupDataFormat(friendName, groupMembers)).addGroup()) {
                        if (onAddFriendSucceedListener != null) {
                            onAddFriendSucceedListener.onAddFriendSucceed();
                            Toast.makeText(getContext(), "add friend success", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "add friend error", Toast.LENGTH_SHORT).show();
                }
            }
        });
        checkBox = (CheckBox) findViewById(R.id.item_check);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onMyCheckedChangedListener != null) {
                    onMyCheckedChangedListener.onMyCheckedChanged(friendDataFormat, isChecked);
                }
            }
        });
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.getVisibility() == VISIBLE) {
                    checkBox.setChecked(!checkBox.isChecked());
                }
            }
        });

        username = PreferenceUtils.getStringValue(BaseApplication.getContext(), PreferenceUtils.KEY_USERNAME);
    }

//    public void setContact(FriendDataFormat friendDataFormat) {
//        this.friendDataFormat = friendDataFormat;
//        friendName = friendDataFormat.getValueByName(FriendDataFormat.ATTRI_FRIEND_NAME);
//        friendGender = friendDataFormat.getValueByName(FriendDataFormat.ATTRI_FRIEND_GENDER);
//
//        nameTextView.setText(friendName);
//        if (friendGender.equals("male")) {
//            portraitImageView.setImageResource(R.drawable.portrait_boy);
//            genderImageView.setImageResource(R.drawable.gender_male);
//        } else {
//            portraitImageView.setImageResource(R.drawable.portrait_girl);
//            genderImageView.setImageResource(R.drawable.gender_female);
//        }
//
//        switch (mode) {
//            case ConstantValues.CONTACT_MODE_NEW_GROUP:
//                addButton.setVisibility(GONE);
//                checkBox.setVisibility(VISIBLE);
//                break;
//            case ConstantValues.CONTACT_MODE_ADD_FRIEND:
//                addButton.setVisibility(VISIBLE);
//                checkBox.setVisibility(GONE);
//                break;
//        }
//    }

    static public void setMode(int mode_code) {
        mode = mode_code;
    }

    //below three are for CheckBox
    public void setOnMyCheckedChangeListener(OnMyCheckedChangedListener onMyCheckedChangedListener) {
        this.onMyCheckedChangedListener = onMyCheckedChangedListener;
    }
    public interface OnMyCheckedChangedListener {
        public void onMyCheckedChanged(FriendDataFormat friendDataFormat, boolean isChecked);
    }
    private OnMyCheckedChangedListener onMyCheckedChangedListener = null;

    //below three are for add-friend Button
    public void setOnAddFriendSucceed(OnAddFriendSucceedListener onAddFriendSucceedListener) {
        this.onAddFriendSucceedListener = onAddFriendSucceedListener;
    }
    public interface OnAddFriendSucceedListener{
        public void onAddFriendSucceed();
    }
    private OnAddFriendSucceedListener onAddFriendSucceedListener = null;
}

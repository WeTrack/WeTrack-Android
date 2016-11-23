package com.wetrack;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CreateGroupItemView extends RelativeLayout {

    private ImageView portraitImageView;
    private TextView nameTextView;
    private ImageView genderImageView;
    private CheckBox checkBox;

    private String friendName = null;

    public CreateGroupItemView(Context context) {
        super(context);
        init();
    }
    public CreateGroupItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public CreateGroupItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        RelativeLayout contactItemLayout = (RelativeLayout)layoutInflater.inflate(R.layout.create_group_item, null);
        addView(contactItemLayout);

        portraitImageView = (ImageView) findViewById(R.id.create_group_item_portrait);
        nameTextView = (TextView)findViewById(R.id.create_group_item_name);
        genderImageView = (ImageView)findViewById(R.id.create_group_item_gender);
        checkBox = (CheckBox) findViewById(R.id.create_group_item_checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onMyCheckBoxChangedListener != null && friendName != null) {
                    if (isChecked) {
                        onMyCheckBoxChangedListener.addFriendToGroup(friendName);
                    } else {
                        onMyCheckBoxChangedListener.removeFriendFromGroup(friendName);
                    }
                }
            }
        });
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.setChecked(!checkBox.isChecked());
            }
        });
    }

    public void setContact(String friendName, String friendGender) {
        this.friendName = friendName;

        nameTextView.setText(friendName);
        if (friendGender.equals("male")) {
            portraitImageView.setImageResource(R.drawable.portrait_boy);
            genderImageView.setImageResource(R.drawable.gender_male);
        } else {
            portraitImageView.setImageResource(R.drawable.portrait_girl);
            genderImageView.setImageResource(R.drawable.gender_female);
        }
    }

    //below three are for CheckBox
    public void setOnMyCheckBoxChangeListener(OnMyCheckBoxChangedListener onMyCheckBoxChangedListener) {
        this.onMyCheckBoxChangedListener = onMyCheckBoxChangedListener;
    }
    public interface OnMyCheckBoxChangedListener {
        void addFriendToGroup(String friendName);
        void removeFriendFromGroup(String friendName);
    }
    private OnMyCheckBoxChangedListener onMyCheckBoxChangedListener = null;
}

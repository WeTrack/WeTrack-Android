package com.wetrack;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AddFriendItemView extends RelativeLayout {

    private ImageView portraitImageView;
    private TextView nameTextView;
    private ImageView genderImageView;
    private Button addButton;

    private String friendName = null;

    public AddFriendItemView(Context context) {
        super(context);
        init();
    }
    public AddFriendItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public AddFriendItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        RelativeLayout contactItemLayout = (RelativeLayout)layoutInflater.inflate(R.layout.add_friend_item, null);
        addView(contactItemLayout);

        portraitImageView = (ImageView) findViewById(R.id.add_friend_item_portrait);
        nameTextView = (TextView)findViewById(R.id.add_friend_item_name);
        genderImageView = (ImageView)findViewById(R.id.add_friend_item_gender);
        addButton = (Button) findViewById(R.id.add_friend_item_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAddFriendListener != null && friendName != null) {
                    onAddFriendListener.onAddFriend(friendName);
                }
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

    //below three are for add-friend Button
    public void setOnAddFriend(OnAddFriendListener onAddFriendListener) {
        this.onAddFriendListener = onAddFriendListener;
    }
    public interface OnAddFriendListener {
        void onAddFriend(String friendName);
    }
    private OnAddFriendListener onAddFriendListener = null;
}

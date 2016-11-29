package com.wetrack.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wetrack.R;
import com.wetrack.model.User;

public class AddFriendItemView extends RelativeLayout {

    private ImageView portraitImageView;
    private TextView nameTextView;

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

    public void setUser(User user) {
        this.friendName = user.getUsername();

        nameTextView.setText(user.getNickname());
        if(user.getUsername().equals("ken"))
            portraitImageView.setImageResource(R.drawable.portrait_boy);
        else if(user.getUsername().equals("robert.peng"))
            portraitImageView.setImageResource(R.drawable.dai);
        else if(user.getUsername().equals("CCWindy"))
            portraitImageView.setImageResource(R.drawable.windy);
        else
            portraitImageView.setImageResource(R.drawable.head2);
    }

    // below three are for add-friend Button
    public void setOnAddFriend(OnAddFriendListener onAddFriendListener) {
        this.onAddFriendListener = onAddFriendListener;
    }

    public interface OnAddFriendListener {
        void onAddFriend(String friendName);
    }

    private OnAddFriendListener onAddFriendListener = null;
}

package com.wetrack.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wetrack.R;
import com.wetrack.client.EntityCallback;
import com.wetrack.client.WeTrackClient;
import com.wetrack.model.User;

public class CreateChatItemView extends RelativeLayout {

    private ImageView portraitImageView;
    private TextView nameTextView;
    private CheckBox checkBox;

    private String friendName = null;

    public CreateChatItemView(Context context) {
        super(context);
        init();
    }

    public CreateChatItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CreateChatItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        RelativeLayout contactItemLayout = (RelativeLayout)layoutInflater.inflate(R.layout.create_chat_item, null);
        addView(contactItemLayout);

        portraitImageView = (ImageView) findViewById(R.id.portrait);
        nameTextView = (TextView)findViewById(R.id.nickname);
        checkBox = (CheckBox) findViewById(R.id.checkbox);
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

    public void setUser(User user) {
        this.friendName = user.getUsername();

        nameTextView.setText(user.getNickname());

        portraitImageView.setImageResource(R.drawable.portrait_boy);
        WeTrackClient.singleton().getUserPortrait(user.getUsername(), true, new EntityCallback<Bitmap>() {
            @Override
            protected void onReceive(Bitmap value) {
                portraitImageView.setImageBitmap(value);
            }
        });
    }

    // below three are for CheckBox
    public void setOnMyCheckBoxChangeListener(OnMyCheckBoxChangedListener onMyCheckBoxChangedListener) {
        this.onMyCheckBoxChangedListener = onMyCheckBoxChangedListener;
    }

    public interface OnMyCheckBoxChangedListener {
        void addFriendToGroup(String friendName);
        void removeFriendFromGroup(String friendName);
    }

    private OnMyCheckBoxChangedListener onMyCheckBoxChangedListener = null;
}

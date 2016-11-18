package com.wetrack;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by moziliang on 16/11/18.
 */
public class ContactView extends RelativeLayout {
    final static public int MODE_NEW_GROUP = 1;
    final static public int MODE_ADD_FRIEND = 2;

    private ImageButton backButton;
    private TextView titleTextView;
    private Button configButton;

    private int mode;

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

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        RelativeLayout sidebarLayout = (RelativeLayout)layoutInflater.inflate(R.layout.contact_list, null);
        addView(sidebarLayout);

        backButton = (ImageButton) findViewById(R.id.back_button);
        titleTextView = (TextView) findViewById(R.id.title_textview);
        configButton = (Button) findViewById(R.id.config_button);

        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
    }

    public void setMode(int mode) {
        this.mode = mode;
        switch (this.mode) {
            case MODE_NEW_GROUP:
                titleTextView.setText(R.string.newGroup);
                configButton.setText("OK");
                break;
            case MODE_ADD_FRIEND:
                titleTextView.setText(R.string.addFriend);
                configButton.setText("");
                configButton.setEnabled(false);
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

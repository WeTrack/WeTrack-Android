package com.wetrack;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wetrack.client.EntityCallback;
import com.wetrack.client.WeTrackClientWithDbCache;
import com.wetrack.model.Chat;
import com.wetrack.utils.PreferenceUtils;
import com.wetrack.view.adapter.GroupMemberAdapter;

import java.util.List;

/**
 * Created by Robert on 2016/12/1.
 */

public class GroupInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private GroupMemberAdapter adapter;
    private GridView gridView;
    private TextView chatNameView;
    private  TextView EidtchatNameView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        initView();
    }

    private void initView(){
        ImageButton backButton = (ImageButton) findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        chatNameView = (TextView) findViewById(R.id.title);
        EidtchatNameView = (TextView) findViewById(R.id.txt_groupname);
        gridView = (GridView)  findViewById(R.id.gridview);

        WeTrackClientWithDbCache.singleton().getChatInfo(
                PreferenceUtils.getCurrentChatId(), PreferenceUtils.getCurrentToken(),
                new EntityCallback<Chat>() {
                    @Override
                    protected void onReceive(Chat chat) {
                        load(chat);
                       // chatNameView.setText(chat.getName());
                       // EidtchatNameView.setText(chat.getName());
                    }
                }
        );
    }
    private void load(Chat chat){
        chatNameView.setText(chat.getName());
        EidtchatNameView.setText(chat.getName());
        List<String> usernames = chat.getMemberNames();
        adapter = new GroupMemberAdapter(this, usernames);
        gridView.setAdapter(adapter);
    }
    @Override
    public void onClick(View v) {

    }
}

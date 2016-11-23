package com.wetrack;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.wetrack.Adaptor.ChatMessageAdaptor;
import com.wetrack.model.Chat;
import com.wetrack.model.ChatMessage;
import com.wetrack.model.User;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends FragmentActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private ListView listView;
    private EditText mEditTextContent;
    private Button buttonSend;
    private RelativeLayout edittext_layout;
    private ChatMessageAdaptor adapter;
    private InputMethodManager manager;
    //List<String> AmyMessage;
    //List<String> BobMessage;
    private User me;
    private User user1;
    private User user2;
    private List<ChatMessage> chatMessageList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
        setUpView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                // 点击发送按钮(发文字)
                String s = mEditTextContent.getText().toString();
                sendText(s);
                break;
            default:
                break;
        }
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        listView = (ListView) findViewById(R.id.list);
        mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
        buttonSend = (Button) findViewById(R.id.btn_send);
        buttonSend.setOnClickListener(this);
        edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
        edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);
        edittext_layout.requestFocus();
        mEditTextContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    edittext_layout
                            .setBackgroundResource(R.drawable.input_bar_bg_active);
                } else {
                    edittext_layout
                            .setBackgroundResource(R.drawable.input_bar_bg_normal);
                }

            }
        });
    }

    private void setUpView() {
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //test

        Chat chatTest = new Chat();
        User user1 = new User();
        User user2 = new User();
        user1.setUsername("Amy");
        user1.setIconUrl("head1.png");
        user2.setUsername("Bob");
        user2.setIconUrl("head2.png");
        chatTest.setChatId("chatId1");
        List<String> usernames = new ArrayList<String>();
        usernames.add(user1.getUsername());
        usernames.add(user2.getUsername());
        chatTest.setMemberNames(usernames);
        ChatMessage defaultMessage = new ChatMessage();
        defaultMessage.setChatId(chatTest.getChatId());
        defaultMessage.setFromUsername(user1.getUsername());
        defaultMessage.setContent("This is a default message sent by Amy.");
        defaultMessage.setSendTime(LocalDateTime.parse("2016-11-23"));

        chatMessageList = new ArrayList<ChatMessage>();
        chatMessageList.add(defaultMessage);
        adapter = new ChatMessageAdaptor(this, chatMessageList, chatTest, user2);
        listView.setAdapter(adapter);
        listView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                edittext_layout.requestFocus();
                return false;
            }
        });

    }

    public void editClick(View v) {
        listView.setSelection(listView.getCount() - 1);
    }

    private void hideKeyboard(){
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void sendText(String s){
        //test
        ChatMessage sendMessage = new ChatMessage();
        sendMessage.setFromUsername("Bob");
        sendMessage.setSendTime(LocalDateTime.parse("2016-11-23"));
        sendMessage.setContent(s);
        chatMessageList.add(sendMessage);
        adapter.refresh(chatMessageList);
        listView.setSelection(listView.getCount() - 1);
        mEditTextContent.setText("");
    }
}

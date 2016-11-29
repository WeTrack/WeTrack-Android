package com.wetrack;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.wetrack.client.EntityCallback;
import com.wetrack.client.WeTrackClient;
import com.wetrack.client.WeTrackClientWithDbCache;
import com.wetrack.database.ChatMessageDao;
import com.wetrack.database.WeTrackDatabaseHelper;
import com.wetrack.service.ChatServiceManager;
import com.wetrack.utils.CryptoUtils;
import com.wetrack.utils.PreferenceUtils;
import com.wetrack.view.adapter.ChatMessageAdapter;
import com.wetrack.model.Chat;
import com.wetrack.model.ChatMessage;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private static final Comparator<ChatMessage> messageCmp = new Comparator<ChatMessage>() {
        @Override
        public int compare(ChatMessage m1, ChatMessage m2) {
            return m1.getSendTime().compareTo(m2.getSendTime());
        }
    };

    private final WeTrackClient client = WeTrackClientWithDbCache.singleton();

    private ListView messageListView;
    private EditText messageEditText;
    private RelativeLayout messageEditLayout;
    private ChatMessageAdapter adapter;
    private InputMethodManager imeManager;
    private List<ChatMessage> chatMessageList;

    private ChatServiceManager mChatServiceManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initView();
        initChatServiceManager();
    }

    private void initChatServiceManager() {
        mChatServiceManager = new ChatServiceManager(this) {
            @Override
            public void onReceivedMessage(ChatMessage receivedMessage) {
                if (!receivedMessage.getChatId().equals(PreferenceUtils.getCurrentChatId()))
                    return;
                chatMessageList.add(receivedMessage);
                adapter.refresh(chatMessageList);
                messageListView.setSelection(messageListView.getCount() - 1);
            }

            @Override
            public void onReceivedMessageAck(String ackedMessageId) {
                for (ChatMessage message : chatMessageList) {
                    if (message.getId().equals(ackedMessageId)) {
                        message.setAcked(true);
                        break;
                    }
                }
                adapter.refresh(chatMessageList);
            }
        };
        mChatServiceManager.start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send_btn) {
            String s = messageEditText.getText().toString();
            sendMessage(s);
        }
    }

    private void initView() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        WeTrackClientWithDbCache.singleton().getChatInfo(
                PreferenceUtils.getCurrentChatId(), PreferenceUtils.getCurrentToken(),
                new EntityCallback<Chat>() {
                    @Override
                    protected void onReceive(Chat value) {
                        toolbar.setTitle(value.getName());
                    }
                }
        );

        final Button buttonSend = (Button) findViewById(R.id.send_btn);
        buttonSend.setOnClickListener(this);
        buttonSend.setEnabled(false);

        messageListView = (ListView) findViewById(R.id.message_list_view);
        messageEditText = (EditText) findViewById(R.id.message_edit);

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isEmpty())
                    buttonSend.setEnabled(false);
                else
                    buttonSend.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        messageEditLayout = (RelativeLayout) findViewById(R.id.message_edit_layout);
        messageEditLayout.setBackgroundResource(R.drawable.input_bar_bg_normal);
        messageEditLayout.requestFocus();

        messageEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    messageEditLayout.setBackgroundResource(R.drawable.input_bar_bg_active);
                else
                    messageEditLayout.setBackgroundResource(R.drawable.input_bar_bg_normal);
            }
        });

        imeManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        chatMessageList = new ArrayList<>();
        chatMessageList.addAll(OpenHelperManager.getHelper(this, WeTrackDatabaseHelper.class)
                .getChatMessageDao().getMessageBefore(PreferenceUtils.getCurrentChatId(), LocalDateTime.now(), null));
        adapter = new ChatMessageAdapter(this, chatMessageList, PreferenceUtils.getCurrentUsername());
        messageListView.setAdapter(adapter);
        messageListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                messageEditLayout.requestFocus();
                return false;
            }
        });
        messageListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        messageListView.setSelection(messageListView.getCount() - 1);

        client.getNewChatMessages(PreferenceUtils.getCurrentChatId(), PreferenceUtils.getCurrentToken(),
                new EntityCallback<List<ChatMessage>>() {
                    @Override
                    protected void onReceive(List<ChatMessage> receivedMessages) {
                        chatMessageList.addAll(receivedMessages);
                        Collections.sort(chatMessageList, messageCmp);
                        adapter.refresh(chatMessageList);
                    }
                }
        );
    }

    public void editClick(View v) {
        messageListView.setSelection(messageListView.getCount() - 1);
    }

    private void hideKeyboard(){
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                imeManager.hideSoftInputFromWindow(getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void sendMessage(String message){
        ChatMessage sendMessage = new ChatMessage();
        sendMessage.setFromUsername(PreferenceUtils.getCurrentUsername());
        sendMessage.setChatId(PreferenceUtils.getCurrentChatId());
        sendMessage.setSendTime(LocalDateTime.now());
        sendMessage.setContent(message);
        sendMessage.setId(CryptoUtils.md5Digest(String.format("%s:%s", sendMessage.getSendTime().toString(), message)));
        chatMessageList.add(sendMessage);
        adapter.refresh(chatMessageList);
        messageListView.setSelection(adapter.getCount() - 1);
        messageEditText.setText("");

        // Send message to server
        mChatServiceManager.sendChatMessage(sendMessage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mChatServiceManager != null) {
            mChatServiceManager.stop();
            mChatServiceManager = null;
        }
    }
}

package com.wetrack.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wetrack.R;
import com.wetrack.model.ChatMessage;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

public class ChatMessageAdapter extends BaseAdapter {
    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

    private Context context;

    private List<ChatMessage> chatMessageList;
    private final String currentUsername;

    public ChatMessageAdapter(Context context, List<ChatMessage> chatMessageList, String currentUsername) {
        this.context = context;
        this.chatMessageList = chatMessageList;
        this.currentUsername = currentUsername;
    }

    public void refresh(List<ChatMessage> chatMessageList) {
        this.chatMessageList = chatMessageList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return chatMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.chat_message_item, parent, false);

        LinearLayout leftLayout = (LinearLayout) row.findViewById(R.id.left_layout);
        LinearLayout rightLayout = (LinearLayout) row.findViewById(R.id.right_layout);
        ChatMessage message = chatMessageList.get(position);
        // Received message
        if (!message.getFromUsername().equals(currentUsername)) {
            TextView timestamp = (TextView) row.findViewById(R.id.timestamp);
            TextView content = (TextView) row.findViewById(R.id.left_msg);
            ImageView portrait = (ImageView) row.findViewById(R.id.tv_userhead);

            timestamp.setText(message.getSendTime().toString(formatter));
            content.setText(message.getContent());
            rightLayout.setVisibility(View.GONE);
            leftLayout.setVisibility(View.VISIBLE);


            // TODO Set the portrait for user
            //这里直接使用drawable的head1.png头像

            if(message.getFromUsername().equals("ken"))
                portrait.setBackgroundResource(R.drawable.portrait_boy);
            else if(message.getFromUsername().equals("robert.peng"))
                portrait.setBackgroundResource(R.drawable.dai);
            else if(message.getFromUsername().equals("CCWindy"))
                portrait.setBackgroundResource(R.drawable.windy);
            else
                portrait.setBackgroundResource(R.drawable.head1);

            return row;
        }
        // Sent message
        else {
            TextView timestamp = (TextView) row.findViewById(R.id.timestamp);
            TextView content = (TextView) row.findViewById(R.id.right_msg);
            ImageView portrait = (ImageView) row.findViewById(R.id.iv_userhead);
            timestamp.setText(message.getSendTime().toString(formatter));
            content.setText(message.getContent());
            if (message.isAcked())
                row.findViewById(R.id.pb_sending).setVisibility(View.GONE);
            leftLayout.setVisibility(View.GONE);
            rightLayout.setVisibility(View.VISIBLE);

            // 用currentUser.getIconUrl()currentUser头像
            // 这里直接使用head2.png

            if(currentUsername.equals("ken"))
                portrait.setBackgroundResource(R.drawable.portrait_boy);
            else if(currentUsername.equals("robert.peng"))
                portrait.setBackgroundResource(R.drawable.dai);
            else if(currentUsername.equals("CCWindy"))
                portrait.setBackgroundResource(R.drawable.windy);
            else
                portrait.setBackgroundResource(R.drawable.head2);
            return row;
        }
    }
}

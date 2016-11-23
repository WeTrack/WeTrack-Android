package com.wetrack.Adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wetrack.R;
import com.wetrack.model.Chat;
import com.wetrack.model.ChatMessage;
import com.wetrack.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robert on 2016/11/22.
 */

public class ChatMessageAdaptor extends BaseAdapter {
    private Context context;
    private Chat chat;
    private User currentUser;
    List<ChatMessage> chatMessageList;
    public ChatMessageAdaptor(Context context, List<ChatMessage> chatMessageList, Chat chat, User currentUser){
        this.context = context;
        this.chat = chat;
        this.currentUser = currentUser;
        this.chatMessageList = chatMessageList;
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
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row;
        row = inflater.inflate(R.layout.chat_message, parent, false);
        LinearLayout leftLayout, rightLayout;
        leftLayout =  (LinearLayout) row.findViewById(R.id.left_layout);
        rightLayout = (LinearLayout) row.findViewById(R.id.right_layout);
        //received message
        if (chatMessageList.get(position).getFromUsername() != currentUser.getUsername()) {
            TextView timestamp, content;
            ImageView head;
            timestamp = (TextView) row.findViewById(R.id.timestamp);
            content = (TextView) row.findViewById(R.id.left_msg);
            head = (ImageView)row.findViewById(R.id.tv_userhead);
            timestamp.setText(chatMessageList.get(position).getSendTime().toString());
            content.setText(chatMessageList.get(position).getContent());
            rightLayout.setVisibility(View.GONE);
            leftLayout.setVisibility(View.VISIBLE);


            List<String> chatUsers = new ArrayList<String>();
            chatUsers = chat.getMemberNames();
            User fromUser;
            for(int i = 0; i < chatUsers.size(); i++ ){
                if(chatMessageList.get(position).getFromUsername() == chatUsers.get(i)) {
                    //从本地数据库查找username为message.getFromUsername()的User fromUser
                    //以用fromUser.getIconUrl()获得fromUser的头像
                    break;
                }
            }
            //这里直接使用drawable的head1.png头像
            head.setBackgroundResource(R.drawable.head1);


            return row;
        }
        //sent message
        else if (chatMessageList.get(position).getFromUsername() == currentUser.getUsername()) {
            row = inflater.inflate(R.layout.chat_message, parent, false);
            TextView timestamp, username, content;
            ImageView head;
            timestamp = (TextView) row.findViewById(R.id.timestamp);
            content = (TextView) row.findViewById(R.id.right_msg);
            head = (ImageView)row.findViewById(R.id.iv_userhead);
            timestamp.setText(chatMessageList.get(position).getSendTime().toString());
            content.setText(chatMessageList.get(position).getContent());
            leftLayout.setVisibility(View.GONE);
            rightLayout.setVisibility(View.VISIBLE);

            //用currentUser.getIconUrl()currentUser头像
            //这里直接使用head2.png
            head.setBackgroundResource(R.drawable.head2);
            return row;
        }
        else
            return null;
    }
}

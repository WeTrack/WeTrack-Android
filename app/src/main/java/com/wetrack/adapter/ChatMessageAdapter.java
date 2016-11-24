package com.wetrack.adapter;

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

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

public class ChatMessageAdapter extends BaseAdapter {
    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm");

    private Context context;

    private User currentUser;
    private Chat chat;
    private List<ChatMessage> chatMessageList;

    public ChatMessageAdapter(Context context, List<ChatMessage> chatMessageList,
                              Chat chat, User currentUser){
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
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.chat_message_item, parent, false);

        LinearLayout leftLayout = (LinearLayout) row.findViewById(R.id.left_layout);
        LinearLayout rightLayout = (LinearLayout) row.findViewById(R.id.right_layout);
        // Received message
        if (!chatMessageList.get(position).getFromUsername().equals(currentUser.getUsername())) {
            TextView timestamp = (TextView) row.findViewById(R.id.timestamp);
            TextView content = (TextView) row.findViewById(R.id.left_msg);
            ImageView portrait = (ImageView) row.findViewById(R.id.tv_userhead);

            timestamp.setText(chatMessageList.get(position).getSendTime().toString(formatter));
            content.setText(chatMessageList.get(position).getContent());
            rightLayout.setVisibility(View.GONE);
            leftLayout.setVisibility(View.VISIBLE);


            List<String> chatUsers = chat.getMemberNames();
            for(int i = 0; i < chatUsers.size(); i++ ){
                if(chatMessageList.get(position).getFromUsername() == chatUsers.get(i)) {
                    //从本地数据库查找username为message.getFromUsername()的User fromUser
                    //以用fromUser.getIconUrl()获得fromUser的头像
                    break;
                }
            }
            //这里直接使用drawable的head1.png头像
            portrait.setBackgroundResource(R.drawable.head1);
            return row;
        }
        // Sent message
        else {
            TextView timestamp = (TextView) row.findViewById(R.id.timestamp);
            TextView content = (TextView) row.findViewById(R.id.right_msg);
            ImageView portrait = (ImageView) row.findViewById(R.id.iv_userhead);
            timestamp.setText(chatMessageList.get(position).getSendTime().toString(formatter));
            content.setText(chatMessageList.get(position).getContent());
            leftLayout.setVisibility(View.GONE);
            rightLayout.setVisibility(View.VISIBLE);

            // 用currentUser.getIconUrl()currentUser头像
            // 这里直接使用head2.png
            portrait.setBackgroundResource(R.drawable.head2);
            return row;
        }
    }
}

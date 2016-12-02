package com.wetrack.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wetrack.R;
import com.wetrack.client.EntityCallback;
import com.wetrack.client.WeTrackClient;

import java.util.List;

public class GroupMemberAdapter extends BaseAdapter {
    private Context context;
    private List<String> users;

    public GroupMemberAdapter(Context context, List<String> users){
        this.context = context;
        this.users = users;
    }
    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.group_member, parent, false);
        String user = users.get(position);
        TextView username = (TextView) row.findViewById(R.id.tv_username);
        final ImageView portrait = (ImageView) row.findViewById(R.id.iv_avatar);

        username.setText(user);
        portrait.setImageResource(R.drawable.portrait_boy);
        WeTrackClient.singleton().getUserPortrait(user, false, new EntityCallback<Bitmap>() {
            @Override
            protected void onReceive(Bitmap value) {
                portrait.setImageBitmap(value);
            }
        });

        return row;
    }
}

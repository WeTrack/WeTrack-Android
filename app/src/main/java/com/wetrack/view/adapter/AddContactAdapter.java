package com.wetrack.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wetrack.R;

/**
 * Created by Robert on 2016/11/28.
 */

public class AddContactAdapter extends BaseAdapter {

    private Context context;
    private int[] imgs;
    private String[] texts;

    public AddContactAdapter(Context context, int[] imgs, String[] texts) {
        this.context = context;
        this.imgs = imgs;
        this.texts = texts;
    }
    @Override
    public int getCount() {
        return texts.length;
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
        View row = inflater.inflate(R.layout.add_contact, parent, false);
            TextView textView = (TextView) row.findViewById(R.id.add_contact_text);
            ImageView imageview = (ImageView) row.findViewById(R.id.add_contact_img);
            textView.setText(texts[position]);
            imageview.setBackgroundResource(imgs[position]);
            return row;
    }
}

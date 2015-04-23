package com.example.matija_pc.carewell.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.matija_pc.carewell.R;
import com.example.matija_pc.carewell.database.DatabaseTables;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Matija-PC on 17.4.2015..
 */
public class MessagesAdapter extends BaseAdapter {
    ArrayList<HashMap<String, String>> mMessages;
    Context mContext;
    Activity mActivity;

    public MessagesAdapter(Activity activity, ArrayList<HashMap<String, String>> messages) {
        mMessages = messages;
        mActivity = activity;
        mContext = activity.getApplicationContext();
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        //if this is a received message, use received message layout, else use sent message layout
        if (mMessages.get(position).get(DatabaseTables.Messages.MESSAGE_DIRECTION).equals("received"))
            view = LayoutInflater.from(mContext).inflate(R.layout.received_message_layout, parent);
        else
            view = LayoutInflater.from(mContext).inflate(R.layout.sent_message_layout, parent);

        TextView messageText = (TextView) view.findViewById(R.id.message_text);
        TextView messageDate = (TextView) view.findViewById(R.id.message_date);

        long timestamp = Long.parseLong(mMessages.get(position).get(DatabaseTables.Messages.TIMESTAMP));
        Date date = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        String stringTimestamp = dateFormat.format(date);

        messageText.setText(mMessages.get(position).get(DatabaseTables.Messages.MESSAGE_TEXT));
        messageDate.setText(stringTimestamp);

        return view;
    }
}

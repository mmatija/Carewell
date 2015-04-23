package com.example.matija_pc.carewell.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matija_pc.carewell.R;
import com.example.matija_pc.carewell.database.DatabaseOperations;
import com.example.matija_pc.carewell.database.DatabaseTables;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Matija-PC on 16.4.2015..
 */
public class ConversationsAdapter extends BaseAdapter {

    ArrayList<UserImageLoader> distinctUserImages;
    ArrayList<HashMap<String, String>> mConversations;
    Activity mActivity;
    Context mContext;

    public ConversationsAdapter (Activity activity, ArrayList<HashMap<String, String>> converations) {
        mConversations = converations;
        mActivity = activity;
        mContext = activity.getApplicationContext();
        distinctUserImages = new ArrayList<UserImageLoader>();

        String query =  "SELECT * FROM " + DatabaseTables.Contacts.TABLE_NAME + " JOIN " +
                DatabaseTables.Conversations.TABLE_NAME + " ON " +
                DatabaseTables.Conversations.USER_ID + "=" + DatabaseTables.Contacts.USER_ID;

        //load images from different users
        DatabaseOperations databaseOperations = new DatabaseOperations(mContext);
        Cursor result = databaseOperations.select(query, null);
        result.moveToFirst();
        while (!result.isAfterLast()) {
            String userID = result.getString(result.getColumnIndex(DatabaseTables.Contacts.USER_ID));
            UserImageLoader imageLoader = new UserImageLoader(userID, mContext);
            distinctUserImages.add(imageLoader);
            result.moveToNext();
        }
        result.close();
    }

    @Override
    public int getCount() {
        return mConversations.size();
    }

    @Override
    public Object getItem(int position) {
        return mConversations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = LayoutInflater.from(mContext).inflate(R.layout.message_conversation, parent);

        ImageView userImage = (ImageView) view.findViewById(R.id.user_picture_thumbnail);
        TextView userInfo = (TextView) view.findViewById(R.id.user_info);
        TextView lastMessage = (TextView) view.findViewById(R.id.last_message_text);

        String queryUserData = "SELECT " + DatabaseTables.Contacts.FIRST_NAME + " , " + DatabaseTables.Contacts.LAST_NAME +
                       " FROM " + DatabaseTables.Contacts.TABLE_NAME + " WHERE " +
                        DatabaseTables.Contacts.USER_ID + "=?";

        //get user data
        DatabaseOperations databaseOperations = new DatabaseOperations(mContext);
        Cursor result = databaseOperations.select(queryUserData, mConversations.get(position).get(DatabaseTables.Conversations.USER_ID));

        //if the user does not exist in database, set the name to "unknown"
        if (!result.moveToNext()) {
            userInfo.setText("Unknown");
        }
        else {
            String firstName = result.getString(result.getColumnIndex(DatabaseTables.Contacts.FIRST_NAME));
            String lastName = result.getString(result.getColumnIndex(DatabaseTables.Contacts.LAST_NAME));
            userInfo.setText(firstName + " " + lastName);
        }

        result.close();
        //query for getting the last message exchanged with this user
        String queryLastMessage =   "SELECT * FROM " + DatabaseTables.Messages.TABLE_NAME + " WHERE " +
                                    DatabaseTables.Messages.USER_ID + "=?" + " ORDER BY " + DatabaseTables.Messages.TIMESTAMP +
                                    " DESC LIMIT 1";

        result = databaseOperations.select(queryLastMessage, null);
        result.moveToFirst();
        String messageText = result.getString(result.getColumnIndex(DatabaseTables.Messages.MESSAGE_TEXT));
        lastMessage.setText(messageText);

        result.close();

        //set user image
        userImage.setBackgroundColor(Color.parseColor("#ff9e9e9e"));
        for (int i=0; i<distinctUserImages.size(); i++) {
            if (mConversations.get(i).get(DatabaseTables.Conversations.USER_ID).equals(distinctUserImages.get(i).userID)) {
                if (distinctUserImages.get(i).bitmap!=null) {
                    userImage.setImageBitmap(distinctUserImages.get(i).bitmap);
                    userImage.setBackgroundColor(0x0);
                }
                else {
                    userImage.setImageResource(R.drawable.generic_picture);
                }
            }
        }

        return view;
    }
}

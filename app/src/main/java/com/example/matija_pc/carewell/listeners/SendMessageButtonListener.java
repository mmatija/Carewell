package com.example.matija_pc.carewell.listeners;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;

import com.example.matija_pc.carewell.ComposeMessageActivity;
import com.example.matija_pc.carewell.MainActivity;
import com.example.matija_pc.carewell.MessagesActivity;
import com.example.matija_pc.carewell.database.DatabaseOperations;
import com.example.matija_pc.carewell.database.DatabaseTables;

/**
 * Created by Matija-PC on 13.4.2015..
 */
public class SendMessageButtonListener implements View.OnClickListener {
    Activity mActivity;
    public static final String CONVERSATION_DOESNT_EXIST = "com.example.matija_pc.carewell.CONVERSATION_DOESNT_EXIST";

    public SendMessageButtonListener(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onClick(View v) {
        //Toast.makeText(mActivity.getApplicationContext(), "Send message", Toast.LENGTH_SHORT).show();
        String userID = (String) v.getTag();
        DatabaseOperations databaseOperations = new DatabaseOperations(mActivity.getApplicationContext());
        String query = "SELECT * FROM " + DatabaseTables.Conversations.TABLE_NAME + " WHERE " + DatabaseTables.Conversations.USER_ID + "=?";
        //check if conversation with this user already exists;
        //if it does exits, start MessagesActivity, else start ComposeMessageActivity
        Cursor result = databaseOperations.select(query, userID);
        if (!result.moveToFirst()) {
            Intent intent = new Intent(mActivity, ComposeMessageActivity.class);
            intent.putExtra(MainActivity.USER_ID, userID);
            intent.setAction(CONVERSATION_DOESNT_EXIST);
            mActivity.startActivity(intent);
        }
        else {
            Intent intent = new Intent(mActivity, MessagesActivity.class);
            intent.putExtra(MainActivity.USER_ID, userID);
            mActivity.startActivity(intent);
        }
    }
}

package com.example.matija_pc.carewell.listeners;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.example.matija_pc.carewell.activities.DisplayMessagesActivity;
import com.example.matija_pc.carewell.activities.MainActivity;

/**
 * Created by Matija-PC on 13.4.2015..
 */
public class SendMessageButtonListener implements View.OnClickListener {
    Activity mActivity;
    public SendMessageButtonListener(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onClick(View v) {
        String userID = (String) v.getTag();
        Intent intent = new Intent(mActivity, DisplayMessagesActivity.class);
        intent.putExtra(MainActivity.USER_ID, userID);
        mActivity.startActivity(intent);
    }
}

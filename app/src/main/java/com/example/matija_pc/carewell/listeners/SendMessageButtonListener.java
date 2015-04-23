package com.example.matija_pc.carewell.listeners;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

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
        Toast.makeText(mActivity.getApplicationContext(), "Send message", Toast.LENGTH_SHORT).show();
    }
}

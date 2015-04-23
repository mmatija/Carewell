package com.example.matija_pc.carewell.listeners;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Matija-PC on 13.4.2015..
 */
public class AudioCallButtonListener implements View.OnClickListener {
    Context mContext;

    public AudioCallButtonListener(Context context) {
        mContext = context;
    }


    @Override
    public void onClick(View v) {
        Toast.makeText(mContext, "Audio call", Toast.LENGTH_SHORT).show();
    }
}

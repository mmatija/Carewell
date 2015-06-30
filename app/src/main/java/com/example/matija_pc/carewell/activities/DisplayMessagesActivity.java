package com.example.matija_pc.carewell.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.matija_pc.carewell.R;

/**
 * Created by Matija-PC on 2.6.2015..
 */
public class DisplayMessagesActivity extends Activity{

    private static final String MESSAGES_URL = MainActivity.SERVER_URL +"/messageMobile";
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_messages);
        userId = getIntent().getStringExtra(MainActivity.USER_ID);
        Intent intent = new Intent();
        try {
            intent=new Intent(Intent.ACTION_VIEW, Uri.parse(MESSAGES_URL + "/" + MainActivity.id + "/" + userId));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage("com.android.chrome");
            startActivity(intent);
        }catch (ActivityNotFoundException ex) {
            // Chrome browser presumably not installed so allow user to choose instead
            intent.setPackage(null);
            startActivity(intent);
        }
    }

}

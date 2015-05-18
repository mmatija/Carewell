package com.example.matija_pc.carewell;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;

import com.example.matija_pc.carewell.listeners.CallButtonListener;

/**
 * Created by Matija-PC on 18.5.2015..
 */
public class CallActivity extends Activity {
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_activity);
        String urlString="https://dams-smreki.herokuapp.com";
        Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(urlString));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.android.chrome");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            // Chrome browser presumably not installed so allow user to choose instead
            intent.setPackage(null);
            startActivity(intent);
        }
    }




    @Override
    public void onBackPressed() {
        //mark finish time and add call log to database
        long callFinish = System.currentTimeMillis();
        CallButtonListener.addCallToAdapter(callFinish);
        finish();

    }
}

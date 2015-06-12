package com.example.matija_pc.carewell.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.example.matija_pc.carewell.HttpMethods;
import com.example.matija_pc.carewell.R;
import com.example.matija_pc.carewell.listeners.CallButtonListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Matija-PC on 18.5.2015..
 */
public class CallActivity extends Activity {
    WebView webView;
    // /call/{callerId}/{calleeId}
    //vraća url;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_activity);
        userId = getIntent().getStringExtra(MainActivity.USER_ID);
        Intent intent = getIntent();
        try {
            intent=new Intent(Intent.ACTION_VIEW,Uri.parse(MainActivity.SERVER_URL + "/callMobile/" + MainActivity.id + "/" + userId));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage("com.android.chrome");
            startActivity(intent);
        }catch (ActivityNotFoundException ex) {
            // Chrome browser presumably not installed so allow user to choose instead
            intent.setPackage(null);
            startActivity(intent);
        }
        //new StartCall().execute();
    }

    private class StartCall extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            Log.i("CALL_MOBILE", MainActivity.SERVER_URL + "/callMobile/" + MainActivity.id + "/" + userId);

            JSONObject[] jsonObjects = HttpMethods.getMethod(MainActivity.SERVER_URL + "/callMobile/" + MainActivity.id + "/" + userId);
            Intent intent = null;
            try {
                String url = jsonObjects[0].getString("url");
                Log.i("CallActivity", url);
                intent=new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.android.chrome");
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
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

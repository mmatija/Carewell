package com.example.matija_pc.carewell.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.matija_pc.carewell.HttpMethods;
import com.example.matija_pc.carewell.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Matija-PC on 2.6.2015..
 */
public class DisplayMessagesActivity extends Activity{

    private static final String MESSAGES_URL = MainActivity.SERVER_URL +"/message";
    private String userId;
    WebView webView;
    // /message/{doctorId}/{patientId}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_messages);
        userId = getIntent().getStringExtra(MainActivity.USER_ID);
        webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl(url);
                return true;
            }
        });

        new GetMessagesFromServer().execute();
    }

    private class GetMessagesFromServer extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            JSONObject[] jsonObjects = HttpMethods.getMethod(MESSAGES_URL + "/" + MainActivity.id + "/" + userId );
            try {
                Log.i("DisplayMessagesActivity", jsonObjects[0].getString("urlDoctor"));
                return jsonObjects[0].getString("urlDoctor");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String url) {
            if (url == null) return;
            webView.loadUrl(url);
        }
    }
}

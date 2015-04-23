package com.example.matija_pc.carewell;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.example.matija_pc.carewell.adapters.MessagesAdapter;
import com.example.matija_pc.carewell.database.DatabaseOperations;
import com.example.matija_pc.carewell.database.DatabaseTables;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Matija-PC on 23.4.2015..
 */
public class MessagesActivity extends Activity {
    private Intent intent;
    private String userID;
    MessagesAdapter adapter;
    ArrayList<HashMap<String, String>> messages;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_activity);
        intent = getIntent();
        userID = intent.getStringExtra(MainActivity.USER_ID);

        messages = new ArrayList<>();
        ListView listView = (ListView) findViewById(R.id.messages_list);
        adapter = new MessagesAdapter(this, messages);
        listView.setAdapter(adapter);
        new GetMessagesFromDatabase().execute();

    }

    private class GetMessagesFromDatabase extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            messages.clear();
            String query =  "SELECT * FROM " + DatabaseTables.Messages.TABLE_NAME +
                            " WHERE " + DatabaseTables.Messages.USER_ID + "=?" +
                            " ORDER BY " + DatabaseTables.Messages.TIMESTAMP + " ASC";

            DatabaseOperations databaseOperations = new DatabaseOperations(getApplicationContext());
            Cursor result = databaseOperations.select(query, userID);
            result.moveToFirst();
            while (!result.isAfterLast()) {
                HashMap<String, String> message = new HashMap<>();
                message.put(DatabaseTables.Messages.USER_ID, userID);
                long timestamp = result.getLong(result.getColumnIndex(DatabaseTables.Messages.TIMESTAMP));
                message.put(DatabaseTables.Messages.TIMESTAMP, String.valueOf(timestamp));
                message.put(DatabaseTables.Messages.MESSAGE_DIRECTION, result.getString(result.getColumnIndex(DatabaseTables.Messages.MESSAGE_DIRECTION)));
                message.put(DatabaseTables.Messages.MESSAGE_TEXT, result.getString(result.getColumnIndex(DatabaseTables.Messages.MESSAGE_TEXT)));
                messages.add(message);
                result.moveToNext();
            }
            result.close();
            return null;
        }

        protected void onPostExecute(Void param) {
            adapter.notifyDataSetChanged();
        }
    }
}

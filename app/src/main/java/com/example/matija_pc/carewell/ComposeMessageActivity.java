package com.example.matija_pc.carewell;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matija_pc.carewell.database.DatabaseOperations;
import com.example.matija_pc.carewell.database.DatabaseTables;
import com.example.matija_pc.carewell.fragments.ConversationsFragment;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by Matija-PC on 19.4.2015..
 */
public class ComposeMessageActivity extends Activity {
    public static final int ACTIVITY_REQUEST_CODE = 2;
    public static final int SEARCH_RESULT = 15;
    private String userID;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compose_message_activity);
        Button sendButton = (Button) findViewById(R.id.send_message_button);
        sendButton.setOnClickListener(sendMessage);
        userID = null;
        handleIntent(getIntent());
    }

    protected void onNewIntent(Intent intent) {
        //setIntent(intent);
        super.onNewIntent(intent);
        handleIntent(intent);
    }


    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Intent searchIntent = new Intent(this, SearchableActivity.class);
            searchIntent.putExtra(SearchManager.QUERY, query);
            startActivityForResult(searchIntent, ACTIVITY_REQUEST_CODE);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_compose_message_activity, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setIconifiedByDefault(false);
        getActionBar().setTitle("Compose");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        //list all contacts - if a query is empty, sql will match all users
        if (id==R.id.list_all_contacts) {
            String query = "";
            Intent searchIntent = new Intent(this, SearchableActivity.class);
            searchIntent.putExtra(SearchManager.QUERY, query);
            startActivityForResult(searchIntent, ACTIVITY_REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_REQUEST_CODE && resultCode==SEARCH_RESULT) {
            String ID = data.getStringExtra(MainActivity.USER_ID);
            String firstName = data.getStringExtra(MainActivity.FIRST_NAME);
            String lastName = data.getStringExtra(MainActivity.LAST_NAME);
            TextView userInfo = (TextView) findViewById(R.id.message_recipient);
            userInfo.setText(getResources().getString(R.string.recipient) + " " + firstName + " " + lastName);
            userID = ID;
        }
    }

    private View.OnClickListener sendMessage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (userID == null) {
                Toast.makeText(getApplicationContext(), "Message recipient is not chosen", Toast.LENGTH_SHORT).show();
            }
            else {
                String messageText;
                EditText editText = (EditText) findViewById(R.id.message_text);
                messageText = editText.getText().toString();
                //if message body is empty
                if (messageText.equals("")) {
                    Toast.makeText(getApplicationContext(), "Message body is empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    DatabaseOperations databaseOperations = new DatabaseOperations(getApplicationContext());
                    ContentValues values = new ContentValues();
                    Date currentDate = new Date();
                    long timestamp = currentDate.getTime();
                    String messageDirection = "sent";
                    values.put(DatabaseTables.Messages.USER_ID, userID);
                    values.put(DatabaseTables.Messages.TIMESTAMP, timestamp);
                    values.put(DatabaseTables.Messages.MESSAGE_DIRECTION, messageDirection);
                    values.put(DatabaseTables.Messages.MESSAGE_TEXT, messageText);

                    //store the message into database
                    databaseOperations.insert(DatabaseTables.Messages.TABLE_NAME, values);
                    Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_SHORT).show();

                    String query = "SELECT " + DatabaseTables.Conversations.USER_ID + " FROM " +
                                    DatabaseTables.Conversations.TABLE_NAME + " WHERE " +
                                    DatabaseTables.Conversations.USER_ID + "=?";

                    Cursor result = databaseOperations.select(query, userID);
                    //if the conversation with this user doesn't exist, create it
                    if (!result.moveToFirst()) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DatabaseTables.Conversations.USER_ID, userID);
                        databaseOperations.insert(DatabaseTables.Conversations.TABLE_NAME, contentValues);
                        HashMap<String, String> conversation = new HashMap<>();
                        conversation.put(DatabaseTables.Conversations.USER_ID, userID);
                        ConversationsFragment.conversations.add(conversation);
                    }
                    ConversationsFragment.adapter.notifyDataSetChanged();
                    //start new activity in which all messages exchanged with this user are shown
                    Intent intent = new Intent(getApplicationContext(), MessagesActivity.class);
                    intent.putExtra(MainActivity.USER_ID, userID);
                    startActivity(intent);
                    finish();
                }
            }

        }
    };

}

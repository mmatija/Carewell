package com.example.matija_pc.carewell.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.matija_pc.carewell.R;
import com.example.matija_pc.carewell.adapters.MessagesAdapter;
import com.example.matija_pc.carewell.database.DatabaseOperations;
import com.example.matija_pc.carewell.database.DatabaseTables;
import com.example.matija_pc.carewell.fragments.ConversationsFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Matija-PC on 23.4.2015..
 */
public class MessagesActivity extends Activity {
    private Intent intent;
    private String userID;
    MessagesAdapter adapter;
    ArrayList<HashMap<String, String>> messages;
    EditText editText;
    ListView listView;
    boolean userExists = false;
    private final int MESSAGES_CONTEXT_MENU_GROUP = 3;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_activity);
        //hide soft keyboard when this activity launches
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //setup UI so that edit text loses focus whenever a user clicks outside of it
        setupUI(findViewById(R.id.parent));

        intent = getIntent();
        userID = intent.getStringExtra(MainActivity.USER_ID);

        //get user first and last name and display it on action bar
        DatabaseOperations databaseOperations = new DatabaseOperations(getApplicationContext());
        Cursor cursor = databaseOperations.select("SELECT * FROM " + DatabaseTables.Contacts.TABLE_NAME + " WHERE " + DatabaseTables.Contacts.USER_ID + "=?", userID);
        String firstName = "";
        String lastName = "";
        listView = (ListView) findViewById(R.id.messages_list);
        editText = (EditText) findViewById(R.id.message_text);

        if(cursor.moveToFirst()) {
            firstName = cursor.getString(cursor.getColumnIndex(DatabaseTables.Contacts.FIRST_NAME));
            lastName = cursor.getString(cursor.getColumnIndex(DatabaseTables.Contacts.LAST_NAME));
            userExists = true;
            Button button = (Button) findViewById(R.id.send_message_button);
            button.setOnClickListener(sendMessageButtonListener);
        }
        else {
            firstName = "Unknown";
            editText.setFocusable(false);
            editText.setHint("Cannot send message to this user");
        }
        cursor.close();

        getActionBar().setTitle(firstName + " " + lastName);

        messages = new ArrayList<>();
        adapter = new MessagesAdapter(this, messages);
        listView.setAdapter(adapter);
        //scrollMyListViewToBottom();
        getMessages();
        //scroll to the last message
        listView.setSelection(adapter.getCount() - 1);
        listView.setLongClickable(true);
        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(MESSAGES_CONTEXT_MENU_GROUP, v.getId(), 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() != MESSAGES_CONTEXT_MENU_GROUP) return  super.onContextItemSelected(item);
        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (item.getTitle().equals("Delete")) {
            String timestamp = messages.get(adapterContextMenuInfo.position).get(DatabaseTables.Messages.TIMESTAMP);
            String messageDirection = messages.get(adapterContextMenuInfo.position).get(DatabaseTables.Messages.MESSAGE_DIRECTION);
            new DeleteSingleMessage(timestamp, messageDirection).execute();
            return true;
        }
        return super.onContextItemSelected(item);
    }


    private void getMessages() {
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
        adapter.notifyDataSetChanged();
    }


    private class DeleteSingleMessage extends AsyncTask<Void, Void, Void> {

        String mTimestamp;
        String mMessageDirection;

        public DeleteSingleMessage(String timestamp, String messageDirection) {
            mTimestamp = timestamp;
            mMessageDirection = messageDirection;
        }

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseOperations databaseOperations = new DatabaseOperations(getApplicationContext());
            String whereClause = DatabaseTables.Messages.USER_ID + "=?" + " AND " +
                    DatabaseTables.Messages.TIMESTAMP + "=?" + " AND " +
                    DatabaseTables.Messages.MESSAGE_DIRECTION + "=?";

            databaseOperations.delete(DatabaseTables.Messages.TABLE_NAME, whereClause, userID, mTimestamp, mMessageDirection);

            for (int i = 0; i < messages.size(); i++) {
                HashMap<String, String> temp = messages.get(i);
                if (temp.get(DatabaseTables.Messages.USER_ID).equals(userID) && temp.get(DatabaseTables.Messages.TIMESTAMP).equals(mTimestamp) && temp.get(DatabaseTables.Messages.MESSAGE_DIRECTION).equals(mMessageDirection)) {
                    messages.remove(i);
                    break;
                }
            }

            //check if there are any messages exchanged with this user left, and delete entire conversation if there aren't
            String query = "SELECT COUNT(" + DatabaseTables.Messages.USER_ID + ")" + " FROM " +
                    DatabaseTables.Messages.TABLE_NAME + " WHERE " + DatabaseTables.Messages.USER_ID + "=?";
            Cursor result = databaseOperations.select(query, userID);
            result.moveToFirst();
            if (result.getInt(0) == 0) {
                //no more messages with this user, delete conversation
                databaseOperations.delete(DatabaseTables.Conversations.TABLE_NAME, DatabaseTables.Conversations.USER_ID + "=?", userID);
                for (int i = 0; i < ConversationsFragment.conversations.size(); i++) {
                    HashMap<String, String> temp = ConversationsFragment.conversations.get(i);
                    if (temp.get(DatabaseTables.Conversations.USER_ID).equals(userID)) {
                        ConversationsFragment.conversations.remove(i);
                        break;
                    }
                }
            }
            result.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.notifyDataSetChanged();
            ConversationsFragment.adapter.notifyDataSetChanged();
        }
    }

    View.OnClickListener sendMessageButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String messageText = editText.getText().toString();
            if (messageText.equals("")) {
                Toast.makeText(getApplicationContext(), "Message body is empty", Toast.LENGTH_SHORT).show();
            }
            else {
                //add new message to database
                DatabaseOperations databaseOperations = new DatabaseOperations(getApplicationContext());
                Date date = new Date();
                long timestamp = date.getTime();
                String messageDirection = "sent";
                ContentValues contentValues = new ContentValues();
                contentValues.put(DatabaseTables.Messages.USER_ID, userID);
                contentValues.put(DatabaseTables.Messages.TIMESTAMP, timestamp);
                contentValues.put(DatabaseTables.Messages.MESSAGE_DIRECTION, messageDirection);
                contentValues.put(DatabaseTables.Messages.MESSAGE_TEXT, messageText);
                databaseOperations.insert(DatabaseTables.Messages.TABLE_NAME, contentValues);

                //update timestamp of last message in Conversations table
                contentValues = new ContentValues();
                contentValues.put(DatabaseTables.Conversations.LAST_MESSAGE_TIMESTAMP, timestamp);
                databaseOperations.update(DatabaseTables.Conversations.TABLE_NAME, contentValues, DatabaseTables.Conversations.USER_ID, userID);

                //add new message to list and refresh the adapter
                HashMap<String, String> newMessage = new HashMap<>();
                newMessage.put(DatabaseTables.Messages.USER_ID, userID);
                newMessage.put(DatabaseTables.Messages.TIMESTAMP, String.valueOf(timestamp));
                newMessage.put(DatabaseTables.Messages.MESSAGE_DIRECTION, messageDirection);
                newMessage.put(DatabaseTables.Messages.MESSAGE_TEXT, messageText);
                messages.add(newMessage);
                adapter.notifyDataSetChanged();
                if (ConversationsFragment.adapter != null) {

                    //if the conversation doesn't exist, create it
                    String query = "SELECT * FROM " + DatabaseTables.Conversations.TABLE_NAME + " WHERE " +
                                    DatabaseTables.Conversations.USER_ID + "=?";
                    Cursor result = databaseOperations.select(query, userID);
                    if (!result.moveToFirst()) {
                        ContentValues cv = new ContentValues();
                        cv.put(DatabaseTables.Conversations.USER_ID, userID);
                        cv.put(DatabaseTables.Conversations.LAST_MESSAGE_TIMESTAMP, String.valueOf(timestamp));
                        databaseOperations.insert(DatabaseTables.Conversations.TABLE_NAME, cv);
                        HashMap<String, String> temp = new HashMap<>();
                        temp.put(DatabaseTables.Conversations.USER_ID, userID);
                        temp.put(DatabaseTables.Conversations.LAST_MESSAGE_TIMESTAMP, String.valueOf(timestamp));
                        ConversationsFragment.conversations.add(temp);
                        //ConversationsFragment.adapter.notifyDataSetChanged();
                    }
                    else result.close();

                    //move this conversation to the top
                    for (int i=0; i<ConversationsFragment.conversations.size(); i++) {
                        if (ConversationsFragment.conversations.get(i).get(DatabaseTables.Conversations.USER_ID).equals(userID)) {
                            HashMap<String, String> tempConversation = ConversationsFragment.conversations.get(i);
                            //update the timestamp
                            //tempConversation.put(DatabaseTables.Messages.TIMESTAMP, String.valueOf(timestamp));
                            ConversationsFragment.conversations.remove(i);
                            ConversationsFragment.conversations.add(0, tempConversation);
                            break;
                        }
                    }

                    ConversationsFragment.adapter.notifyDataSetChanged();
                }
                editText.setText("");
                editText.clearFocus();
                //scrollMyListViewToBottom();
                listView.setSelection(adapter.getCount() - 1);
                Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_SHORT).show();
            }

        }
    };

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(MessagesActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    private void scrollMyListViewToBottom() {
        listView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listView.setSelection(adapter.getCount() - 1);
            }
        });
    }
}

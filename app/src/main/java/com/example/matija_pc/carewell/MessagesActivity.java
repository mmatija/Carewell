package com.example.matija_pc.carewell;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
        //editText.setOnFocusChangeListener(onFocusChangeListener);
        adapter = new MessagesAdapter(this, messages);
        listView.setAdapter(adapter);
        //new GetMessagesFromDatabase().execute();
        //scrollMyListViewToBottom();
        getMessages();
        //scroll to the last message
        listView.setSelection(adapter.getCount() - 1);
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
                String messageDirection = getResources().getString(R.string.sent);
                ContentValues contentValues = new ContentValues();
                contentValues.put(DatabaseTables.Messages.USER_ID, userID);
                contentValues.put(DatabaseTables.Messages.TIMESTAMP, timestamp);
                contentValues.put(DatabaseTables.Messages.MESSAGE_DIRECTION, messageDirection);
                contentValues.put(DatabaseTables.Messages.MESSAGE_TEXT, messageText);
                databaseOperations.insert(DatabaseTables.Messages.TABLE_NAME, contentValues);

                //add new message to list and refresh the adapter
                HashMap<String, String> newMessage = new HashMap<>();
                newMessage.put(DatabaseTables.Messages.USER_ID, userID);
                newMessage.put(DatabaseTables.Messages.TIMESTAMP, String.valueOf(timestamp));
                newMessage.put(DatabaseTables.Messages.MESSAGE_DIRECTION, messageDirection);
                newMessage.put(DatabaseTables.Messages.MESSAGE_TEXT, messageText);
                messages.add(newMessage);
                adapter.notifyDataSetChanged();
                if (ConversationsFragment.adapter != null)
                    ConversationsFragment.adapter.notifyDataSetChanged();
                editText.setText("");
                editText.clearFocus();
                //scrollMyListViewToBottom();
                listView.setSelection(adapter.getCount() - 1);

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

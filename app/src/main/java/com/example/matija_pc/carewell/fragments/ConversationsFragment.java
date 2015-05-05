package com.example.matija_pc.carewell.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.matija_pc.carewell.ComposeMessageActivity;
import com.example.matija_pc.carewell.MainActivity;
import com.example.matija_pc.carewell.MessagesActivity;
import com.example.matija_pc.carewell.R;
import com.example.matija_pc.carewell.adapters.ConversationsAdapter;
import com.example.matija_pc.carewell.database.DatabaseOperations;
import com.example.matija_pc.carewell.database.DatabaseTables;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Matija-PC on 16.4.2015..
 */
public class ConversationsFragment extends Fragment {

    private static final int CONVERSATIONS_CONTEX_MENU_GROUP = 2;
    ListView listView;
    public static ArrayList<HashMap<String, String>> conversations;
    public static ConversationsAdapter adapter;

    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.conversations_fragment, container, false);
        listView = (ListView) view.findViewById(R.id.conversations_list);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        conversations = new ArrayList<HashMap<String, String>>();
        adapter = new ConversationsAdapter(getActivity(), conversations);
        listView.setAdapter(adapter);
        listView.setItemsCanFocus(true);
        listView.setOnItemClickListener(onItemClickListener);
        registerForContextMenu(listView);
        new GetConversationsFromDatabase().execute();
    }

    public void onCreateContextMenu (ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        //super.onCreateContextMenu(menu, view, menuInfo);
        menu.add(CONVERSATIONS_CONTEX_MENU_GROUP, view.getId(), 0, "Delete");
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        if (menuItem.getGroupId() != CONVERSATIONS_CONTEX_MENU_GROUP) return super.onContextItemSelected(menuItem);
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
        final String userID = conversations.get(menuInfo.position).get(DatabaseTables.Conversations.USER_ID);

        if (menuItem.getTitle().toString().equals("Delete")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Delete conversation?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    deleteConversations(DatabaseTables.Conversations.USER_ID, userID);
                }
            });
            builder.setNegativeButton("No", null);
            builder.show();

            return true;
        }

        return super.onContextItemSelected(menuItem);
    }

    private class GetConversationsFromDatabase extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseOperations databaseOperations = new DatabaseOperations(getActivity().getApplicationContext());
            Cursor result = databaseOperations.select("SELECT * FROM " + DatabaseTables.Conversations.TABLE_NAME, null);
            result.moveToFirst();
            while (!result.isAfterLast()) {
                HashMap<String, String> conversation = new HashMap<>();
                conversation.put(DatabaseTables.Conversations.USER_ID, result.getString(result.getColumnIndex(DatabaseTables.Conversations.USER_ID)));
                conversations.add(conversation);
                result.moveToNext();
            }
            result.close();
            return null;
        }

        protected void onPostExecute(Void param) {
            adapter.notifyDataSetChanged();
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.conversations_fragment_menu, menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.start_new_conversation:
                //start a new activity
                Intent intent = new Intent(getActivity(), ComposeMessageActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_delete_all:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Delete all conversations?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteConversations(null, null);

                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    public void deleteConversations(String where, String value) {
        DatabaseOperations databaseOperations = new DatabaseOperations(getActivity().getApplicationContext());
        databaseOperations.delete(DatabaseTables.Conversations.TABLE_NAME, where, value);
        databaseOperations.delete(DatabaseTables.Messages.TABLE_NAME, where, value);
        if (where == null && value == null) {
            conversations.clear();
        }
        else {
            for(int i=0; i<conversations.size(); i++) {
                if (conversations.get(i).get(DatabaseTables.Conversations.USER_ID).equals(value)) {
                    conversations.remove(i);
                    break;
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), MessagesActivity.class);
            intent.putExtra(MainActivity.USER_ID, conversations.get(position).get(DatabaseTables.Conversations.USER_ID));
            startActivity(intent);
        }
    };

}

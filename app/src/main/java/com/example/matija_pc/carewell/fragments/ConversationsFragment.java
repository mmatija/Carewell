package com.example.matija_pc.carewell.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.matija_pc.carewell.ComposeMessageActivity;
import com.example.matija_pc.carewell.R;
import com.example.matija_pc.carewell.SearchableActivity;
import com.example.matija_pc.carewell.adapters.ConversationsAdapter;
import com.example.matija_pc.carewell.database.DatabaseOperations;
import com.example.matija_pc.carewell.database.DatabaseTables;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Matija-PC on 16.4.2015..
 */
public class ConversationsFragment extends Fragment {

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
        new GetConversationsFromDatabase().execute();
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
        }

        return super.onOptionsItemSelected(menuItem);
    }


}

package com.example.matija_pc.carewell;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.matija_pc.carewell.adapters.ContactsAdapter;
import com.example.matija_pc.carewell.adapters.SearchResultAdapter;
import com.example.matija_pc.carewell.database.DatabaseOperations;
import com.example.matija_pc.carewell.database.DatabaseTables;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Matija-PC on 19.4.2015..
 */
public class SearchableActivity extends Activity {
    ArrayList<HashMap<String, String>> contacts;
    SearchResultAdapter adapter;

    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchable_activity);
        contacts = new ArrayList<>();
        adapter = new SearchResultAdapter(this, contacts);
        ListView listView = (ListView) findViewById(R.id.search_result_list);
        listView.setAdapter(adapter);


        //get intent
        Intent intent = getIntent();
        if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //do search
            new GetSearchResult().execute(query);
        }
    }

    private class GetSearchResult extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String query = "SELECT * FROM " + DatabaseTables.Contacts.TABLE_NAME +
                            " WHERE " + DatabaseTables.Contacts.FIRST_NAME + " LIKE " + "'%" +
                            params[0] + "%'" + " OR " + DatabaseTables.Contacts.LAST_NAME + " LIKE " + "'%" +
                            params[0] + "%'";

            DatabaseOperations databaseOperations = new DatabaseOperations(getApplicationContext());
            Cursor result = databaseOperations.select(query, null);
            result.moveToFirst();
            while (!result.isAfterLast()) {
                HashMap<String, String> contact = new HashMap<>();
                contact.put(DatabaseTables.Contacts._ID, result.getString(result.getColumnIndex(DatabaseTables.Contacts._ID)));
                contact.put(DatabaseTables.Contacts.USER_ID, result.getString(result.getColumnIndex(DatabaseTables.Contacts.USER_ID)));
                contact.put(DatabaseTables.Contacts.FIRST_NAME, result.getString(result.getColumnIndex(DatabaseTables.Contacts.FIRST_NAME)));
                contact.put(DatabaseTables.Contacts.LAST_NAME, result.getString(result.getColumnIndex(DatabaseTables.Contacts.LAST_NAME)));
                contact.put(DatabaseTables.Contacts.IMAGE_PATH, result.getString(result.getColumnIndex(DatabaseTables.Contacts.IMAGE_PATH)));
                contacts.add(contact);
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

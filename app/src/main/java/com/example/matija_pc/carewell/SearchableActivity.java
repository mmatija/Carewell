package com.example.matija_pc.carewell;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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
    Intent returnIntent;

    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchable_activity);
        contacts = new ArrayList<>();
        adapter = new SearchResultAdapter(this, contacts);
        ListView listView = (ListView) findViewById(R.id.search_result_list);
        listView.setAdapter(adapter);
        returnIntent = new Intent();


        //get intent
        Intent intent = getIntent();
        String query = intent.getStringExtra(SearchManager.QUERY);
        if (query.equals("")) getActionBar().setTitle("All contacts");
        else getActionBar().setTitle("Search results");
        //do search
        new GetSearchResult().execute(query);
        listView.setOnItemClickListener(onItemClickListener);
    }

    private class GetSearchResult extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            contacts.clear();
            String query = "SELECT * FROM " + DatabaseTables.Contacts.TABLE_NAME +
                            " WHERE " + DatabaseTables.Contacts.FIRST_NAME + " LIKE " + "'%" +
                            params[0] + "%'" + " OR " + DatabaseTables.Contacts.LAST_NAME + " LIKE " + "'%" +
                            params[0] + "%'" + " OR " + DatabaseTables.Contacts.FIRST_NAME + " || " + "' '" + " || " + DatabaseTables.Contacts.LAST_NAME +
                            " LIKE " + "'%" + params[0] + "%'";

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


    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String userID = contacts.get(position).get(DatabaseTables.Contacts.USER_ID);
            String firstName = contacts.get(position).get(DatabaseTables.Contacts.FIRST_NAME);
            String lastName = contacts.get(position).get(DatabaseTables.Contacts.LAST_NAME);
            Log.d("SearchableActivity", userID);
            Log.d("SearchableActivity", firstName);
            Log.d("SearchableActivity", lastName);
            returnIntent.putExtra(MainActivity.USER_ID, userID);
            returnIntent.putExtra(MainActivity.FIRST_NAME, firstName);
            returnIntent.putExtra(MainActivity.LAST_NAME, lastName);
            setResult(ComposeMessageActivity.SEARCH_RESULT, returnIntent);
            finish();
        }
    };

}

package com.example.matija_pc.carewell.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.matija_pc.carewell.JsonParser;
import com.example.matija_pc.carewell.R;
import com.example.matija_pc.carewell.adapters.ContactsAdapter;
import com.example.matija_pc.carewell.adapters.ContactsHolderClass;
import com.example.matija_pc.carewell.database.DatabaseHelper;
import com.example.matija_pc.carewell.database.DatabaseOperations;
import com.example.matija_pc.carewell.database.DatabaseTables;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Matija-PC on 11.4.2015..
 */
public class ContactsFragment extends Fragment {

    private static final int CONTACTS_CONTEXT_MENU_GROUP = 0;
    public static final String FIRST_NAME = "com.example.matija_pc.carewell.FIRST_NAME";
    public static final String USER_ID = "com.example.matija_pc.carewell.USER_ID";
    public static final String LAST_NAME = "com.example.matija_pc.carewell.LAST_NAME";
    public static ArrayList<HashMap<String, String>> contacts;
    public static ContactsAdapter adapter;
    ListView listView;
    GetContactsFromDatabase getContactsFromDatabase;
    public static int id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        contacts = new ArrayList<HashMap<String, String>>();
        adapter = new ContactsAdapter(getActivity(), contacts);
        listView.setAdapter(adapter);
        listView.setItemsCanFocus(true);
        //insertTestData();
        registerForContextMenu(listView);
        getContactsFromDatabase = new GetContactsFromDatabase();
        getContactsFromDatabase.execute();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contacts_fragment, container, false);
        listView = (ListView) view.findViewById(R.id.user_list);
        return view;
    }


    public class GetContactsFromDatabase extends AsyncTask<Void, Void, Void> {
        ArrayList<HashMap<String, String>> tempContacts = new ArrayList<>();

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper helper = new DatabaseHelper(getActivity().getApplicationContext());
            SQLiteDatabase database = helper.getReadableDatabase();
            Cursor cursor = database.rawQuery("select * from " + DatabaseTables.Contacts.TABLE_NAME, null);
            cursor.moveToFirst();
            //ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();

            //read all rows
            while (!cursor.isAfterLast()) {
                HashMap<String, String> contact = new HashMap<String, String>();
                contact.put(DatabaseTables.Contacts._ID, cursor.getString(cursor.getColumnIndex(DatabaseTables.Contacts._ID)));
                contact.put(DatabaseTables.Contacts.USER_ID, cursor.getString(cursor.getColumnIndex(DatabaseTables.Contacts.USER_ID)));
                contact.put(DatabaseTables.Contacts.FIRST_NAME, cursor.getString(cursor.getColumnIndex(DatabaseTables.Contacts.FIRST_NAME)));
                contact.put(DatabaseTables.Contacts.LAST_NAME, cursor.getString(cursor.getColumnIndex(DatabaseTables.Contacts.LAST_NAME)));
                contact.put(DatabaseTables.Contacts.IMAGE_PATH, cursor.getString(cursor.getColumnIndex(DatabaseTables.Contacts.IMAGE_PATH)));
                contacts.add(contact);
                //tempContacts.add(contact);
                cursor.moveToNext();
            }
            cursor.close();

            return null;
        }

        protected void onPostExecute(Void v) {
            //contacts = tempContacts;
            adapter.notifyDataSetChanged();
        }
    }


    public void deleteContact(String userID) {
        DatabaseOperations databaseOperations = new DatabaseOperations(getActivity().getApplicationContext());
        databaseOperations.delete(DatabaseTables.Contacts.TABLE_NAME, DatabaseTables.Contacts.USER_ID + "=?", userID);
        for (int i = 0; i < contacts.size(); i++) {
            HashMap<String, String> temp = contacts.get(i);
            if (temp.get(DatabaseTables.Contacts.USER_ID).equals(userID)) {
                contacts.remove(i);
                break;
            }
        }
        adapter.notifyDataSetChanged();
        CallsFragment.callsAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity().getApplicationContext(), "Contact deleted", Toast.LENGTH_SHORT).show();
    }

    public void updateContactInfo(String newFirstName, String newLastName, String userID) {

        //write changes to database
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseTables.Contacts.FIRST_NAME, newFirstName);
        contentValues.put(DatabaseTables.Contacts.LAST_NAME, newLastName);
        DatabaseOperations databaseOperations = new DatabaseOperations(getActivity().getApplicationContext());
        databaseOperations.update(DatabaseTables.Contacts.TABLE_NAME, contentValues, DatabaseTables.Contacts.USER_ID, userID);

        for (int i = 0; i < contacts.size(); i++) {
            HashMap<String, String> temp = new HashMap<String, String>();
            temp = contacts.get(i);
            if (temp.get(DatabaseTables.Contacts.USER_ID).equals(userID)) {
                temp.put(DatabaseTables.Contacts.FIRST_NAME, newFirstName);
                temp.put(DatabaseTables.Contacts.LAST_NAME, newLastName);
                contacts.set(i, temp);
            }
        }
        adapter.notifyDataSetChanged();
        Toast.makeText(getActivity().getApplicationContext(), "Contact updated", Toast.LENGTH_SHORT).show();
    }


    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        //super.onCreateContextMenu(menu, view, menuInfo);
        menu.add(CONTACTS_CONTEXT_MENU_GROUP, view.getId(), 0, "Edit");
        menu.add(CONTACTS_CONTEXT_MENU_GROUP, view.getId(), 0, "Delete");
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        if (menuItem.getGroupId() != CONTACTS_CONTEXT_MENU_GROUP)
            return super.onContextItemSelected(menuItem);
        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
        final String userID = contacts.get(adapterContextMenuInfo.position).get(DatabaseTables.Contacts.USER_ID);

        //user has selected Edit action
        if (menuItem.getTitle().toString().equals("Edit")) {
            View view = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.edit_user_data, null);
            final EditText firstName = (EditText) view.findViewById(R.id.edit_first_name);
            final EditText lastName = (EditText) view.findViewById(R.id.edit_last_name);
            final String tempFirstName = contacts.get(adapterContextMenuInfo.position).get(DatabaseTables.Contacts.FIRST_NAME);
            final String tempLastName = contacts.get(adapterContextMenuInfo.position).get(DatabaseTables.Contacts.LAST_NAME);

            firstName.setText(tempFirstName);
            lastName.setText(tempLastName);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setView(view);
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if any of the fields has changed
                    if (!firstName.getText().toString().equals(tempFirstName) || !lastName.getText().toString().equals(tempLastName)) {
                        //if both fields are empty
                        if (firstName.getText().toString().equals("") && lastName.getText().toString().equals(""))
                            Toast.makeText(getActivity().getApplicationContext(), "Both fields cannot be left empty", Toast.LENGTH_SHORT).show();
                        else
                            updateContactInfo(firstName.getText().toString(), lastName.getText().toString(), userID);
                    }
                }
            })
                    .setNegativeButton("Cancel", null)
                    .show();

            return true;
        }

        if (menuItem.getTitle().toString().equals("Delete")) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("Delete contact?");
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteContact(userID);
                }
            })
                    .setNegativeButton("No", null)
                    .show();

            return true;
        }

        return super.onContextItemSelected(menuItem);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.contacts_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_new_contact:
                //add code
                new GetContactsFromServer().execute("http://www.omdbapi.com/?t=Batman+begins&y=2005&plot=short&r=json");
                // /id JSON post username:pacijent1, password:sifra1
                //new GetContactsFromServer().execute("http://10.129.44.123:8080/patient/1/data");
                //new GetIdFromServer().execute("http://10.129.44.123:8080/id");
                //new GetContactsFromServer().execute("http://10.129.44.123:8080");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class GetContactsFromServer extends AsyncTask<String, String, Void> {
        private ProgressDialog progressDialog = new ProgressDialog(getActivity());
        InputStream inputStream = null;
        String result = "";
        JSONObject[] listOfJsonObjects = null;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Downloading contacts...");
            progressDialog.show();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    GetContactsFromServer.this.cancel(true);
                }
            });
        }

        @Override
        protected Void doInBackground(String... params) {

//            String idUrl = params[0] + "/id";
//            String contactsUrl = params[0] + "/familymember/10/patients";
//            id = getIdFromServer(idUrl);
            String testUrl = params[0];

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet();
//                httpGet.setURI(URI.create(contactsUrl));
                httpGet.setURI(URI.create(testUrl));
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                inputStream = httpEntity.getContent();
                listOfJsonObjects = JsonParser.parseJson(inputStream);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (listOfJsonObjects == null) return;
            new AddContactsToDatabase().execute(listOfJsonObjects);
            progressDialog.dismiss();
        }

        public int getIdFromServer(String url) {
            SharedPreferences preferences = getActivity().getSharedPreferences("com.example.matija_pc.carewell", Context.MODE_PRIVATE);
            int id = preferences.getInt(USER_ID, -1);
            if (id != -1) return id;

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("username", "pacijent2"));
                nameValuePairs.add(new BasicNameValuePair("password", "sifra2"));
                //                nameValuePairs.add(new BacicNameValuePair("password", "sifra2"));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();


                InputStream inputStream = httpEntity.getContent();
                JSONObject[] listOfJsonObjects = JsonParser.parseJson(inputStream);
                id = listOfJsonObjects[0].getInt("id");
                preferences.edit().putInt(USER_ID, id).apply();

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return id;
        }


        class AddContactsToDatabase extends AsyncTask<JSONObject, Void, Void> {

            @Override
            protected Void doInBackground(JSONObject... params) {
                for (JSONObject jsonObject : params) {
                    try {
                        String title = jsonObject.getString("Title");
                        String year = jsonObject.getString("Year");
                        Log.i("Title", title);
                        Log.i("Year", year);
                        ContactsHolderClass.ContactsHolder contact = new ContactsHolderClass.ContactsHolder();
                        contact.firstName = "";
                        contact.userID = year;
                        contact.lastName = "";
                        contact.imagePath = "";
                        addContact(contact);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                adapter.notifyDataSetChanged();
                if (CallsFragment.callsAdapter != null)
                    CallsFragment.callsAdapter.notifyDataSetChanged();
                if (ConversationsFragment.adapter != null)
                    ConversationsFragment.adapter.notifyDataSetChanged();
            }
        }

        public void addContact(ContactsHolderClass.ContactsHolder contact) {
            //add new contact to database
            DatabaseOperations databaseOperations = new DatabaseOperations(getActivity().getApplicationContext());
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseTables.Contacts.USER_ID, contact.userID);
            contentValues.put(DatabaseTables.Contacts.FIRST_NAME, contact.firstName);
            contentValues.put(DatabaseTables.Contacts.LAST_NAME, contact.lastName);
            contentValues.put(DatabaseTables.Contacts.IMAGE_PATH, "");
            long id = databaseOperations.insert(DatabaseTables.Contacts.TABLE_NAME, contentValues);
            if (id == -1) return; //error occurred
            //add new contact to adapter
            HashMap<String, String> newContact = new HashMap<>();
            newContact.put(DatabaseTables.Contacts._ID, String.valueOf(id));
            newContact.put(DatabaseTables.Contacts.USER_ID, contact.userID);
            newContact.put(DatabaseTables.Contacts.FIRST_NAME, contact.firstName);
            newContact.put(DatabaseTables.Contacts.LAST_NAME, contact.lastName);
            newContact.put(DatabaseTables.Contacts.IMAGE_PATH, "");
            contacts.add(newContact);
        }
    }
}

package com.example.matija_pc.carewell.fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.matija_pc.carewell.R;
import com.example.matija_pc.carewell.adapters.ContactsAdapter;
import com.example.matija_pc.carewell.database.DatabaseHelper;
import com.example.matija_pc.carewell.database.DatabaseOperations;
import com.example.matija_pc.carewell.database.DatabaseTables;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Matija-PC on 11.4.2015..
 */
public class ContactsFragment extends Fragment {

    public static final String FIRST_NAME = "com.example.matija_pc.carewell.FIRST_NAME";
    public static final String USER_ID = "com.example.matija_pc.carewell.USER_ID";
    public static final String LAST_NAME = "com.example.matija_pc.carewell.LAST_NAME";
    public static final String IMAGE_PATH = "com.example.matija_pc.carewell.IMAGE_PATH";
    public static final int DELETE = 10;
    public static final int UPDATE = 11;
    public static ArrayList<HashMap<String, String>> contacts;
    public static ContactsAdapter adapter;
    ListView listView;



    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        contacts = new ArrayList<HashMap<String, String>>();
        adapter = new ContactsAdapter(getActivity(), contacts);
        listView.setAdapter(adapter);
        listView.setItemsCanFocus(true);
        //insertTestData();
        registerForContextMenu(listView);
        new GetContactsFromDatabase().execute();

    }

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Log.i("Contacts fragment", "onCreateView called");
        //super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.contacts_fragment, container, false);
        listView = (ListView) view.findViewById(R.id.user_list);
        return view;
    }

    public void insertTestData() {

        ContentValues person1 = new ContentValues();
        ContentValues person2 = new ContentValues();
        ContentValues person3 = new ContentValues();
        ContentValues person4 = new ContentValues();

        person1.put(DatabaseTables.Contacts.USER_ID, "mm");
        person1.put(DatabaseTables.Contacts.FIRST_NAME, "Matija");
        person1.put(DatabaseTables.Contacts.LAST_NAME, "Močilac");
        person1.put(DatabaseTables.Contacts.IMAGE_PATH, "");


        person2.put(DatabaseTables.Contacts.USER_ID, "jm");
        person2.put(DatabaseTables.Contacts.FIRST_NAME, "Juraj");
        person2.put(DatabaseTables.Contacts.LAST_NAME, "Močilac");
        person2.put(DatabaseTables.Contacts.IMAGE_PATH, "");

        person3.put(DatabaseTables.Contacts.USER_ID, "tm");
        person3.put(DatabaseTables.Contacts.FIRST_NAME, "Tereza");
        person3.put(DatabaseTables.Contacts.LAST_NAME, "Močilac");
        person3.put(DatabaseTables.Contacts.IMAGE_PATH, "");

        person4.put(DatabaseTables.Contacts.USER_ID, "zm");
        person4.put(DatabaseTables.Contacts.FIRST_NAME, "Zvonimir");
        person4.put(DatabaseTables.Contacts.LAST_NAME, "Močilac");
        person4.put(DatabaseTables.Contacts.IMAGE_PATH, "");

        DatabaseOperations databaseOperations = new DatabaseOperations(getActivity().getApplicationContext());
        databaseOperations.insert(DatabaseTables.Contacts.TABLE_NAME, person1);
        databaseOperations.insert(DatabaseTables.Contacts.TABLE_NAME, person2);
        databaseOperations.insert(DatabaseTables.Contacts.TABLE_NAME, person3);
        databaseOperations.insert(DatabaseTables.Contacts.TABLE_NAME, person4);
    }

    public class GetContactsFromDatabase extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseHelper helper = new DatabaseHelper(getActivity().getApplicationContext());
            SQLiteDatabase database = helper.getReadableDatabase();
            Cursor cursor = database.rawQuery("select * from " + DatabaseTables.Contacts.TABLE_NAME, null);
            cursor.moveToFirst();
            //ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();

            //read all rows
            while (!cursor.isAfterLast()) {
                HashMap<String, String> row = new HashMap<String, String>();
                row.put(DatabaseTables.Contacts._ID, cursor.getString(cursor.getColumnIndex(DatabaseTables.Contacts._ID)));
                row.put(DatabaseTables.Contacts.USER_ID, cursor.getString(cursor.getColumnIndex(DatabaseTables.Contacts.USER_ID)));
                row.put(DatabaseTables.Contacts.FIRST_NAME, cursor.getString(cursor.getColumnIndex(DatabaseTables.Contacts.FIRST_NAME)));
                row.put(DatabaseTables.Contacts.LAST_NAME, cursor.getString(cursor.getColumnIndex(DatabaseTables.Contacts.LAST_NAME)));
                row.put(DatabaseTables.Contacts.IMAGE_PATH, cursor.getString(cursor.getColumnIndex(DatabaseTables.Contacts.IMAGE_PATH)));
                if (!contacts.contains(row))
                    contacts.add(row);
                cursor.moveToNext();
            }
            cursor.close();

            return null;
        }

        protected void onPostExecute(Void v){
            adapter.notifyDataSetChanged();
        }
    }

     /*View.OnClickListener displayUserProfile = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(getActivity(), UserProfileActivity.class);
            ContactsHolderClass.ContactsHolder contactsHolder = new ContactsHolderClass.ContactsHolder();
            contactsHolder = (ContactsHolderClass.ContactsHolder) v.getTag();
            //TextView textView = (TextView) v.getTag();
            intent.putExtra(FIRST_NAME, contactsHolder.firstName);
            intent.putExtra(LAST_NAME, contactsHolder.lastName);
            intent.putExtra(USER_ID, contactsHolder.userID);
            intent.putExtra(IMAGE_PATH, contactsHolder.imagePath);
            startActivityForResult(intent, 1);
        }
    };*/


    public void deleteContact (String userID) {
        DatabaseOperations databaseOperations = new DatabaseOperations(getActivity().getApplicationContext());
        databaseOperations.delete(DatabaseTables.Contacts.TABLE_NAME, DatabaseTables.Contacts.USER_ID, userID);
        for (int i=0; i< contacts.size(); i++){
            HashMap<String, String> temp = contacts.get(i);
            if (temp.get(DatabaseTables.Contacts.USER_ID).equals(userID)) {
                contacts.remove(i);
                break;
            }
        }
        adapter.notifyDataSetChanged();
        Toast.makeText(getActivity().getApplicationContext(), "Contact deleted", Toast.LENGTH_SHORT).show();
    }

    public void updateContactInfo(String newFirstName, String newLastName, String userID) {

        //write changes to database
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseTables.Contacts.FIRST_NAME, newFirstName);
        contentValues.put(DatabaseTables.Contacts.LAST_NAME, newLastName);
        DatabaseOperations databaseOperations = new DatabaseOperations(getActivity().getApplicationContext());
        databaseOperations.update(DatabaseTables.Contacts.TABLE_NAME, contentValues, DatabaseTables.Contacts.USER_ID, userID);

        for (int i=0; i< contacts.size(); i++) {
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



    public void onCreateContextMenu (ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        menu.add(0, view.getId(), 0, "Edit");
        menu.add(0, view.getId(), 0, "Delete");
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
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
}

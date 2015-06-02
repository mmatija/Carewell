package com.example.matija_pc.carewell.activities;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.matija_pc.carewell.R;
import com.example.matija_pc.carewell.adapters.TabsPagerAdapter;
import com.example.matija_pc.carewell.database.DatabaseOperations;
import com.example.matija_pc.carewell.database.DatabaseTables;
import com.example.matija_pc.carewell.fragments.CallsFragment;
import com.example.matija_pc.carewell.fragments.ContactsFragment;

import java.util.HashMap;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    public static final String FIRST_NAME = "com.example.matija_pc.carewell.FIRST_NAME";
    public static final String USER_ID = "com.example.matija_pc.carewell.USER_ID";
    public static final String LAST_NAME = "com.example.matija_pc.carewell.LAST_NAME";
    public static final String IMAGE_PATH = "com.example.matija_pc.carewell.IMAGE_PATH";
    public static final int DELETE = 10;
    public static final int UPDATE = 11;

    private ViewPager pager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set-up viewPager and action bars
        pager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(mAdapter);


        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);

        ActionBar.Tab contactsTab = actionBar.newTab()
                            .setText(R.string.contacts)
                            //.setTabListener(new ActionBarTabListener<>(this, "contacts", ContactsFragment.class));
                            .setTabListener(this);

        ActionBar.Tab callsTab = actionBar.newTab()
                            .setText("Calls Log")
                            //.setTabListener(new ActionBarTabListener<>(this, "calls", CallsFragment.class));
                            .setTabListener(this);

        ActionBar.Tab conversationsTab = actionBar.newTab()
                                        .setText("Conversations")
                                        .setTabListener(this);

        actionBar.addTab(contactsTab);
        actionBar.addTab(callsTab);
        //actionBar.addTab(conversationsTab);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                actionBar.setSelectedNavigationItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //getSupportActionBar().setTitle("Custom Title");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


    public void onActivityResult (int requestCode, int resultCode, Intent data){
        if (resultCode==DELETE) {
            String deletedUserID = data.getStringExtra(USER_ID);
            deleteContact(deletedUserID);

        }
        if (resultCode==UPDATE) {
            String updatedUserID = data.getStringExtra(USER_ID);
            String newFirstName = data.getStringExtra(FIRST_NAME);
            String newLastName = data.getStringExtra(LAST_NAME);
            updateContactInfo(newFirstName, newLastName, updatedUserID);
        }

    }

    public void deleteContact (String userID) {
        DatabaseOperations databaseOperations = new DatabaseOperations(getApplicationContext());
        databaseOperations.delete(DatabaseTables.Contacts.TABLE_NAME, DatabaseTables.Contacts.USER_ID + "=?", userID);
        for (int i=0; i< ContactsFragment.contacts.size(); i++){
            HashMap<String, String> temp = ContactsFragment.contacts.get(i);
            if (temp.get(DatabaseTables.Contacts.USER_ID).equals(userID)) {
                ContactsFragment.contacts.remove(i);
                break;
            }
        }

        ContactsFragment.adapter.notifyDataSetChanged();
        //CallsAdapter.distinctContacts.clear();
        CallsFragment.callsAdapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), "Contact deleted", Toast.LENGTH_SHORT).show();
    }

    public void updateContactInfo(String newFirstName, String newLastName, String userID) {

        //write changes to database
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseTables.Contacts.FIRST_NAME, newFirstName);
        contentValues.put(DatabaseTables.Contacts.LAST_NAME, newLastName);
        DatabaseOperations databaseOperations = new DatabaseOperations(getApplicationContext());
        databaseOperations.update(DatabaseTables.Contacts.TABLE_NAME, contentValues, DatabaseTables.Contacts.USER_ID, userID);

        for (int i=0; i< ContactsFragment.contacts.size(); i++) {
            HashMap<String, String> temp = new HashMap<String, String>();
            temp = ContactsFragment.contacts.get(i);
            if (temp.get(DatabaseTables.Contacts.USER_ID).equals(userID)) {
                temp.put(DatabaseTables.Contacts.FIRST_NAME, newFirstName);
                temp.put(DatabaseTables.Contacts.LAST_NAME, newLastName);
                ContactsFragment.contacts.set(i, temp);
            }
        }
        ContactsFragment.adapter.notifyDataSetChanged();
        CallsFragment.callsAdapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), "Contact updated", Toast.LENGTH_SHORT).show();
    }

}

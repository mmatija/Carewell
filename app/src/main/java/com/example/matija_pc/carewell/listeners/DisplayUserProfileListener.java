package com.example.matija_pc.carewell.listeners;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;

import com.example.matija_pc.carewell.activities.MainActivity;
import com.example.matija_pc.carewell.activities.UserProfileActivity;
import com.example.matija_pc.carewell.adapters.ContactsHolderClass;
import com.example.matija_pc.carewell.database.DatabaseOperations;
import com.example.matija_pc.carewell.database.DatabaseTables;

/**
 * Created by Matija-PC on 13.4.2015..
 */
public class DisplayUserProfileListener implements View.OnClickListener {
    Activity activity;

    public DisplayUserProfileListener(Activity activity) {
        this.activity=activity;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(activity, UserProfileActivity.class);
        ContactsHolderClass.ContactsHolder contactsHolder = new ContactsHolderClass.ContactsHolder();
        contactsHolder = (ContactsHolderClass.ContactsHolder) v.getTag();
        DatabaseOperations databaseOperations = new DatabaseOperations(activity.getApplicationContext());
        String query = "SELECT * FROM " + DatabaseTables.Contacts.TABLE_NAME + " WHERE " +
                        DatabaseTables.Contacts.USER_ID + "=?";
        Cursor result = databaseOperations.select(query, contactsHolder.userID);
        if (result.getCount()<=0)
            return;
        intent.putExtra(MainActivity.FIRST_NAME, contactsHolder.firstName);
        intent.putExtra(MainActivity.LAST_NAME, contactsHolder.lastName);
        intent.putExtra(MainActivity.USER_ID, contactsHolder.userID);
        intent.putExtra(MainActivity.USER_NAME, contactsHolder.userName);
        intent.putExtra(MainActivity.IMAGE_PATH, contactsHolder.imagePath);
        activity.startActivityForResult(intent, 1);
    }
}

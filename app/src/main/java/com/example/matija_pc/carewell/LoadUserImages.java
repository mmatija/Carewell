package com.example.matija_pc.carewell;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.example.matija_pc.carewell.adapters.UserImageLoader;
import com.example.matija_pc.carewell.database.DatabaseOperations;
import com.example.matija_pc.carewell.database.DatabaseTables;

import java.util.ArrayList;

/**
 * Created by Matija-PC on 14.5.2015..
 */
public class LoadUserImages extends AsyncTask<Void, Integer, Void> {

    ArrayList<UserImageLoader> mUserImages;
    Context mContext;

    public LoadUserImages(ArrayList<UserImageLoader> userImages, Context context) {
        mUserImages = userImages;
        mContext = context;
    }


    @Override
    protected Void doInBackground(Void... params) {
        String query = "SELECT " + DatabaseTables.Contacts.USER_ID + " FROM " + DatabaseTables.Contacts.TABLE_NAME;
        DatabaseOperations databaseOperations = new DatabaseOperations(mContext);
        Cursor result = databaseOperations.select(query, null);
        result.moveToFirst();
        while (!result.isAfterLast()) {
            UserImageLoader imageLoader = new UserImageLoader(result.getString(0), mContext);
            mUserImages.add(imageLoader);
            result.moveToNext();
        }
        result.close();
        return null;
    }

}

package com.example.matija_pc.carewell.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;

import com.example.matija_pc.carewell.BitmapScaler;
import com.example.matija_pc.carewell.database.DatabaseOperations;
import com.example.matija_pc.carewell.database.DatabaseTables;

/**
 * Created by Matija-PC on 16.4.2015..
 */
public class UserImageLoader {
    public String userID;
    String imagePath;
    Bitmap bitmap;
    Context mContext;

    public UserImageLoader(String userID, Context context) {
        mContext = context;
        this.userID = userID;
        imagePath="";
        DatabaseOperations databaseOperations = new DatabaseOperations(mContext);
        String query =  "SELECT " + DatabaseTables.Contacts.IMAGE_PATH + " FROM " +
                DatabaseTables.Contacts.TABLE_NAME + " WHERE " +
                DatabaseTables.Contacts.USER_ID + "=?";
        Cursor result = databaseOperations.select(query, userID);
        if(result.moveToFirst()) {
            imagePath = result.getString(0);
        }
        bitmap = BitmapScaler.decodeSampledBitmap(imagePath, 90, 90);
        result.close();
    }
}

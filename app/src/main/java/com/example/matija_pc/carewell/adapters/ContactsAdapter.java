package com.example.matija_pc.carewell.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.matija_pc.carewell.R;
import com.example.matija_pc.carewell.database.DatabaseOperations;
import com.example.matija_pc.carewell.database.DatabaseTables;
import com.example.matija_pc.carewell.listeners.CallButtonListener;
import com.example.matija_pc.carewell.listeners.DisplayUserProfileListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Matija-PC on 3.4.2015..
 */

//adapter class for displaying contacts list
public class ContactsAdapter extends BaseAdapter {
    ArrayList<HashMap<String, String>> mContacts = new ArrayList<HashMap<String, String>>();
    public static ArrayList<UserImageLoader> distinctContacts;
    Context mContext;
    Activity mActivity;

    public ContactsAdapter(Activity activity, ArrayList<HashMap<String, String>> contacts){
        mContext= activity.getApplicationContext();
        mContacts =contacts;
        mActivity = activity;
        distinctContacts = new ArrayList<>();
        String query = "SELECT " + DatabaseTables.Contacts.USER_ID + " FROM " + DatabaseTables.Contacts.TABLE_NAME;
        DatabaseOperations databaseOperations = new DatabaseOperations(mContext);
        Cursor result = databaseOperations.select(query, null);
        result.moveToFirst();
        while (!result.isAfterLast()) {
            UserImageLoader imageLoader = new UserImageLoader(result.getString(0), mContext);
            distinctContacts.add(imageLoader);
            result.moveToNext();
        }
        result.close();
    }

    public long getItemId(int position){
        return position;
    }

    public Object getItem(int pos){
        return mContacts.get(pos);
    }

    public int getCount(){
        return mContacts.size();
    }


    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = view;
        if (v==null){
            v=inflater.inflate(R.layout.contact, null);
        }

        ContactsHolderClass.ContactsHolder contactsHolder = new ContactsHolderClass.ContactsHolder();
        contactsHolder._ID = mContacts.get(position).get(DatabaseTables.Contacts._ID);
        contactsHolder.userID = mContacts.get(position).get(DatabaseTables.Contacts.USER_ID);
        contactsHolder.firstName = mContacts.get(position).get(DatabaseTables.Contacts.FIRST_NAME);
        contactsHolder.lastName = mContacts.get(position).get(DatabaseTables.Contacts.LAST_NAME);
        contactsHolder.imagePath = mContacts.get(position).get(DatabaseTables.Contacts.IMAGE_PATH);


        RelativeLayout relativeLayout = (RelativeLayout) v.findViewById(R.id.contact_relative_layout);
        relativeLayout.setLongClickable(true);
        relativeLayout.setTag(contactsHolder);
        relativeLayout.setOnClickListener(new DisplayUserProfileListener(mActivity));
        ImageView userImage = (ImageView) v.findViewById(R.id.user_picture_thumbnail);
        TextView userInfo = (TextView) v.findViewById(R.id.user_info);
        //userInfo.setLongClickable(true);
        userInfo.setText(contactsHolder.firstName + " " + contactsHolder.lastName);
        userImage.setTag(contactsHolder);
        //userInfo.setTag(contactsHolder);
        userImage.setOnClickListener(new DisplayUserProfileListener(mActivity));
        //userInfo.setOnClickListener(new DisplayUserProfileListener(mActivity));

        boolean userImageExists = false;
        for (UserImageLoader iter : distinctContacts) {
            if (iter.userID.equals(contactsHolder.userID)) {
                userImageExists = true;
                if (iter.bitmap != null) {
                    userImage.setImageBitmap(iter.bitmap);
                    userImage.setBackgroundColor(0x0);
                }
                else
                    userImage.setImageResource(R.drawable.generic_picture);
                break;
            }
        }

        if (!userImageExists) {
            UserImageLoader imageLoader = new UserImageLoader(contactsHolder.userID, mContext);
            distinctContacts.add(imageLoader);

            if (imageLoader.bitmap != null) {
                userImage.setImageBitmap(imageLoader.bitmap);
                userImage.setBackgroundColor(0x0);
            }
            else
                userImage.setImageResource(R.drawable.generic_picture);
        }

        /*
        Bitmap image = BitmapScaler.decodeSampledBitmap(contactsHolder.imagePath, 100, 100);
        if (image != null) {
            userImage.setImageBitmap(image);
            userImage.setBackgroundColor(0x0);

        }
        else
            userImage.setImageResource(R.drawable.generic_picture);*/

        ImageButton videCallButton = (ImageButton) v.findViewById(R.id.video_call_button);
        ImageButton audioCallButton = (ImageButton) v.findViewById(R.id.audio_call_button);

        CallButtonListener.CallHelper videCallHelper = new CallButtonListener.CallHelper();
        videCallHelper.userID = contactsHolder.userID;
        videCallHelper.callType = "video";

        CallButtonListener.CallHelper audioCallHelper = new CallButtonListener.CallHelper();
        audioCallHelper.userID = contactsHolder.userID;
        audioCallHelper.callType = "audio";

        videCallButton.setTag(videCallHelper);
        audioCallButton.setTag(audioCallHelper);

        videCallButton.setOnClickListener(new CallButtonListener(mContext));
        audioCallButton.setOnClickListener(new CallButtonListener(mContext));
        return v;
    }

}
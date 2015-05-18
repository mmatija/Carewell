package com.example.matija_pc.carewell.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.matija_pc.carewell.LoadUserImages;
import com.example.matija_pc.carewell.R;
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
        mContacts = contacts;
        mActivity = activity;
        distinctContacts = new ArrayList<>();
        //get user pictures
        LoadUserImages loadUserImages = new LoadUserImages(distinctContacts, activity);
        loadUserImages.execute();
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
        ContactsViewHolder contactsViewHolder = new ContactsViewHolder();
        if (v==null){
            v=inflater.inflate(R.layout.contact, null);
            contactsViewHolder.relativeLayout = (RelativeLayout) v.findViewById(R.id.contact_relative_layout);
            contactsViewHolder.userInfo = (TextView) v.findViewById(R.id.user_info);
            contactsViewHolder.userImage = (ImageView) v.findViewById(R.id.user_picture_thumbnail);
            contactsViewHolder.videoCallButton = (ImageButton) v.findViewById(R.id.video_call_button);
            contactsViewHolder.audioCallButton = (ImageButton) v.findViewById(R.id.audio_call_button);
            v.setTag(contactsViewHolder);
        }
        else contactsViewHolder = (ContactsViewHolder) view.getTag();

        ContactsHolderClass.ContactsHolder contactsHolder = new ContactsHolderClass.ContactsHolder();
        contactsHolder._ID = mContacts.get(position).get(DatabaseTables.Contacts._ID);
        contactsHolder.userID = mContacts.get(position).get(DatabaseTables.Contacts.USER_ID);
        contactsHolder.firstName = mContacts.get(position).get(DatabaseTables.Contacts.FIRST_NAME);
        contactsHolder.lastName = mContacts.get(position).get(DatabaseTables.Contacts.LAST_NAME);
        contactsHolder.imagePath = mContacts.get(position).get(DatabaseTables.Contacts.IMAGE_PATH);


        contactsViewHolder.relativeLayout.setLongClickable(true);
        contactsViewHolder.relativeLayout.setTag(contactsHolder);
        contactsViewHolder.relativeLayout.setOnClickListener(new DisplayUserProfileListener(mActivity));

        contactsViewHolder.userInfo.setText(contactsHolder.firstName + " " + contactsHolder.lastName);
        contactsViewHolder.userInfo.setTag(contactsHolder);


        boolean userImageExists = false;
        for (UserImageLoader iter : distinctContacts) {
            if (iter.userID.equals(contactsHolder.userID)) {
                userImageExists = true;
                if (iter.bitmap != null) {

                    contactsViewHolder.userImage.setImageBitmap(iter.bitmap);
                    contactsViewHolder.userImage.setBackgroundColor(0x0);
                }
                else
                    contactsViewHolder.userImage.setImageResource(R.drawable.generic_picture);
                break;
            }
        }

        if (!userImageExists) {
            UserImageLoader imageLoader = new UserImageLoader(contactsHolder.userID, mContext);
            distinctContacts.add(imageLoader);

            if (imageLoader.bitmap != null) {
                contactsViewHolder.userImage.setImageBitmap(imageLoader.bitmap);
                contactsViewHolder.userImage.setBackgroundColor(0x0);
            }
            else
                contactsViewHolder.userImage.setImageResource(R.drawable.generic_picture);
        }


        CallButtonListener.CallHelper videCallHelper = new CallButtonListener.CallHelper();
        videCallHelper.userID = contactsHolder.userID;
        videCallHelper.callType = "video";

        CallButtonListener.CallHelper audioCallHelper = new CallButtonListener.CallHelper();
        audioCallHelper.userID = contactsHolder.userID;
        audioCallHelper.callType = "audio";

        contactsViewHolder.videoCallButton.setTag(videCallHelper);
        contactsViewHolder.audioCallButton.setTag(audioCallHelper);
        contactsViewHolder.videoCallButton.setOnClickListener(new CallButtonListener(mActivity));
        contactsViewHolder.audioCallButton.setOnClickListener(new CallButtonListener(mActivity));
        return v;
    }

    private class ContactsViewHolder {
        public RelativeLayout relativeLayout;
        public ImageView userImage;
        public TextView userInfo;
        public ImageButton videoCallButton;
        public ImageButton audioCallButton;
    }

}
package com.example.matija_pc.carewell.adapters;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matija_pc.carewell.BitmapScaler;
import com.example.matija_pc.carewell.database.DatabaseTables;
import com.example.matija_pc.carewell.R;
import com.example.matija_pc.carewell.fragments.ContactsFragment;
import com.example.matija_pc.carewell.listeners.AudioCallButtonListener;
import com.example.matija_pc.carewell.listeners.DisplayUserProfileListener;
import com.example.matija_pc.carewell.listeners.VideoCallButtonListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Matija-PC on 3.4.2015..
 */

//adapter class for displaying contacts list
public class ContactsAdapter extends BaseAdapter {
    ArrayList<HashMap<String, String>> mContacts = new ArrayList<HashMap<String, String>>();
    Context mContext;
    Activity mActivity;

    public ContactsAdapter(Activity activity, ArrayList<HashMap<String, String>> contacts){
        mContext= activity.getApplicationContext();
        mContacts =contacts;
        mActivity = activity;
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
            v=inflater.inflate(R.layout.user, null);
        }

        ContactsHolderClass.ContactsHolder contactsHolder = new ContactsHolderClass.ContactsHolder();
        contactsHolder._ID = mContacts.get(position).get(DatabaseTables.Contacts._ID);
        contactsHolder.userID = mContacts.get(position).get(DatabaseTables.Contacts.USER_ID);
        contactsHolder.firstName = mContacts.get(position).get(DatabaseTables.Contacts.FIRST_NAME);
        contactsHolder.lastName = mContacts.get(position).get(DatabaseTables.Contacts.LAST_NAME);
        contactsHolder.imagePath = mContacts.get(position).get(DatabaseTables.Contacts.IMAGE_PATH);



        ImageView iv = (ImageView) v.findViewById(R.id.user_picture_thumbnail);
        TextView tv = (TextView) v.findViewById(R.id.user_info);
        tv.setLongClickable(true);
        tv.setText(contactsHolder.firstName+" "+contactsHolder.lastName);
        iv.setTag(contactsHolder);
        tv.setTag(contactsHolder);
        //iv.setOnClickListener(mListener);
        //tv.setOnClickListener(mListener);
        iv.setOnClickListener(new DisplayUserProfileListener(mActivity));
        tv.setOnClickListener(new DisplayUserProfileListener(mActivity));
        Bitmap image = BitmapScaler.decodeSampledBitmap(contactsHolder.imagePath, 100, 100);
        if (image != null) {
            iv.setImageBitmap(image);
            iv.setBackgroundColor(0x0);

        }
        else
            iv.setImageResource(R.drawable.generic_picture);

        ImageButton videCallButton = (ImageButton) v.findViewById(R.id.video_call_button);
        ImageButton audioCallButton = (ImageButton) v.findViewById(R.id.audio_call_button);

        videCallButton.setTag(contactsHolder.userID);
        audioCallButton.setTag(contactsHolder.userID);

        videCallButton.setOnClickListener(new VideoCallButtonListener(mContext));
        audioCallButton.setOnClickListener(new AudioCallButtonListener(mContext));
        //new GetUserImage().execute(iv);
        return v;
    }

    public class GetUserImage extends AsyncTask<ImageView, Void, AsyncTaskReturnItem> {
        protected AsyncTaskReturnItem doInBackground (ImageView... params) {
            ContactsHolderClass.ContactsHolder holder = (ContactsHolderClass.ContactsHolder) params[0].getTag();
            Bitmap bitmap = BitmapScaler.decodeSampledBitmap(holder.imagePath, 100, 100);
            AsyncTaskReturnItem returnItem = new AsyncTaskReturnItem(params[0], bitmap);
            return returnItem;
        }

        protected void onPostExecute(AsyncTaskReturnItem returnItem) {
            if (returnItem.bitmap!=null)
            {
                returnItem.imageView.setImageBitmap(returnItem.bitmap);
                returnItem.imageView.setBackgroundColor(0x0);
            }
            else
                returnItem.imageView.setImageResource(R.drawable.generic_picture);
            ContactsFragment.adapter.notifyDataSetChanged();

        }
    }

    public class AsyncTaskReturnItem {
        ImageView imageView;
        Bitmap bitmap;

        public AsyncTaskReturnItem(ImageView imageView, Bitmap bitmap) {
            this.imageView=imageView;
            this.bitmap=bitmap;
        }
    }

}
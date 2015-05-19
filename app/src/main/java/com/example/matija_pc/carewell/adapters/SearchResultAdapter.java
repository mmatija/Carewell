package com.example.matija_pc.carewell.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matija_pc.carewell.LoadUserImages;
import com.example.matija_pc.carewell.R;
import com.example.matija_pc.carewell.database.DatabaseTables;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Matija-PC on 20.4.2015..
 */
public class SearchResultAdapter extends BaseAdapter {

    ArrayList<HashMap<String, String>> mContacts;
    ArrayList<UserImageLoader> userPictures;
    Activity mActivity;

    public SearchResultAdapter(Activity activity, ArrayList<HashMap<String, String>> contacts) {
        mContacts = contacts;
        mActivity = activity;
        userPictures = new ArrayList<>();
        new LoadUserImages(userPictures, mActivity.getApplicationContext()).execute();
    }

    @Override
    public int getCount() {
        return mContacts.size();
    }

    @Override
    public Object getItem(int position) {
        return mContacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = LayoutInflater.from(mActivity.getApplicationContext()).inflate(R.layout.search_result, null);
        String userID = mContacts.get(position).get(DatabaseTables.Contacts.USER_ID);
        ImageView userImage = (ImageView) view.findViewById(R.id.user_picture_thumbnail);
        TextView userInfo = (TextView) view.findViewById(R.id.user_info);
        for (UserImageLoader iter : userPictures) {
            if (iter.userID.equals(userID)) {
                if (iter.bitmap != null) {
                    userImage.setImageBitmap(iter.bitmap);
                    userImage.setBackgroundColor(0x0);
                }
                else {
                    userImage.setImageResource(R.drawable.generic_picture);
                    userImage.setBackgroundColor(Color.parseColor("#ff9e9e9e"));
                }
                break;
            }
        }

        String firstName = mContacts.get(position).get(DatabaseTables.Contacts.FIRST_NAME);
        String lastName = mContacts.get(position).get(DatabaseTables.Contacts.LAST_NAME);
        userInfo.setText(firstName + " " + lastName);

        return view;
    }
}

package com.example.matija_pc.carewell.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matija_pc.carewell.R;
import com.example.matija_pc.carewell.database.DatabaseOperations;
import com.example.matija_pc.carewell.database.DatabaseTables;
import com.example.matija_pc.carewell.listeners.CallButtonListener;
import com.example.matija_pc.carewell.listeners.DisplayUserProfileListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Matija-PC on 13.4.2015..
 */
public class CallsAdapter extends BaseAdapter {
    ArrayList<HashMap<String, String>> mCalls;
    Context mContext;
    Activity mActivity;
    public static ArrayList<UserImageLoader> distinctContacts;
    String selectDistinct = "SELECT DISTINCT " + DatabaseTables.CallsLog.PERSON_CALLED +
            " FROM " + DatabaseTables.CallsLog.TABLE_NAME;


    public CallsAdapter (Activity activity, ArrayList<HashMap<String, String>> calls) {
        mContext = activity.getApplicationContext();
        mCalls = calls;
        mActivity = activity;
        distinctContacts = new ArrayList<>();
        DatabaseOperations databaseOperations = new DatabaseOperations(mContext);

        //select all distinct persons called and get their pictures
        Cursor result = databaseOperations.select(selectDistinct, null);
        result.moveToFirst();
        while (!result.isAfterLast()) {
            String userID = result.getString(0);
            UserImageLoader helper = new UserImageLoader(userID, mContext);
            distinctContacts.add(helper);
            result.moveToNext();
        }
        result.close();
    }

    @Override
    public int getCount() {
        return mCalls.size();
    }

    @Override
    public Object getItem(int position) {
        return mCalls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        CallsViewHolder callsViewHolder = new CallsViewHolder();

        if (v == null) {
            v = LayoutInflater.from(mContext).inflate(R.layout.call_log, null);
            callsViewHolder.userImage = (ImageView) v.findViewById(R.id.user_picture_thumbnail);
            callsViewHolder.userInfo = (TextView) v.findViewById(R.id.user_info);
            callsViewHolder.callDirection = (ImageView) v.findViewById(R.id.call_direction);
            callsViewHolder.callType = (ImageView) v.findViewById(R.id.call_type);
            callsViewHolder.callDuration = (TextView) v.findViewById(R.id.call_duration);
            callsViewHolder.callTime = (TextView) v.findViewById(R.id.call_time);
            callsViewHolder.videoCallButton = (ImageButton) v.findViewById(R.id.video_call_button);
            callsViewHolder.audioCallButton = (ImageButton) v.findViewById(R.id.audio_call_button);
            callsViewHolder.relativeLayout = (RelativeLayout) v.findViewById(R.id.call_log_relative_layout);
            v.setTag(callsViewHolder);
        }
        else callsViewHolder = (CallsViewHolder) v.getTag();

        CallsHolderClass.CallsHolder callsHolder = new CallsHolderClass.CallsHolder();
        callsHolder._ID = mCalls.get(position).get(DatabaseTables.CallsLog._ID);
        callsHolder.personCalled = mCalls.get(position).get(DatabaseTables.CallsLog.PERSON_CALLED);
        callsHolder.callStart = mCalls.get(position).get(DatabaseTables.CallsLog.CALL_START);
        callsHolder.callFinish = mCalls.get(position).get(DatabaseTables.CallsLog.CALL_FINISH);
        callsHolder.callDuration = mCalls.get(position).get(DatabaseTables.CallsLog.CALL_DURATION);
        callsHolder.callDirection = mCalls.get(position).get(DatabaseTables.CallsLog.CALL_DIRECTION);
        callsHolder.callType = mCalls.get(position).get(DatabaseTables.CallsLog.CALL_TYPE);

        /*String rawQuery =   "SELECT * FROM " + DatabaseTables.CallsLog.TABLE_NAME + " JOIN " +
                                    DatabaseTables.Contacts.TABLE_NAME + " ON " + DatabaseTables.Contacts.USER_ID +
                                    "=" + DatabaseTables.CallsLog.PERSON_CALLED + " WHERE " + DatabaseTables.CallsLog.PERSON_CALLED +
                                    "=?";*/

        String rawQuery = "SELECT * FROM " + DatabaseTables.Contacts.TABLE_NAME + " WHERE " +
                            DatabaseTables.Contacts.USER_ID + "=?";


        DatabaseOperations databaseOperations = new DatabaseOperations(mContext);
        Cursor result = databaseOperations.select(rawQuery, callsHolder.personCalled);

        callsViewHolder.relativeLayout.setLongClickable(true);

        //if there are no rows, i.e. user is deleted
        if(!result.moveToFirst()) {
            callsViewHolder.userInfo.setText("Unknown");
            callsViewHolder.userImage.setImageResource(R.drawable.generic_picture);
            for (int i=0; i<distinctContacts.size(); i++)
                if (distinctContacts.get(i).userID.equals(callsHolder.personCalled)) {
                    distinctContacts.remove(i);
                    break;
                }

            //set call button listeners to display a message that this user cannot be called
            callsViewHolder.videoCallButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "Cannot call this user", Toast.LENGTH_SHORT).show();
                }
            });


            callsViewHolder.audioCallButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "Cannot call this user", Toast.LENGTH_SHORT).show();
                }
            });
        }

        else {
            String firstName = result.getString(result.getColumnIndex(DatabaseTables.Contacts.FIRST_NAME));
            String lastName = result.getString(result.getColumnIndex(DatabaseTables.Contacts.LAST_NAME));
            callsViewHolder.userInfo.setText(firstName + " " + lastName);

            ContactsHolderClass.ContactsHolder contactsHolder = new ContactsHolderClass.ContactsHolder();
            contactsHolder.firstName = result.getString(result.getColumnIndex(DatabaseTables.Contacts.FIRST_NAME));
            contactsHolder.lastName = result.getString(result.getColumnIndex(DatabaseTables.Contacts.LAST_NAME));
            contactsHolder.imagePath = result.getString(result.getColumnIndex(DatabaseTables.Contacts.IMAGE_PATH));
            contactsHolder.userID = result.getString(result.getColumnIndex(DatabaseTables.Contacts.USER_ID));


            callsViewHolder.relativeLayout.setTag(contactsHolder);
            callsViewHolder.relativeLayout.setOnClickListener(new DisplayUserProfileListener(mActivity));

            CallButtonListener.CallHelper videoCallHelper = new CallButtonListener.CallHelper();
            videoCallHelper.userID = callsHolder.personCalled;
            videoCallHelper.callType = "video";

            CallButtonListener.CallHelper audioCallHelper = new CallButtonListener.CallHelper();
            audioCallHelper.userID = callsHolder.personCalled;
            audioCallHelper.callType = "audio";


            callsViewHolder.videoCallButton.setTag(videoCallHelper);
            callsViewHolder.audioCallButton.setTag(audioCallHelper);

            callsViewHolder.videoCallButton.setOnClickListener(new CallButtonListener(mActivity));
            callsViewHolder.audioCallButton.setOnClickListener(new CallButtonListener(mActivity));
        }
        //set the name of the person

        SimpleDateFormat dateFormat = new SimpleDateFormat();
        Date tempStartDate = new Date(Long.parseLong(callsHolder.callStart));
        Date tempDuration = new Date(Long.parseLong(callsHolder.callDuration));

        String startDate = dateFormat.format(tempStartDate);

        callsViewHolder.callTime.setText(startDate);
        //calculate call duration
        long durationMilliseconds = tempDuration.getTime();

        String duration = calculateCallDuration(durationMilliseconds);
        callsViewHolder.callDuration.setText("Duration: " + duration);

        String tempCallDirection = callsHolder.callDirection;
        setCallDirectionIcon(callsViewHolder.callDirection, tempCallDirection);

        String tempCallType = callsHolder.callType;
        setCallType(callsViewHolder.callType, tempCallType);


        //set user image
        callsViewHolder.userImage.setBackgroundColor(Color.parseColor("#ff9e9e9e"));
        boolean userImageExists = false;
        for (int i=0; i<distinctContacts.size(); i++) {
            //if image is already loaded
            if (distinctContacts.get(i).userID.equals(callsHolder.personCalled)) {
                userImageExists = true;
                if (distinctContacts.get(i).bitmap!=null) {
                    callsViewHolder.userImage.setImageBitmap(distinctContacts.get(i).bitmap);
                    callsViewHolder.userImage.setBackgroundColor(0x0);
                }
                else {
                    callsViewHolder.userImage.setImageResource(R.drawable.generic_picture);
                }
                break;
            }
        }

        if (!userImageExists) {
            UserImageLoader imageLoader = new UserImageLoader(callsHolder.personCalled, mContext);
            distinctContacts.add(imageLoader);
            if (imageLoader.bitmap!=null) {
                callsViewHolder.userImage.setImageBitmap(imageLoader.bitmap);
                callsViewHolder.userImage.setBackgroundColor(0x0);
            }
            else {
                callsViewHolder.userImage.setImageResource(R.drawable.generic_picture);
            }
        }

        result.close();

        return v;
    }


    String calculateCallDuration(long milliseconds) {
        long elapsedSeconds = milliseconds / 1000;
        long elapsedHours = (long) elapsedSeconds/3600;
        long remainder = elapsedSeconds - elapsedHours * 3600;
        long elapsedMinutes = (long) remainder / 60;
        remainder = remainder - elapsedMinutes * 60;
        elapsedSeconds = remainder;
        String duration = "";

        if (elapsedHours<10)
            duration+="0"+elapsedHours+":";
        else
            duration+=elapsedHours+":";

        if (elapsedMinutes<10)
            duration+="0"+elapsedMinutes+":";
        else
            duration+=elapsedMinutes+":";

        if (elapsedSeconds<10)
            duration+="0"+elapsedSeconds;
        else
            duration+=elapsedSeconds;

        return duration;
    }

    private void setCallDirectionIcon (ImageView iv, String callDirection) {
        switch (callDirection) {
            case "missed":
                iv.setImageResource(R.drawable.missed_call);
                break;
            case "incoming":
                iv.setImageResource(R.drawable.incoming_call);
                break;
            default:
                iv.setImageResource(R.drawable.outgoing_call);
                break;
        }
    }

    private void setCallType (ImageView iv, String callType) {
        if (callType.equals("video"))
            iv.setImageResource(R.drawable.video_call_icon);
        else
            iv.setImageResource(R.drawable.audio_call_icon);
    }

    private class CallsViewHolder {
        public RelativeLayout relativeLayout;
        public ImageView userImage;
        public TextView userInfo;
        public ImageView callDirection;
        public ImageView callType;
        public TextView callDuration;
        public TextView callTime;
        public ImageButton videoCallButton;
        public ImageButton audioCallButton;
    }

}

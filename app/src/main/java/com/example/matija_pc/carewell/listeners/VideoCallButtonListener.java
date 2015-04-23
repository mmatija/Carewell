package com.example.matija_pc.carewell.listeners;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

import com.example.matija_pc.carewell.adapters.CallsAdapter;
import com.example.matija_pc.carewell.database.DatabaseOperations;
import com.example.matija_pc.carewell.database.DatabaseTables;
import com.example.matija_pc.carewell.fragments.CallsFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Matija-PC on 13.4.2015..
 */
public class VideoCallButtonListener implements View.OnClickListener {
    Context mContext;
    public CallsAdapter mAdapter;
    public ArrayList<HashMap<String, String>> mCalls;

    public VideoCallButtonListener(Context context)
    {
        mContext = context;
    }

    @Override
    public void onClick(View v) {
        //String[] possibleCallers = {"mm", "jm", "tm"};
        String[] possibleCallDirections = {"incoming", "outgoing", "missed"};

        String personCalled = (String) v.getTag();
        Toast.makeText(mContext, "Video call", Toast.LENGTH_SHORT).show();
        ContentValues values = new ContentValues();
        Calendar calendar = Calendar.getInstance();
        long callStart = calendar.getTimeInMillis();
        Random r = new Random();
        long callFinish = callStart + r.nextInt(20001) + 1000;
        long callDuration = callFinish - callStart;
        String callType = "video";
        String callDirection = possibleCallDirections[r.nextInt(3)];

        values.put(DatabaseTables.CallsLog.PERSON_CALLED, personCalled);
        values.put(DatabaseTables.CallsLog.CALL_START, callStart);
        values.put(DatabaseTables.CallsLog.CALL_FINISH, callFinish);
        values.put(DatabaseTables.CallsLog.CALL_DURATION, callDuration);
        values.put(DatabaseTables.CallsLog.CALL_DIRECTION, callDirection);
        values.put(DatabaseTables.CallsLog.CALL_TYPE, callType);

        //insert call log into database
        DatabaseOperations databaseOperations = new DatabaseOperations(mContext);
        databaseOperations.insert(DatabaseTables.CallsLog.TABLE_NAME, values);
        addCallToAdapter(values);

    }

    public static void addCallToAdapter (ContentValues values) {
        HashMap<String, String> call = new HashMap<String, String>();
        call.put(DatabaseTables.CallsLog.PERSON_CALLED, values.getAsString(DatabaseTables.CallsLog.PERSON_CALLED));
        call.put(DatabaseTables.CallsLog.CALL_START, values.getAsString(DatabaseTables.CallsLog.CALL_START));
        call.put(DatabaseTables.CallsLog.CALL_FINISH, values.getAsString(DatabaseTables.CallsLog.CALL_FINISH));
        call.put(DatabaseTables.CallsLog.CALL_DURATION, values.getAsString(DatabaseTables.CallsLog.CALL_DURATION));
        call.put(DatabaseTables.CallsLog.CALL_DIRECTION, values.getAsString(DatabaseTables.CallsLog.CALL_DIRECTION));
        call.put(DatabaseTables.CallsLog.CALL_TYPE, values.getAsString(DatabaseTables.CallsLog.CALL_TYPE));
        CallsFragment.calls.add(call);
        CallsFragment.callsAdapter.notifyDataSetChanged();
    }
}

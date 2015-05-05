package com.example.matija_pc.carewell.listeners;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
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
public class CallButtonListener implements View.OnClickListener {
    private static Context mContext;
    public CallsAdapter mAdapter;
    public ArrayList<HashMap<String, String>> mCalls;

    public CallButtonListener(Context context)
    {
        mContext = context;
    }

    @Override
    public void onClick(View v) {
        //String[] possibleCallers = {"mm", "jm", "tm"};
        String[] possibleCallDirections = {"incoming", "outgoing", "missed"};

        CallHelper callHelper = (CallHelper) v.getTag();
        String personCalled = callHelper.userID;
        String callType = callHelper.callType;
        Toast.makeText(mContext, callType, Toast.LENGTH_SHORT).show();
        ContentValues values = new ContentValues();
        Calendar calendar = Calendar.getInstance();
        long callStart = calendar.getTimeInMillis();
        Random r = new Random();
        long callFinish = callStart + r.nextInt(20001) + 1000;
        long callDuration = callFinish - callStart;
        String callDirection = possibleCallDirections[r.nextInt(3)];
        if (callDirection.equals("missed")) callDuration = 0;

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
        DatabaseOperations databaseOperations = new DatabaseOperations(mContext);
        String query = "SELECT " + DatabaseTables.CallsLog._ID + " FROM " + DatabaseTables.CallsLog.TABLE_NAME +
                        " WHERE " + DatabaseTables.CallsLog.CALL_DIRECTION + "=?" + " AND " +
                        DatabaseTables.CallsLog.CALL_START + "=?" + " LIMIT 1";
        Cursor result = databaseOperations.select(query, values.getAsString(DatabaseTables.CallsLog.CALL_DIRECTION), values.getAsString(DatabaseTables.CallsLog.CALL_START));
        result.moveToFirst();
        long _ID = result.getLong(0);
        result.close();
        HashMap<String, String> call = new HashMap<String, String>();
        call.put(DatabaseTables.CallsLog._ID, String.valueOf(_ID));
        call.put(DatabaseTables.CallsLog.PERSON_CALLED, values.getAsString(DatabaseTables.CallsLog.PERSON_CALLED));
        call.put(DatabaseTables.CallsLog.CALL_START, values.getAsString(DatabaseTables.CallsLog.CALL_START));
        call.put(DatabaseTables.CallsLog.CALL_FINISH, values.getAsString(DatabaseTables.CallsLog.CALL_FINISH));
        call.put(DatabaseTables.CallsLog.CALL_DURATION, values.getAsString(DatabaseTables.CallsLog.CALL_DURATION));
        call.put(DatabaseTables.CallsLog.CALL_DIRECTION, values.getAsString(DatabaseTables.CallsLog.CALL_DIRECTION));
        call.put(DatabaseTables.CallsLog.CALL_TYPE, values.getAsString(DatabaseTables.CallsLog.CALL_TYPE));
        CallsFragment.calls.add(call);
        CallsFragment.callsAdapter.notifyDataSetChanged();
    }

    //class which is used for adding tags to views so audio and video calls can be distinguished
    public static class CallHelper {
        public String userID;
        public String callType;

        public CallHelper() {
            userID = "";
            callType = "";
        }
    }
}

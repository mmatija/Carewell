package com.example.matija_pc.carewell.listeners;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;

import com.example.matija_pc.carewell.activities.CallActivity;
import com.example.matija_pc.carewell.adapters.CallsAdapter;
import com.example.matija_pc.carewell.database.DatabaseOperations;
import com.example.matija_pc.carewell.database.DatabaseTables;
import com.example.matija_pc.carewell.fragments.CallsFragment;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Matija-PC on 13.4.2015..
 */
public class CallButtonListener implements View.OnClickListener {
    public static Activity mActivity;
    private static int CALL_ACTIVITY_CODE = 100;
    public CallsAdapter mAdapter;
    public ArrayList<HashMap<String, String>> mCalls;
    private static CallHelper callHelper;
    private static long callStart;

    public CallButtonListener(Activity activity)
    {
        mActivity = activity;
    }

    @Override
    public void onClick(View v) {
        callHelper = (CallHelper) v.getTag();
        callStart = System.currentTimeMillis();
        Intent intent = new Intent(mActivity, CallActivity.class);
        mActivity.startActivityForResult(intent, CALL_ACTIVITY_CODE);
    }

    public static void addCallToAdapter (long callFinish) {

        ContentValues values = new ContentValues();
        long callDuration = callFinish - callStart;
        String callDirection = "outgoing";
        if (callDirection.equals("missed")) callDuration = 0;

        values.put(DatabaseTables.CallsLog.PERSON_CALLED, callHelper.userID);
        values.put(DatabaseTables.CallsLog.CALL_START, callStart);
        values.put(DatabaseTables.CallsLog.CALL_FINISH, callFinish);
        values.put(DatabaseTables.CallsLog.CALL_DURATION, callDuration);
        values.put(DatabaseTables.CallsLog.CALL_DIRECTION, callDirection);
        values.put(DatabaseTables.CallsLog.CALL_TYPE, callHelper.callType);

        //insert call log into database
        DatabaseOperations databaseOperations = new DatabaseOperations(mActivity);
        databaseOperations.insert(DatabaseTables.CallsLog.TABLE_NAME, values);


        //DatabaseOperations databaseOperations = new DatabaseOperations(mActivity);
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
        CallsFragment.calls.add(0,call);
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

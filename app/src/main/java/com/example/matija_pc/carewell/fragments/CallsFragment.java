package com.example.matija_pc.carewell.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.matija_pc.carewell.R;
import com.example.matija_pc.carewell.adapters.CallsAdapter;
import com.example.matija_pc.carewell.database.DatabaseOperations;
import com.example.matija_pc.carewell.database.DatabaseTables;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Matija-PC on 13.4.2015..
 */
public class CallsFragment extends Fragment {

    public static ArrayList<HashMap<String, String>> calls;
    public static CallsAdapter callsAdapter;
    public ListView listView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calls_fragment, container, false);
        listView = (ListView) view.findViewById(R.id.calls_list);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        calls = new ArrayList<HashMap<String, String>>();
        callsAdapter = new CallsAdapter(getActivity(), calls);
        listView.setAdapter(callsAdapter);
        //listView.setItemsCanFocus(true);
        //registerForContextMenu(listView);
        new GetCallsFromDatabase().execute();
    }

    private class GetCallsFromDatabase extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseOperations databaseOperations = new DatabaseOperations(getActivity().getApplicationContext());
            Cursor result = databaseOperations.select("SELECT * FROM " + DatabaseTables.CallsLog.TABLE_NAME, null);
            result.moveToFirst();
            while (!result.isAfterLast()) {
                HashMap<String, String> call = new HashMap<String, String>();
                call.put(DatabaseTables.CallsLog.PERSON_CALLED, result.getString(result.getColumnIndex(DatabaseTables.CallsLog.PERSON_CALLED)));
                call.put(DatabaseTables.CallsLog.CALL_TYPE, result.getString(result.getColumnIndex(DatabaseTables.CallsLog.CALL_TYPE)));
                call.put(DatabaseTables.CallsLog.CALL_DIRECTION, result.getString(result.getColumnIndex(DatabaseTables.CallsLog.CALL_DIRECTION)));
                long callStart = result.getLong(result.getColumnIndex(DatabaseTables.CallsLog.CALL_START));
                call.put(DatabaseTables.CallsLog.CALL_START, String.valueOf(callStart));
                long callFinish = result.getLong(result.getColumnIndex(DatabaseTables.CallsLog.CALL_FINISH));
                call.put(DatabaseTables.CallsLog.CALL_FINISH, String.valueOf(callFinish));
                long callDuration = result.getLong(result.getColumnIndex(DatabaseTables.CallsLog.CALL_DURATION));
                call.put(DatabaseTables.CallsLog.CALL_DURATION, String.valueOf(callDuration));
                calls.add(call);
                result.moveToNext();

            }
            result.close();
            return null;
        }

        protected void onPostExecute (Void param) {
            callsAdapter.notifyDataSetChanged();
        }
    }

    /*public void onCreateContextMenu (ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        menu.add(0, view.getId(), 0, "Delete");
    }

    public boolean onContextItemSelected(MenuItem menuItem) {
        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
        final String _id = calls.get(adapterContextMenuInfo.position).get(DatabaseTables.CallsLog._ID);

        if (menuItem.getTitle().toString().equals("Delete")) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("Delete call log?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteCallLog(_id);
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();

            return true;
        }

        return super.onContextItemSelected(menuItem);
    }*/

    private void deleteCallLog(String _id) {
        //delete log from database
        DatabaseOperations databaseOperations = new DatabaseOperations(getActivity().getApplicationContext());
        databaseOperations.delete(DatabaseTables.CallsLog.TABLE_NAME, DatabaseTables.CallsLog._ID, _id);

        for (int i=0; i<calls.size(); i++) {
            if (calls.get(i).get(DatabaseTables.CallsLog._ID).equals(_id)) {
                calls.remove(i);
                callsAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.calls_fragment_menu, menu);
    }

    private void deleteAllCallLogs() {
        DatabaseOperations databaseOperations = new DatabaseOperations(getActivity().getApplicationContext());
        databaseOperations.delete(DatabaseTables.CallsLog.TABLE_NAME, null, null);
        calls.clear();
        callsAdapter.notifyDataSetChanged();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Delete all call logs?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAllCallLogs();
                    }
                })
                       .setNegativeButton("No", null)
                       .show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

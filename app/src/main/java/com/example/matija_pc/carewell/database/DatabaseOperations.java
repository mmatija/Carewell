package com.example.matija_pc.carewell.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Matija-PC on 5.4.2015..
 */
public class DatabaseOperations {

    private DatabaseHelper helper;
    private SQLiteDatabase writeDatabase;
    private SQLiteDatabase readDatabase;

    public DatabaseOperations(Context context){
        helper = new DatabaseHelper(context);
        writeDatabase = helper.getWritableDatabase();
        readDatabase = helper.getReadableDatabase();
    }

    public void insert (String tableName, ContentValues contentValues){
        writeDatabase.insert(tableName, null, contentValues);
    }

    public void delete (String tableName, String where, String args) {
        if (where==null && args==null)
            writeDatabase.delete(tableName, null, null);
        else
            writeDatabase.delete(tableName, where+"=?", new String[] {args} );
    }


    public void update (String tableName, ContentValues contentValues, String where, String value) {
        writeDatabase.update(tableName, contentValues, where+"=?", new String[] {value});
    }

    public Cursor select (String query, String where) {
        if (where==null)
            return readDatabase.rawQuery(query, null);
        return readDatabase.rawQuery(query, new String[] {where});
    }

    /*public Cursor selectDistinct (String tableName, String[] columns, String selection, String[] selectionArgs, String groupBy) {
        return readDatabase.query(true, tableName, columns, selection, selectionArgs, groupBy, null, null, null);
    }*/


}

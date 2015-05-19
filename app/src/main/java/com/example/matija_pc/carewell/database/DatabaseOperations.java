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

    public void delete (String tableName, String where, String... args) {
        if (where == null) writeDatabase.delete(tableName, null, null);
        writeDatabase.delete(tableName, where, args);
    }

    public void update (String tableName, ContentValues contentValues, String where, String... value) {
        writeDatabase.update(tableName, contentValues, where+"=?", value);
    }

    public Cursor select (String query, String... where) {
        return readDatabase.rawQuery(query, where);
    }



}

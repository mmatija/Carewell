package com.example.matija_pc.carewell.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Matija-PC on 30.3.2015..
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 6;
    public static final String DATABASE_NAME = "CarewellDatabase.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseTables.CREATE_TABLE_CONTACTS);
        db.execSQL(DatabaseTables.CREATE_TABLE_CALLS_LOG);
        db.execSQL(DatabaseTables.CREATE_TABLE_MESSAGES);
        db.execSQL(DatabaseTables.CREATE_TABLE_CONVERSATIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseTables.DELETE_TABLE_CALLS_LOG);
        db.execSQL(DatabaseTables.DELETE_TABLE_MESSAGES);
        db.execSQL(DatabaseTables.DELETE_TABLE_CONTACTS);
        db.execSQL(DatabaseTables.DELETE_TABLE_CONVERSATIONS);
        onCreate(db);
    }
}

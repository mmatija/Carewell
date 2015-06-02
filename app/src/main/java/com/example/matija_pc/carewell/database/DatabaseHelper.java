package com.example.matija_pc.carewell.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Matija-PC on 30.3.2015..
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 15;
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
        insertTestData(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseTables.DELETE_TABLE_CALLS_LOG);
        db.execSQL(DatabaseTables.DELETE_TABLE_MESSAGES);
        db.execSQL(DatabaseTables.DELETE_TABLE_CONTACTS);
        db.execSQL(DatabaseTables.DELETE_TABLE_CONVERSATIONS);
        onCreate(db);
    }

    public void insertTestData(SQLiteDatabase db) {

        ContentValues person1 = new ContentValues();
        ContentValues person2 = new ContentValues();
        ContentValues person3 = new ContentValues();
        ContentValues person4 = new ContentValues();

        person1.put(DatabaseTables.Contacts.USER_ID, 1);
        person1.put(DatabaseTables.Contacts.FIRST_NAME, "Matija");
        person1.put(DatabaseTables.Contacts.LAST_NAME, "Močilac");
        person1.put(DatabaseTables.Contacts.IMAGE_PATH, "");


        person2.put(DatabaseTables.Contacts.USER_ID, 2);
        person2.put(DatabaseTables.Contacts.FIRST_NAME, "Juraj");
        person2.put(DatabaseTables.Contacts.LAST_NAME, "Močilac");
        person2.put(DatabaseTables.Contacts.IMAGE_PATH, "");

        person3.put(DatabaseTables.Contacts.USER_ID, 3);
        person3.put(DatabaseTables.Contacts.FIRST_NAME, "Tereza");
        person3.put(DatabaseTables.Contacts.LAST_NAME, "Močilac");
        person3.put(DatabaseTables.Contacts.IMAGE_PATH, "");

        person4.put(DatabaseTables.Contacts.USER_ID, 4);
        person4.put(DatabaseTables.Contacts.FIRST_NAME, "Zvonimir");
        person4.put(DatabaseTables.Contacts.LAST_NAME, "Močilac");
        person4.put(DatabaseTables.Contacts.IMAGE_PATH, "");

        db.insert(DatabaseTables.Contacts.TABLE_NAME, null, person1);
        db.insert(DatabaseTables.Contacts.TABLE_NAME, null, person2);
        db.insert(DatabaseTables.Contacts.TABLE_NAME, null, person3);
        db.insert(DatabaseTables.Contacts.TABLE_NAME, null, person4);
    }
}

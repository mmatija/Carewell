package com.example.matija_pc.carewell.database;

import android.provider.BaseColumns;

/**
 * Created by Matija-PC on 30.3.2015..
 */
public final class DatabaseTables {
    public DatabaseTables() {}

    public static abstract class Contacts implements BaseColumns{
        public static final String TABLE_NAME = "Contacts";
        public static final String USER_ID = "id";
        public static final String USER_NAME = "userName";
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
        public static final String IMAGE_PATH = "imagePath";    //path to image location

    }

    public static abstract class CallsLog implements BaseColumns {
        public static final String TABLE_NAME = "CallsLog";
        public static final String CALL_START = "callStart";    //in milliseconds
        public static final String CALL_FINISH = "callFinish";  //in milliseconds
        public static final String PERSON_CALLED = "personCalled";
        public static final String CALL_TYPE = "callType";  //audio, video
        public static final String CALL_DIRECTION = "callDirection"; //incoming, outgoing, missed
        public static final String CALL_DURATION = "callDuration";  //in milliseconds
    }




    public static final String CREATE_TABLE_CONTACTS =
            "CREATE TABLE " + Contacts.TABLE_NAME + " ( " +
            Contacts._ID + " INTEGER PRIMARY KEY, " +
            Contacts.USER_ID + " INTEGER UNIQUE, " +
            Contacts.USER_NAME + " TEXT NOT NULL, " +
            Contacts.FIRST_NAME + " TEXT, " +
            Contacts.LAST_NAME + " TEXT, " +
            Contacts.IMAGE_PATH + " TEXT " + ")";

    public static final String  CREATE_TABLE_CALLS_LOG =
            "CREATE TABLE " + CallsLog.TABLE_NAME + " ( " +
            CallsLog._ID + " INTEGER PRIMARY KEY, " +
            CallsLog.CALL_START + " INTEGER NOT NULL, " +
            CallsLog.CALL_FINISH + " INTEGER NOT NULL, " +
            CallsLog.PERSON_CALLED + " TEXT NOT NULL, " +
            CallsLog.CALL_TYPE + " TEXT NOT NULL, " +
            CallsLog.CALL_DIRECTION + " TEXT NOT NULL," +
            CallsLog.CALL_DURATION + " INTEGER NOT NULL" + ")";


    public static final String DELETE_TABLE_CONTACTS =
            "DROP TABLE IF EXISTS " + Contacts.TABLE_NAME;

    public static final String DELETE_TABLE_CALLS_LOG =
            "DROP TABLE IF EXISTS " + CallsLog.TABLE_NAME;

}

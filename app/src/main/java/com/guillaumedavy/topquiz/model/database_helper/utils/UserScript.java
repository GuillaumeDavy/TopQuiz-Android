package com.guillaumedavy.topquiz.model.database_helper.utils;

public class UserScript {
    public static final String TABLE_NAME = "USER";
    public static final String COLUMN_USER_ID ="id";
    public static final String COLUMN_USER_NAME ="username";
    public static final String COLUMN_USER_PASSWORD ="password";
    public static final String COLUMN_USER_EMAIL ="email";
    public static final String COLUMN_USER_ISADMIN ="is_admin";

    public static final String createTableQuery(){
        return "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_NAME + " TEXT, "
                + COLUMN_USER_PASSWORD + " TEXT, "
                + COLUMN_USER_EMAIL + " TEXT, "
                + COLUMN_USER_ISADMIN + " BIT, "
                + "CONSTRAINT ck_testbool_isadmin CHECK (" + COLUMN_USER_ISADMIN + " IN (1,0))"
                + ")";
    }

    public static final String selectAllQuery(){
        return "SELECT  * FROM " + TABLE_NAME;
    }

    public static final String selectUserByEmail(String email){
        return "SELECT  * FROM " + TABLE_NAME
                + " WHERE " + COLUMN_USER_EMAIL + "=\"" + email + "\"";
    }

    public static final String selectUserById(long id){
        return "SELECT  * FROM " + TABLE_NAME
                + " WHERE " + COLUMN_USER_ID + "=" + id;
    }

    public static final String countAllQuery(){
        return "SELECT  * FROM " + TABLE_NAME;
    }

    public static final String dropTableQuery(){
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}

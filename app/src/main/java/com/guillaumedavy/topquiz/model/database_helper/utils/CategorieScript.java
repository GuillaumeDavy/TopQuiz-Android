package com.guillaumedavy.topquiz.model.database_helper.utils;

public class CategorieScript {
    public static final String TABLE_NAME = "CATEGORIE";
    public static final String COLUMN_CATEGORIE_ID ="id";
    public static final String COLUMN_CATEGORIE_NAME ="name";

    public static final String createTableQuery(){
        return "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_CATEGORIE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_CATEGORIE_NAME + " TEXT"
                + ")";
    }

    public static final String selectAllQuery(){
        return "SELECT  * FROM " + TABLE_NAME;
    }

    public static final String selectCategoryById(long id){
        return "SELECT  * FROM " + TABLE_NAME
                + " WHERE " + COLUMN_CATEGORIE_ID + "=" + id;
    }

    public static final String countAllQuery(){
        return "SELECT  * FROM " + TABLE_NAME;
    }

    public static final String dropTableQuery(){
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}

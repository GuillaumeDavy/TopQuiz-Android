package com.guillaumedavy.topquiz.model.database_helper.utils;

public class QuestionScript {
    public static final String TABLE_NAME = "QUESTION";
    public static final String COLUMN_QUESTION_ID ="id";
    public static final String COLUMN_QUESTION_VALUE ="value";
    public static final String COLUMN_QUESTION_CATEGORY_ID = "category_id";
    public static final String COLUMN_QUESTION_ANSWER1 ="answer_1";
    public static final String COLUMN_QUESTION_ANSWER2 ="answer_2";
    public static final String COLUMN_QUESTION_ANSWER3 ="answer_3";
    public static final String COLUMN_QUESTION_ANSWER4 ="answer_4";
    public static final String COLUMN_QUESTION_ANSWERNUMBER = "answer_number";

    public static final String createTableQuery(){
        return "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_QUESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"  // 0
                + COLUMN_QUESTION_CATEGORY_ID + " INTEGER, "                  // 1
                + COLUMN_QUESTION_VALUE + " TEXT, "                           // 2
                + COLUMN_QUESTION_ANSWER1 + " TEXT, "                         // 3
                + COLUMN_QUESTION_ANSWER2 + " TEXT, "                         // 4
                + COLUMN_QUESTION_ANSWER3 + " TEXT, "                         // 5
                + COLUMN_QUESTION_ANSWER4 + " TEXT, "                         // 6
                + COLUMN_QUESTION_ANSWERNUMBER + " INTEGER"                   // 7
                + ")";
    }

    public static final String selectAllQuery(){
        return "SELECT  * FROM " + TABLE_NAME;
    }

    public static final String selectAllQuestionForCategoryId(long categoryId){
        return "SELECT  * FROM " + TABLE_NAME
                + " WHERE " + COLUMN_QUESTION_CATEGORY_ID + "=" + categoryId;
    }

    /**
     * Requete SQL qui permet de récupérer l'id max de la table
     * @return La requete SQL
     */
    public static final String selectMaxId(){
        return "SELECT  MAX(" + COLUMN_QUESTION_ID + ") FROM " + TABLE_NAME;
    }

    public static final String countAllQuery(){
        return "SELECT  * FROM " + TABLE_NAME;
    }

    public static final String dropTableQuery(){
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}

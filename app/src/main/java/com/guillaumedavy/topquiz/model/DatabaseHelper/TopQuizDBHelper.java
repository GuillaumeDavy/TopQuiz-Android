package com.guillaumedavy.topquiz.model.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.guillaumedavy.topquiz.App;
import com.guillaumedavy.topquiz.R;
import com.guillaumedavy.topquiz.model.Category;
import com.guillaumedavy.topquiz.model.Question;

import java.util.ArrayList;
import java.util.List;

public class TopQuizDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "SQLite";
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "TOPQUIZ_DATABASE";
    private static final String TABLE_QUESTION = "QUESTION";
    private static final String COLUMN_QUESTION_ID ="id";
    private static final String COLUMN_QUESTION_VALUE ="value";
    private static final String COLUMN_QUESTION_CATEGORY_ID = "category_id";
    private static final String COLUMN_QUESTION_ANSWER1 ="answer_1";
    private static final String COLUMN_QUESTION_ANSWER2 ="answer_2";
    private static final String COLUMN_QUESTION_ANSWER3 ="answer_3";
    private static final String COLUMN_QUESTION_ANSWER4 ="answer_4";
    private static final String COLUMN_QUESTION_ANSWERNUMBER = "answer_number";

    public TopQuizDBHelper(Context context)  {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Script to create question table.
        String scriptQuestionTable = "CREATE TABLE " + TABLE_QUESTION + "("
                + COLUMN_QUESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"    // 0
                + COLUMN_QUESTION_CATEGORY_ID + " INTEGER, "                       // 1
                + COLUMN_QUESTION_VALUE + " TEXT, "                               // 2
                + COLUMN_QUESTION_ANSWER1 + " TEXT, "                              // 3
                + COLUMN_QUESTION_ANSWER2 + " TEXT, "                              // 4
                + COLUMN_QUESTION_ANSWER3 + " TEXT, "                              // 5
                + COLUMN_QUESTION_ANSWER4 + " TEXT, "                              // 6
                + COLUMN_QUESTION_ANSWERNUMBER + " INTEGER"                      // 7
                + ")";

        //Execute script
        db.execSQL(scriptQuestionTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION);

        // Create tables again
        onCreate(db);
    }

    /**
     * addQuestion : Insert a question into the database
     * TODO : Also add the category ID into the DB
     * @param question : Objet Question
     */
    public void addQuestion(Question question) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_QUESTION_VALUE, question.getQuestion());
        values.put(COLUMN_QUESTION_CATEGORY_ID, question.getCategory().getId());
        values.put(COLUMN_QUESTION_ANSWER1, question.getAnswer1());
        values.put(COLUMN_QUESTION_ANSWER2, question.getAnswer2());
        values.put(COLUMN_QUESTION_ANSWER3, question.getAnswer3());
        values.put(COLUMN_QUESTION_ANSWER4, question.getAnswer4());
        values.put(COLUMN_QUESTION_ANSWERNUMBER, question.getAnswerIndex());

        // Inserting Row
        db.insert(TABLE_QUESTION, null, values);

        // Closing database connection
        db.close();
    }

    /**
     * getAllQuestions : retroune la liste de toute sles questions disponibles
     * @return All the questions
     */
    public List<Question> getAllQuestions() {

        List<Question> questionList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_QUESTION;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Question question = new Question(
                        cursor.getLong(0),      //ID
                        new Category(cursor.getInt(1), "Categorie Name"), //TODO appeler SQL de categorie
                        cursor.getString(2),    //QUESTION
                        cursor.getString(3),    //ANSWER 1
                        cursor.getString(4),    //ANSWER 2
                        cursor.getString(5),    //ANSWER 3
                        cursor.getString(6),    //ANSWER 1
                        cursor.getInt(7)        //CORRECT ANSWER
                );

                // Adding question to list
                questionList.add(question);
            } while (cursor.moveToNext());
            cursor.close();
        }
        // return note list
        return questionList;
    }

    // If question table has no data
    // default, Insert 2 records.
    public void createDefaultQuestionsIfNeed()  {
        if(this.getQuestionsCount() == 0) {
            Category cultureGenerale = new Category(1, App.getContext().getResources().getString(R.string.categorie1));
            Question question1 = new Question(
                    1,
                    cultureGenerale,
                    App.getContext().getResources().getString(R.string.question1),
                    App.getContext().getResources().getString(R.string.response11),
                    App.getContext().getResources().getString(R.string.response12),
                    App.getContext().getResources().getString(R.string.response13),
                    App.getContext().getResources().getString(R.string.response14),
                    0
            );

            Question question2 = new Question(
                    2,
                    cultureGenerale,
                    App.getContext().getResources().getString(R.string.question2),
                    App.getContext().getResources().getString(R.string.response21),
                    App.getContext().getResources().getString(R.string.response22),
                    App.getContext().getResources().getString(R.string.response23),
                    App.getContext().getResources().getString(R.string.response24),
                    3
            );

            Question question3 = new Question(
                    3,
                    cultureGenerale,
                    App.getContext().getResources().getString(R.string.question3),
                    App.getContext().getResources().getString(R.string.response31),
                    App.getContext().getResources().getString(R.string.response32),
                    App.getContext().getResources().getString(R.string.response33),
                    App.getContext().getResources().getString(R.string.response34),
                    3
            );

            Question question4 = new Question(
                    4,
                    cultureGenerale,
                    App.getContext().getResources().getString(R.string.question4),
                    App.getContext().getResources().getString(R.string.response41),
                    App.getContext().getResources().getString(R.string.response42),
                    App.getContext().getResources().getString(R.string.response43),
                    App.getContext().getResources().getString(R.string.response44),
                    2
            );

            Question question5 = new Question(
                    5,
                    cultureGenerale,
                    App.getContext().getResources().getString(R.string.question5),
                    App.getContext().getResources().getString(R.string.response51),
                    App.getContext().getResources().getString(R.string.response52),
                    App.getContext().getResources().getString(R.string.response53),
                    App.getContext().getResources().getString(R.string.response54),
                    2
            );

            Question question6 = new Question(
                    6,
                    cultureGenerale,
                    App.getContext().getResources().getString(R.string.question6),
                    App.getContext().getResources().getString(R.string.response61),
                    App.getContext().getResources().getString(R.string.response62),
                    App.getContext().getResources().getString(R.string.response63),
                    App.getContext().getResources().getString(R.string.response64),
                    1
            );

            this.addQuestion(question1);
            this.addQuestion(question2);
            this.addQuestion(question3);
            this.addQuestion(question4);
            this.addQuestion(question5);
            this.addQuestion(question6);
        }
    }

    public int getQuestionsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_QUESTION;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();

        cursor.close();

        // return count
        return count;
    }
}

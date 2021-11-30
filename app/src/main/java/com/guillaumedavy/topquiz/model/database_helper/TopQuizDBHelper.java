package com.guillaumedavy.topquiz.model.database_helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Path;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.guillaumedavy.topquiz.App;
import com.guillaumedavy.topquiz.R;
import com.guillaumedavy.topquiz.model.Category;
import com.guillaumedavy.topquiz.model.User;
import com.guillaumedavy.topquiz.model.database_helper.utils.CategorieScript;
import com.guillaumedavy.topquiz.model.database_helper.utils.QuestionScript;
import com.guillaumedavy.topquiz.model.Question;
import com.guillaumedavy.topquiz.model.database_helper.utils.UserScript;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TopQuizDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "SQLite";
    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "TOPQUIZ_DATABASE";


    public TopQuizDBHelper(Context context)  {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Execute script
        db.execSQL(QuestionScript.createTableQuery());
        db.execSQL(CategorieScript.createTableQuery());
        db.execSQL(UserScript.createTableQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL(QuestionScript.dropTableQuery());
        db.execSQL(CategorieScript.dropTableQuery());
        db.execSQL(UserScript.dropTableQuery());

        // Create tables again
        onCreate(db);
    }

    /**
     * addQuestion : Insert a question into the database
     * @param question : Objet Question
     */
    public void addQuestion(Question question) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(QuestionScript.COLUMN_QUESTION_VALUE, question.getQuestion());
        values.put(QuestionScript.COLUMN_QUESTION_CATEGORY_ID, question.getCategory().getId());
        values.put(QuestionScript.COLUMN_QUESTION_ANSWER1, question.getAnswer1());
        values.put(QuestionScript.COLUMN_QUESTION_ANSWER2, question.getAnswer2());
        values.put(QuestionScript.COLUMN_QUESTION_ANSWER3, question.getAnswer3());
        values.put(QuestionScript.COLUMN_QUESTION_ANSWER4, question.getAnswer4());
        values.put(QuestionScript.COLUMN_QUESTION_ANSWERNUMBER, question.getAnswerIndex());

        // Inserting Row
        db.insert(QuestionScript.TABLE_NAME, null, values);

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
        String selectQuery = QuestionScript.selectAllQuery();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Question question = new Question(
                        cursor.getLong(0),      //ID
                        this.getCategoryById(cursor.getInt(1)), //CATEGORY
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

    /**
     * addCategorie : Insert a categorie into the database
     * @param category : Objet Category
     */
    public void addCategorie(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CategorieScript.COLUMN_CATEGORIE_NAME, category.getName());

        // Inserting Row
        db.insert(CategorieScript.TABLE_NAME, null, values);

        // Closing database connection
        db.close();
    }

    /**
     * getAllUsers : retroune la liste de tous les utilisateurs
     * @return All the users
     */
    public List<User> getAllUsers() {

        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(UserScript.selectAllQuery(), null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User user = new User(
                        cursor.getLong(0),              //ID
                        cursor.getString(1),            //USERNAME
                        cursor.getString(2),            //PASSWORD
                        cursor.getString(3),            //EMAIL
                        cursor.getInt(4) == 1   //ISADMIN
                );

                userList.add(user);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return userList;
    }

    /**
     * Get a user by email
     * @param email l'email de l'utilisateur recherché
     * @return un utilisateur
     */
    public User getUserByEmail(String email){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(UserScript.selectUserByEmail(email), null);

        if (cursor.moveToFirst()) {
            User user = new User(
                    cursor.getLong(0),              //ID
                    cursor.getString(1),            //USERNAME
                    cursor.getString(2),            //PASSWORD
                    cursor.getString(3),            //EMAIL
                    cursor.getInt(4) == 1   //ISADMIN
            );
            cursor.close();
            return user;
        }
        throw new SQLException("No match for user email=" + email);
    }

    /**
     * addUser : Insert a user into the database
     * @param user : Objet User
     */
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserScript.COLUMN_USER_NAME, user.getUsername());
        values.put(UserScript.COLUMN_USER_PASSWORD, user.getPassword());
        values.put(UserScript.COLUMN_USER_EMAIL, user.getEmail());
        values.put(UserScript.COLUMN_USER_ISADMIN, user.isAdmin() ? 1 : 0); //1 si admin 0 sinon

        // Inserting Row
        db.insert(UserScript.TABLE_NAME, null, values);

        // Closing database connection
        db.close();
    }

    /**
     * getAllCategories : retroune la liste de toute sles questions disponibles
     * @return All the questions
     */
    public List<Category> getAllCategories() {

        List<Category> categoryList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(CategorieScript.selectAllQuery(), null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Category category = new Category(
                        cursor.getLong(0),      //ID
                        cursor.getString(1)     //NAME
                );

                categoryList.add(category);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return categoryList;
    }

    /**
     * Get a categorie by Id
     * @param id l'id de la categorie recherchée
     * @return une categorie
     */
    public Category getCategoryById(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(CategorieScript.selectCategoryById(id), null);

        if (cursor.moveToFirst()) {
            Category category = new Category(
                    cursor.getLong(0),      //ID
                    cursor.getString(1)     //NAME
            );
            cursor.close();
            return category;
        }
        throw new SQLException("No match for category id=" + id);
    }

    /**
     * Create default questions
     */
    public void createDefaultCategoriesAndQuestionsIfNeed() {
        if(this.getCategoriesCount() == 0){
            Category cultureGenerale = new Category(1, App.getContext().getResources().getString(R.string.categorie1));
            Category sport = new Category(2, App.getContext().getResources().getString(R.string.categorie2));
            this.addCategorie(cultureGenerale);
            this.addCategorie(sport);
        }
        if(this.getQuestionsCount() == 0) {
            Category cultureGenerale = this.getCategoryById(1);
            Category sport = this.getCategoryById(2);
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
                    sport,
                    App.getContext().getResources().getString(R.string.question4),
                    App.getContext().getResources().getString(R.string.response41),
                    App.getContext().getResources().getString(R.string.response42),
                    App.getContext().getResources().getString(R.string.response43),
                    App.getContext().getResources().getString(R.string.response44),
                    2
            );

            Question question5 = new Question(
                    5,
                    sport,
                    App.getContext().getResources().getString(R.string.question5),
                    App.getContext().getResources().getString(R.string.response51),
                    App.getContext().getResources().getString(R.string.response52),
                    App.getContext().getResources().getString(R.string.response53),
                    App.getContext().getResources().getString(R.string.response54),
                    2
            );

            Question question6 = new Question(
                    6,
                    sport,
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

    /**
     * Create one admin and one user
     */
    public void createDefaultUsers(){
        if(this.getUserCount() == 0){
            User admin = new User(1,"admin", "admin", "admin@email.fr", true);
            User user = new User(2,"user", "user", "user@email.fr", false);
            this.addUser(admin);
            this.addUser(user);
        }
    }

    private int getCategoriesCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(CategorieScript.countAllQuery(), null);

        int count = cursor.getCount();

        cursor.close();

        // return count
        return count;
    }

    private int getUserCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(UserScript.countAllQuery(), null);

        int count = cursor.getCount();

        cursor.close();

        // return count
        return count;
    }

    private int getQuestionsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(QuestionScript.countAllQuery(), null);

        int count = cursor.getCount();

        cursor.close();

        // return count
        return count;
    }
}

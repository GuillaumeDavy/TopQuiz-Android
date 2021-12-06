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
import com.guillaumedavy.topquiz.model.Score;
import com.guillaumedavy.topquiz.model.User;
import com.guillaumedavy.topquiz.model.database_helper.utils.CategorieScript;
import com.guillaumedavy.topquiz.model.database_helper.utils.QuestionScript;
import com.guillaumedavy.topquiz.model.Question;
import com.guillaumedavy.topquiz.model.database_helper.utils.ScoreScript;
import com.guillaumedavy.topquiz.model.database_helper.utils.UserScript;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TopQuizDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "SQLite";
    private static final int DATABASE_VERSION = 12;
    private static final String DATABASE_NAME = "TOPQUIZ_DATABASE";


    public TopQuizDBHelper(Context context)  {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Execute creation tables scripts
        db.execSQL(QuestionScript.createTableQuery());
        db.execSQL(CategorieScript.createTableQuery());
        db.execSQL(UserScript.createTableQuery());
        db.execSQL(ScoreScript.createTableQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL(QuestionScript.dropTableQuery());
        db.execSQL(CategorieScript.dropTableQuery());
        db.execSQL(UserScript.dropTableQuery());
        db.execSQL(ScoreScript.dropTableQuery());

        // Create tables again
        onCreate(db);
    }

    /**
     * Insert a question into the database
     * @param question : Objet Question
     */
    public void addQuestion(Question question) {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
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
        } catch (Exception e){
            throw e;
        } finally {
            // Closing database connection
            db.close();
        }
    }

    /**
     * retroune la liste de toute sles questions disponibles
     * @return All the questions
     */
    public List<Question> getAllQuestions() {
        List<Question> questionList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(QuestionScript.selectAllQuery(), null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Question question = new Question(
                            cursor.getLong(0),                      //ID
                            this.getCategoryById(cursor.getInt(1)), //CATEGORY
                            cursor.getString(2),                    //QUESTION
                            cursor.getString(3),                    //ANSWER 1
                            cursor.getString(4),                    //ANSWER 2
                            cursor.getString(5),                    //ANSWER 3
                            cursor.getString(6),                    //ANSWER 1
                            cursor.getInt(7)                        //CORRECT ANSWER
                    );

                    // Adding question to list
                    questionList.add(question);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            throw e;
        } finally {
            cursor.close();
        }
        return questionList;
    }

    /**
     * retroune la liste de tous les utilisateurs
     * @return All the users
     */
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(UserScript.selectAllQuery(), null);
        try{
            if (cursor.moveToFirst()) {
                do {
                    User user = new User(
                            cursor.getString(0),            //EMAIL
                            cursor.getString(1),            //USERNAME
                            cursor.getString(2),            //PASSWORD
                            cursor.getInt(3) == 1   //ISADMIN
                    );
                    userList.add(user);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            throw e;
        } finally {
            cursor.close();
        }
        return userList;
    }

    /**
     * Get a user by email
     * @param email l'email de l'utilisateur recherché
     * @return un utilisateur ou throw une exeption si non existant
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public Optional<User> getUserByEmail(String email){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(UserScript.selectUserByEmail(email), null);
        if (cursor.moveToFirst()) {
            User user = new User(
                    cursor.getString(0),            //EMAIL
                    cursor.getString(1),            //USERNAME
                    cursor.getString(2),            //PASSWORD
                    cursor.getInt(3) == 1   //ISADMIN
            );
            cursor.close();
            return Optional.of(user);
        }
        cursor.close();
        return Optional.empty();
    }

    /**
     * Vérifie si le mot de passe est correct.
     * S'il ne l'est pas, lève une exception
     * @param email l'email de l'utilisateur
     * @param password le mot de passe à vérifier
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void tryLogIn(String email, String password){
        Optional<User> maybeUser = this.getUserByEmail(email);
        if(!maybeUser.isPresent() || !password.equals(maybeUser.get().getPassword())){
            throw new SQLException("Incorrect email or password");
        }
    }

    /**
     * Insert a user into the database,
     * if getUserByEmail does not throw, add user
     * else throw SQLException user already exists
     * @param user : Objet User
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        Optional<User> maybeUser = this.getUserByEmail(user.getEmail());
        if(maybeUser.isPresent() && user.getEmail().equals(maybeUser.get().getEmail())){
            db.close();
            throw new SQLException("User already exists");
        }
        try{
            ContentValues values = new ContentValues();
            values.put(UserScript.COLUMN_USER_NAME, user.getUsername());
            values.put(UserScript.COLUMN_USER_PASSWORD, user.getPassword());
            values.put(UserScript.COLUMN_USER_EMAIL, user.getEmail());
            values.put(UserScript.COLUMN_USER_ISADMIN, user.isAdmin() ? 1 : 0); //1 si admin 0 sinon

            // Inserting Row
            db.insert(UserScript.TABLE_NAME, null, values);
        } catch (Exception e){
            throw e;
        } finally {
            // Closing database connection
            db.close();
        }
    }

    /**
     * Insert a categorie into the database
     * @param category : Objet Category
     */
    public void addCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            ContentValues values = new ContentValues();
            values.put(CategorieScript.COLUMN_CATEGORIE_NAME, category.getName());

            db.insert(CategorieScript.TABLE_NAME, null, values);
        } catch (Exception e) {
            throw e;
        } finally {
            db.close();
        }
    }

    /**
     * retroune la liste de toute sles questions disponibles
     * @return All the questions
     */
    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(CategorieScript.selectAllQuery(), null);
        try{
            if (cursor.moveToFirst()) {
                do {
                    Category category = new Category(
                            cursor.getLong(0),      //ID
                            cursor.getString(1)     //NAME
                    );

                    categoryList.add(category);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            throw e;
        } finally {
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
        cursor.close();
        throw new SQLException("No match for category id=" + id);
    }

    /**
     * Insert a score into the database
     * @param score : Objet Score
     */
    private void addScore(Score score) {
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            ContentValues values = new ContentValues();
            values.put(ScoreScript.COLUMN_SCORE_USER_EMAIL, score.getUser().getEmail());
            values.put(ScoreScript.COLUMN_SCORE_CATEGORYID, score.getCategory().getId());
            values.put(ScoreScript.COLUMN_SCORE_VALUE, score.getScore());

            db.insert(ScoreScript.TABLE_NAME, null, values);
        } catch (Exception e){
            throw e;
        } finally {
            db.close();
        }
    }

    /**
     * Met a jour un Score
     * @param score : le score a etre a jour en DB
     * @return le nombre de lignes affectées
     */
    public int updateScore(Score score){
        SQLiteDatabase db = this.getWritableDatabase();
        int rowUpdated = 0;
        try{
            ContentValues values = new ContentValues();
            values.put(ScoreScript.COLUMN_SCORE_USER_EMAIL, score.getUser().getEmail());
            values.put(ScoreScript.COLUMN_SCORE_CATEGORYID, score.getCategory().getId());
            values.put(ScoreScript.COLUMN_SCORE_VALUE, score.getScore());
            rowUpdated = db.update(ScoreScript.TABLE_NAME, values, ScoreScript.COLUMN_SCORE_ID + " = ?",
                    new String[]{String.valueOf(score.getId())});
        } catch (Exception e){
            throw e;
        } finally {
            db.close();
        }
        return rowUpdated;
    }

    /**
     * retroune la liste de tous les scores disponibles
     * @return All the score
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Score> getAllScores() {
        List<Score> scoreList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(ScoreScript.selectAllQuery(), null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    this.getUserByEmail(cursor.getString(1))
                            .map(user -> new Score(
                                    cursor.getLong(0),                          //ID
                                    user,                                                  //USER
                                    this.getCategoryById(cursor.getLong(2)),    //CATEGORY
                                    cursor.getInt(3)                            //SCORE
                            )).ifPresent(scoreList::add);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            throw e;
        } finally {
            cursor.close();
        }
        return scoreList;
    }

    /**
     * retourne la liste des scores pour un utilisateur
     * @param email : email de l'utilisateur
     * @return les scores d'un joueur
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Score> getAllScoreByUserEmail(String email){
        List<Score> scoreList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(ScoreScript.selectByUserEmailQuery(email), null);
        try{
            if(cursor.moveToFirst()){
                do {
                    this.getUserByEmail(cursor.getString(1))
                            .map(user -> new Score(
                                    cursor.getLong(0),                          //ID
                                    user,                                                  //USER
                                    this.getCategoryById(cursor.getLong(2)),    //CATEGORY
                                    cursor.getInt(3)                            //SCORE
                            )).ifPresent(scoreList::add);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            throw e;
        } finally {
            cursor.close();
        }
        return scoreList;
    }

    /**
     * retourne la liste des scores pour une categorie
     * @param id : id de la categorie
     * @return les scores d'une catégorie
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Score> getAllScoreByCategoryId(long id){
        List<Score> scoreList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(ScoreScript.selectByCategoryIdQuery(id), null);
        try{
            if(cursor.moveToFirst()){
                do {
                    this.getUserByEmail(cursor.getString(1))
                            .map(user -> new Score(
                                    cursor.getLong(0),                          //ID
                                    user,                                                  //USER
                                    this.getCategoryById(cursor.getLong(2)),    //CATEGORY
                                    cursor.getInt(3)                            //SCORE
                            )).ifPresent(scoreList::add);
                } while (cursor.moveToNext());
            }
        } catch (Exception e){
            throw e;
        } finally {
            cursor.close();
        }
        return scoreList;
    }

    /**
     * retourne le score pour un utilisateur pour une categorie,
     * s'il n'existe pas, il le créé
     * @param userEmail : l'email du joueur
     * @param categoryId : l'id de la categorie
     * @return Score
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public Score getScoreByUserEmailAndCategoryId(String userEmail, long categoryId){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(ScoreScript.selectByUserIdAndCategoryIdQuery(userEmail, categoryId), null);
        if(cursor.moveToFirst()){
            Optional<Score> maybeScore = this.getUserByEmail(cursor.getString(1))
                    .map(user -> new Score(
                            cursor.getLong(0),      //ID
                            user, //USER
                            this.getCategoryById(cursor.getLong(2)), //CATEGORY
                            cursor.getInt(3)       //SCORE
                    ));
            cursor.close();
            return maybeScore.orElseThrow(() -> new SQLException("Impossible to get Score"));
        }
        //Crée un score initialisé à 0
        //Recupère l'id max de la table score
        Optional<Score> maybeScore = this.getUserByEmail(cursor.getString(1))
                .map(user -> {
                    long id = this.getMaxScoreId();
                    return new Score(
                            ++id,      //ID
                            user, //USER
                            this.getCategoryById(categoryId), //CATEGORY
                            0      //SCORE
                    );
                });
        cursor.close();
        Score score = maybeScore.orElseThrow(() -> new SQLException("Impossible to get Score"));
        this.addScore(score);
        return score;
    }

    /**
     * Create default Categories
     */
    public void createDefaultCategoriesIfNeeded(){
        if(this.getCategoriesCount() == 0){
            this.addCategory(new Category(1, App.getContext().getResources().getString(R.string.categorie1)));
            this.addCategory(new Category(2, App.getContext().getResources().getString(R.string.categorie2)));
        }
    }

    /**
     * Create default questions
     */
    public void createDefaultQuestionsIfNeeded() {
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
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void createDefaultUsersIfNeeded(){
        if(this.getUserCount() == 0){
            this.addUser(new User("admin@email.fr","admin", "admin", true));
            this.addUser(new User("user@email.fr","user", "user", false));
        }
    }

    public long getMaxScoreId(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(ScoreScript.selectMaxId(), null);
        cursor.moveToFirst();
        long id = cursor.getLong(0);
        cursor.close();
        return id;
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

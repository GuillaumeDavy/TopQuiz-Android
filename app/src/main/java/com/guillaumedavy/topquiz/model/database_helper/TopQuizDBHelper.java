package com.guillaumedavy.topquiz.model.database_helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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
    private static final int DATABASE_VERSION = 23;
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
        try (SQLiteDatabase db = this.getWritableDatabase()) {
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
        }
    }

    /**
     * retroune la liste de toute sles questions disponibles
     * @return All the questions
     */
    public List<Question> getAllQuestions() {
        List<Question> questionList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        try (Cursor cursor = db.rawQuery(QuestionScript.selectAllQuery(), null)) {
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
                    questionList.add(question);
                } while (cursor.moveToNext());
            }
        }
        return questionList;
    }

    /**
     * Retourne la liste des questions pour une category
     * @param category la category voulu
     * @return toutes les questions de cette category
     */
    public List<Question> getQuestionsForCategory(Category category){
        List<Question> questionList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        try (Cursor cursor = db.rawQuery(QuestionScript.selectAllQuestionForCategoryId(category.getId()), null)) {
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
                    questionList.add(question);
                } while (cursor.moveToNext());
            }
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
        try (Cursor cursor = db.rawQuery(UserScript.selectAllQuery(), null)) {
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
        if(maybeUser.isPresent()){
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

        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(CategorieScript.COLUMN_CATEGORIE_NAME, category.getName());

            db.insert(CategorieScript.TABLE_NAME, null, values);
        }
    }

    /**
     * retroune la liste de toute sles questions disponibles
     * @return All the questions
     */
    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        try (Cursor cursor = db.rawQuery(CategorieScript.selectAllQuery(), null)) {
            if (cursor.moveToFirst()) {
                do {
                    Category category = new Category(
                            cursor.getLong(0),      //ID
                            cursor.getString(1)     //NAME
                    );

                    categoryList.add(category);
                } while (cursor.moveToNext());
            }
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
     * Retourne la category correspondant au nom
     * @param name nom de la category
     * @return la category
     */
    public Category getCategoryByName(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(CategorieScript.selectCategoryByName(name), null);

        if (cursor.moveToFirst()) {
            Category category = new Category(
                    cursor.getLong(0),      //ID
                    cursor.getString(1)     //NAME
            );
            cursor.close();
            return category;
        }
        cursor.close();
        throw new SQLException("No match for category name=" + name);
    }

    /**
     * Insert a score into the database
     * @param score : Objet Score
     */
    private void addScore(Score score) {

        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(ScoreScript.COLUMN_SCORE_USER_EMAIL, score.getUser().getEmail());
            values.put(ScoreScript.COLUMN_SCORE_CATEGORYID, score.getCategory().getId());
            values.put(ScoreScript.COLUMN_SCORE_VALUE, score.getScore());

            db.insert(ScoreScript.TABLE_NAME, null, values);
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

        try (Cursor cursor = db.rawQuery(ScoreScript.selectAllQuery(), null)) {
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
    public List<Score> getTop3ScoreByCategoryId(long id){
        List<Score> scoreList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        try (Cursor cursor = db.rawQuery(ScoreScript.selectByCategoryIdQuery(id), null)) {
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
                            cursor.getLong(0),                          //ID
                            user,                                                   //USER
                            this.getCategoryById(cursor.getLong(2)),    //CATEGORY
                            cursor.getInt(3)                            //SCORE
                    ));
            cursor.close();
            return maybeScore.orElseThrow(() -> new SQLException("Impossible to get Score"));
        }
        //Crée un score initialisé à 0
        //Recupère l'id max de la table score
        Optional<Score> maybeScore = this.getUserByEmail(userEmail)
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
    @RequiresApi(api = Build.VERSION_CODES.N)
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
                    "Who is the creator of Android",
                    "Andy Rubin",
                    "Steve Wozniak",
                    "Jake Wharton",
                    "Paul Smith",
                    0
            );
            Question question2 = new Question(
                    2,
                    cultureGenerale,
                    "When did the first man land on the moon",
                    "1958",
                    "1962",
                    "Never",
                    "1969",
                    3
            );
            Question question3 = new Question(
                    3,
                    cultureGenerale,
                    "What is the house number of the Simpsons ?",
                    "42",
                    "101",
                    "666",
                    "742",
                    3
            );
            Question question4 = new Question(
                    4,
                    cultureGenerale,
                    "What is the biggest animal in the world ?",
                    "The bolivian anaconda",
                    "The pygmy marmosets",
                    "The elephant",
                    "The blue whale",
                    3
            );
            Question question5 = new Question(
                    5,
                    cultureGenerale,
                    "Who is the richest man on earth in 2021?",
                    "Jeff Bezos",
                    "Elon Musk",
                    "Mukesh Ambani",
                    "Gautam Adani",
                    0
            );
            Question question6 = new Question(
                    6,
                    cultureGenerale,
                    "How much is 284 in roman numbers",
                    "CCLXXXIV",
                    "CCXVVVIIII",
                    "CICIIV",
                    "IICVVVIV",
                    0
            );
            Question question7 = new Question(
                    7,
                    cultureGenerale,
                    "Camberra is a city of which country ?",
                    "USA",
                    "Chili",
                    "Australia",
                    "Greece",
                    2
            );
            Question question8 = new Question(
                    8,
                    cultureGenerale,
                    "How many species are threatened with extinction ?",
                    "3.178",
                    "14.403",
                    "8.290",
                    "16.306",
                    3
            );
            Question question9 = new Question(
                    9,
                    cultureGenerale,
                    "How deep is the Challenger Deep ?",
                    "11.030 m",
                    "3.880 m",
                    "22.392 m",
                    "17.256 m",
                    0
            );
            Question question10 = new Question(
                    10,
                    cultureGenerale,
                    "How many years has earth not been in war over the last 3.421 years ?",
                    "2.201",
                    "3.324",
                    "1.285",
                    "268",
                    0
            );
            Question question11 = new Question(
                    11,
                    sport,
                    "What is the periodicity of the olympic games?",
                    "1 year",
                    "2 years",
                    "3 years",
                    "4 years",
                    3
            );
            Question question12 = new Question(
                    12,
                    sport,
                    "Where did the Summer Olympics take place in 2016?",
                    "Rio de Janeiro",
                    "Beijing",
                    "Paris",
                    "Tokyo",
                    1
            );
            Question question13 = new Question(
                    13,
                    sport,
                    "In which sport are the following terms used: split, spare, strike?",
                    "Petanque",
                    "Pool",
                    "Bowling",
                    "Volley-ball",
                    2
            );
            Question question14 = new Question(
                    14,
                    sport,
                    "In judo, what is the highest rank among these belts?",
                    "Orange",
                    "Blue",
                    "Green",
                    "Yellow",
                    1
            );
            Question question15 = new Question(
                    15,
                    sport,
                    "In rugby, which country does not participate in the Six Nations tournament?",
                    "Spain",
                    "England",
                    "France",
                    "Italie",
                    0
            );
            Question question16 = new Question(
                    16,
                    sport,
                    "In American Football how many points is a touchdown worth?",
                    "4",
                    "5",
                    "6",
                    "7",
                    2
            );
            Question question17 = new Question(
                    17,
                    sport,
                    "What is the official distance of a marathon?",
                    "42 km",
                    "42.195 km",
                    "42.295 km",
                    "42.395 km",
                    1
            );
            Question question18 = new Question(
                    18,
                    sport,
                    "How many times has France organized the Winter Olympics?",
                    "2",
                    "3",
                    "4",
                    "5",
                    1
            );
            Question question19 = new Question(
                    19,
                    sport,
                    "When did the first Soccer World Cup take place?",
                    "1922",
                    "1926",
                    "1930",
                    "1934",
                    2
            );
            Question question20 = new Question(
                    20,
                    sport,
                    "How many motorcycle titles has Valentino Rossi won?",
                    "6",
                    "7",
                    "8",
                    "9",
                    3
            );
            this.addQuestion(question1);
            this.addQuestion(question2);
            this.addQuestion(question3);
            this.addQuestion(question4);
            this.addQuestion(question5);
            this.addQuestion(question6);
            this.addQuestion(question7);
            this.addQuestion(question8);
            this.addQuestion(question9);
            this.addQuestion(question10);
            this.addQuestion(question11);
            this.addQuestion(question12);
            this.addQuestion(question13);
            this.addQuestion(question14);
            this.addQuestion(question15);
            this.addQuestion(question16);
            this.addQuestion(question17);
            this.addQuestion(question18);
            this.addQuestion(question19);
            this.addQuestion(question20);
        }
    }

    /**
     * Create one admin and one user
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void createDefaultUsersIfNeeded(){
        if(this.getUserCount() == 0){
            this.addUser(new User("admin@email.fr","Admin", "admin", true));
            this.addUser(new User("matthias@email.fr","Matthias", "matthias", false));
            this.addUser(new User("alexandre@email.fr","Alexandre", "alexandre", false));
            this.addUser(new User("guillaume@email.fr","Guillaume", "guillaume", false));
        }
    }

    //TODO remove if not demo
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void createDefaultScoreForDemo(){
        if(this.getAllScores().size() == 0){
            User matthias = getUserByEmail("matthias@email.fr").get();
            User alexandre = getUserByEmail("alexandre@email.fr").get();
            User guillaume = getUserByEmail("guillaume@email.fr").get();
            Category sport = getCategoryById(2);
            System.out.println("hello " + sport);
            this.addScore(new Score(0, matthias, sport, 4));
            this.addScore(new Score(1, alexandre, sport, 3));
            this.addScore(new Score(2, guillaume, sport, 1));
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

    public long getMaxQuestionId(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(QuestionScript.selectMaxId(), null);
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

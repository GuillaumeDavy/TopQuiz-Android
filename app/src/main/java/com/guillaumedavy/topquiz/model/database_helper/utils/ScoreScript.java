package com.guillaumedavy.topquiz.model.database_helper.utils;

import com.guillaumedavy.topquiz.model.Score;

public class ScoreScript {
    public static final String TABLE_NAME = "SCORE";
    public static final String COLUMN_SCORE_ID ="id";
    public static final String COLUMN_SCORE_USERID ="user_id";
    public static final String COLUMN_SCORE_CATEGORYID ="category_id";
    public static final String COLUMN_SCORE_VALUE ="score_value";

    public static final String createTableQuery(){
        return "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_SCORE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_SCORE_USERID + " INTEGER, "
                + COLUMN_SCORE_CATEGORYID + " INTEGER, "
                + COLUMN_SCORE_VALUE + " INTEGER "
                + ")";
    }

    public static final String selectAllQuery(){
        return "SELECT  * FROM " + TABLE_NAME;
    }

    /**
     * Requete SQL qui permet de recuperer tous les scores d'un utilisateur
     * @param id : l'id du joueur
     * @return la requete SQL
     */
    public static final String selectByUserIdQuery(long id){
        return "SELECT  * FROM " + TABLE_NAME
                + " WHERE " + COLUMN_SCORE_USERID + "=" + id;
    }

    /**
     * Requete SQL qui permet de recuperer tous les scores d'une categorie
     * @param id : l'id de la categorie
     * @return la requete SQL
     */
    public static final String selectByCategoryIdQuery(long id){
        return "SELECT  * FROM " + TABLE_NAME
                + " WHERE " + COLUMN_SCORE_CATEGORYID + "=" + id;
    }

    /**
     * Requete SQL qui permet de recuperer le score d'un joueur sur une categorie
     * @param userId : l'id du joueur
     * @param categoryId : l'id de la categorie
     * @return la requete SQL
     */
    public static final String selectByUserIdAndCategoryIdQuery(long userId, long categoryId){
        return "SELECT  * FROM " + TABLE_NAME
                + " WHERE " + COLUMN_SCORE_USERID + "=" + userId
                + " AND " + COLUMN_SCORE_CATEGORYID + "=" + categoryId;

    }

    /**
     * Requete SQL qui permet de récupérer l'id max de la table
     * @return La requete SQL
     */
    public static final String selectMaxId(){
        return "SELECT  MAX(" + COLUMN_SCORE_ID + ") FROM " + TABLE_NAME;
    }

    public static final String countAllQuery(){
        return "SELECT  * FROM " + TABLE_NAME;
    }

    public static final String dropTableQuery(){
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

}

package com.guillaumedavy.topquiz.model;

import java.io.Serializable;

public class Score implements Serializable {
    private final long mId;
    private final User mUser;
    private final Category mCategory;
    private int mScore;

    /**
     * Constructeur sans le score, le met à 0 par default
     * @param id : l'id du score
     * @param user : le joueur a qui appartient le score
     * @param category : la categorie pour laquel le joueur à fait ce score
     */
    public Score(long id, User user, Category category) {
        mId = id;
        mUser = user;
        mCategory = category;
        mScore = 0;
    }

    /**
     * Constructeur
     * @param id : l'id du score
     * @param user : le joueur a qui appartient le score
     * @param category : la categorie pour laquel le joueur à fait ce score
     * @param score : le score réalisé par le joueur
     */
    public Score(long id, User user, Category category, int score) {
        mId = id;
        mUser = user;
        mCategory = category;
        mScore = score;
    }

    /**
     * Permet de récuperer l'id du score
     * @return l'id du score
     */
    public long getId() {
        return mId;
    }

    /**
     * Permet de recuperer le joueur ayant realiser ce score
     * @return le joueur
     */
    public User getUser() {
        return mUser;
    }

    /**
     * Permet de récuperer la categorie
     * @return la categorie
     */
    public Category getCategory() {
        return mCategory;
    }

    /**
     * Permet de recuperer le score du joueur sur la categorie
     * @return le score
     */
    public int getScore() {
        return mScore;
    }

    /**
     * Permet de modifier le score du joueur
     * @param score le nouveau score
     */
    public void setScore(int score) {
        mScore = score;
    }

    @Override
    public String toString() {
        return "Score{" +
                "mId=" + mId +
                ", mUser=" + mUser +
                ", mCategory=" + mCategory +
                ", mScore=" + mScore +
                '}';
    }
}

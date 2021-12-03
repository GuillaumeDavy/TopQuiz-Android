package com.guillaumedavy.topquiz.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Player implements Parcelable {
    private String mUserEmail;
    private int mScore;

    /**
     * Constructeur par default
     */
    public Player(){
        mUserEmail = "defaultUsername";
        mScore = 0;
    }

    /**
     * Constructeur
     * @param username : le nom d'utilisateur
     * @param score : le score du joueur
     */
    public Player(String username, int score) {
        mUserEmail = username;
        mScore = score;
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    /**
     * Permet de recuperer l'email du joueur
     * @return l'email du joueur
     */
    public String getUserEmail() {
        return mUserEmail;
    }

    /**
     * Permet de changer l'email du joueur
     * @param userEmail
     */
    public void setUserEmail(String userEmail) {
        mUserEmail = userEmail;
    }

    /**
     * Permet de recupere le score du joueur
     * @return le score actuel du joueur
     */
    public int getScore() {
        return mScore;
    }

    /**
     * Permet de changer le score
     * @param score
     */
    public void setScore(int score) {
        mScore = score;
    }

    /**
     * Permet d'incrementer le score de 1 si bonne reponse.
     */
    public void incrementScoreByOne(){
        mScore++;
    }

    /**
     * Reinitialise le score a 0
     */
    public void resetScore(){
        mScore = 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUserEmail);
        dest.writeInt(mScore);
    }

    /**
     * Constructeur privé pour le Parcelable
     * @param in un Parcelable
     */
    private Player(Parcel in) {
        mUserEmail = in.readString();
        mScore = in.readInt();
    }

    @Override
    public String toString() {
        return "Player{" +
                "mUserEmail='" + mUserEmail + '\'' +
                ", mScore=" + mScore +
                '}';
    }
}

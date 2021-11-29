package com.guillaumedavy.topquiz.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String mFirstname;
    private int mScore;

    /**
     * Constructeur par défault
     * firstname vide et score = 0
     */
    public User(){
        mFirstname = "";
        mScore = 0;
    }

    /**
     * Constructeur
     * @param firstname : le nom du joueur (String)
     * @param score : le score du joueur (int)
     */
    public User(String firstname, int score){
        this.mFirstname = firstname;
        this.mScore = score;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mScore);
        dest.writeString(mFirstname);
    }

    /**
     * Renvoie le nom du joueur
     * @return firstName
     */
    public String getFirstname() {
        return mFirstname;
    }

    /**
     * Permet de changer le nom du joueur
     * @param firstname : le nouveau nom du joueur (String)
     */
    public void setFirstname(String firstname) {
        mFirstname = firstname;
    }

    /**
     * Permet de récupérer le score actuel du joueur.
     * @return score
     */
    public int getScore() {
        return mScore;
    }

    /**
     * Permet de changer le score du joueur
     * @param score : le nouveau score (int)
     */
    public void setScore(int score) {
        this.mScore = score;
    }

    /**
     * Increment player score by one
     */
    public void incrementScoreByOne(){
        mScore++;
    }

    /**
     * Constructeur privé permettant de construire un User depuis un Parcel
     * @param in : un Parcel
     */
    private User(Parcel in) {
        mFirstname = in.readString();
        mScore = in.readInt();
    }

    @Override
    public String toString() {
        return "User{" +
                "mFirstname='" + mFirstname + '\'' +
                ", mScore=" + mScore +
                '}';
    }
}

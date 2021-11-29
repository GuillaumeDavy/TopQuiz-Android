package com.guillaumedavy.topquiz.model;

import java.io.Serializable;

public class User implements Serializable {
    private final long mId;
    private final String mUsername;
    private final String mPassword;
    private final String mEmail;
    private final boolean mIsAdmin;

    /**
     * Constructeur
     * @param id : l'id du joueur
     * @param username : le pseudo du joueur
     * @param password : le mot de passe du joueur
     * @param email : l'email du joueur (login)
     * @param isAdmin : le joueur est-il admin ?
     */
    public User(long id, String username, String password, String email, boolean isAdmin) {
        mId = id;
        mUsername = username;
        mPassword = password;
        mEmail = email;
        mIsAdmin = isAdmin;
    }

    /**
     * Permet de recuperer l'id de l'utilisateur
     * @return l'id du joueur
     */
    public long getId() {
        return mId;
    }

    /**
     * Permet de recuperer le username de l'utilisateur
     * @return le username du joueur
     */
    public String getUsername() {
        return mUsername;
    }

    /**
     * Permet de recuperer le mot de passe du joueur
     * @return le mot de passe du joueur
     */
    public String getPassword() {
        return mPassword;
    }

    /**
     * Permet de recuperer l'email du joueur
     * @return l'email du joueur (login)
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * Permet de recuperer le status du joueur, admin ou non
     * @return true ou false si admin
     */
    public boolean isAdmin() {
        return mIsAdmin;
    }

    @Override
    public String toString() {
        return "User{" +
                "mId=" + mId +
                ", mUsername='" + mUsername + '\'' +
                ", mPassword='" + mPassword + '\'' +
                ", mEmail='" + mEmail + '\'' +
                ", mIsAdmin=" + mIsAdmin +
                '}';
    }
}

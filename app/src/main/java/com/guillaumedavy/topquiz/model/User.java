package com.guillaumedavy.topquiz.model;

import java.io.Serializable;

public class User implements Serializable {
    private final String mEmail;
    private final String mUsername;
    private final String mPassword;
    private final boolean mIsAdmin;

    /**
     * Constructeur
     * @param email : l'email du joueur (login), il est unique
     * @param username : le pseudo du joueur
     * @param password : le mot de passe du joueur
     * @param isAdmin : le joueur est-il admin ?
     */
    public User(String email, String username, String password, boolean isAdmin) {
        mEmail = email;
        mUsername = username;
        mPassword = password;
        mIsAdmin = isAdmin;
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
                "mEmail='" + mEmail + '\'' +
                ", mUsername='" + mUsername + '\'' +
                ", mPassword='" + mPassword + '\'' +
                ", mIsAdmin=" + mIsAdmin +
                '}';
    }
}

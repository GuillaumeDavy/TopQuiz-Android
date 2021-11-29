package com.guillaumedavy.topquiz.model;

import java.io.Serializable;

public class Category implements Serializable {
    private final long mId;
    private final String mName;

    /**
     * Constructeur
     * @param id : l'id de la category
     * @param name : le nom de la categorie
     */
    public Category(long id, String name) {
        mId = id;
        mName = name;
    }

    public long getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }
}

package com.guillaumedavy.topquiz.model;

import java.io.Serializable;

public class Question implements Serializable {
    private final long mId;
    private final Category mCategory;
    private final String mQuestion;
    private final String mAnswer1;
    private final String mAnswer2;
    private final String mAnswer3;
    private final String mAnswer4;
    private final int mAnswerIndex;

    /**
     * Constructeur avec tous les champs
     * @param id : id de la question (devra etre generé par la base de donnee)
     * @param category : category de la question (Sport, Culture, Histoire etc...)
     * @param question : la question
     * @param answer1 : la premiere reponse
     * @param answer2 : la deuxieme reponse
     * @param answer3 : la troisieme reponse
     * @param answer4 : la quatrieme reponse
     * @param answerIndex : l'index de la reponse correcte
     */
    public Question(long id, Category category, String question, String answer1, String answer2, String answer3, String answer4, int answerIndex) {
        mId = id;
        mCategory = category;
        mQuestion = question;
        mAnswer1 = answer1;
        mAnswer2 = answer2;
        mAnswer3 = answer3;
        mAnswer4 = answer4;
        mAnswerIndex = answerIndex;
    }

    public long getId() {
        return mId;
    }

    public Category getCategory() {
        return mCategory;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public String getAnswer1() {
        return mAnswer1;
    }

    public String getAnswer2() {
        return mAnswer2;
    }

    public String getAnswer3() {
        return mAnswer3;
    }

    public String getAnswer4() {
        return mAnswer4;
    }

    public int getAnswerIndex() {
        return mAnswerIndex;
    }

    @Override
    public String toString() {
        return "Question{" +
                "mId=" + mId +
                ", mCategory=" + mCategory +
                ", mQuestion='" + mQuestion + '\'' +
                ", mAnswer1='" + mAnswer1 + '\'' +
                ", mAnswer2='" + mAnswer2 + '\'' +
                ", mAnswer3='" + mAnswer3 + '\'' +
                ", mAnswer4='" + mAnswer4 + '\'' +
                ", mAnswerIndex=" + mAnswerIndex +
                '}';
    }
}

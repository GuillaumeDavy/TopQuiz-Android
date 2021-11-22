package com.guillaumedavy.topquiz.model;

import java.util.List;

public class Question {
    private final String mQuestion;
    private final List<String> mChoiceList;
    private final int mAnswerIndex;

    /**
     * Constructeur
     * @param question : la question
     * @param choiceList : les réponses
     * @param answerIndex : la réponse correcte
     */
    public Question(String question, List<String> choiceList, int answerIndex) {
        mQuestion = question;
        mChoiceList = choiceList;
        mAnswerIndex = answerIndex;
    }

    /**
     * Permet de récupérer la question
     * @return la Question (String)
     */
    public String getQuestion() {
        return mQuestion;
    }

    /**
     * Permet de récupérer les réponses.
     * @return les réponses (List(String))
     */
    public List<String> getChoiceList() {
        return mChoiceList;
    }

    /**
     * Permet de récupérer l'index de la réponse correcte
     * @return l'index de la bonne réponse(int)
     */
    public int getAnswerIndex() {
        return mAnswerIndex;
    }
}

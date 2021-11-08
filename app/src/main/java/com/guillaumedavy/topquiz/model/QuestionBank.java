package com.guillaumedavy.topquiz.model;

import java.util.Collections;
import java.util.List;

public class QuestionBank {
    List<Question> mQuestionList;
    private int mNextQuestionIndex = 0;

    public QuestionBank(List<Question> questionList) {
        mQuestionList = questionList;
        Collections.shuffle(mQuestionList);
    }

    public Question getCurrentQuestion(){
        return mQuestionList.get(mNextQuestionIndex);
    }

    public Question getNextQuestion() {
        mNextQuestionIndex++;
        return getCurrentQuestion();
    }
}

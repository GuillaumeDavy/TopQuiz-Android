package com.guillaumedavy.topquiz.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionBank implements Parcelable {

    List<Question> mQuestionList;
    private int mCurrentQuestionIndex = 0;

    /**
     * Constructeur
     * @param questionList : la liste de questions
     */
    public QuestionBank(List<Question> questionList) {
        mQuestionList = questionList;
        Collections.shuffle(mQuestionList);
    }

    /**
     * Creation d'un QuestionBank depuis un Parcel
     */
    public static final Creator<QuestionBank> CREATOR = new Creator<QuestionBank>() {
        @Override
        public QuestionBank createFromParcel(Parcel in) {
            return new QuestionBank(in);
        }

        @Override
        public QuestionBank[] newArray(int size) {
            return new QuestionBank[size];
        }
    };

    /**
     * Renvoie la question actuelle dans la banque
     * @return une Question
     */
    public Question getCurrentQuestion(){
        return mQuestionList.get(mCurrentQuestionIndex);
    }

    /**
     * Renvoie la question suivante de la banque
     * @return une Question
     */
    public Question getNextQuestion() {
        mCurrentQuestionIndex++;
        return getCurrentQuestion();
    }

    /**
     * Modifie l'index de la current question de la banque
     * @param nextQuestionIndex : l'index de la currentQuestion
     */
    public void setNextQuestionIndex(int nextQuestionIndex){
        this.mCurrentQuestionIndex = nextQuestionIndex;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(mQuestionList);
        dest.writeInt(mCurrentQuestionIndex);
    }

    /**
     * Constructeur privé créant une QuestionBank a partir d'un Parcel.
     * @param in : un Parcel
     */
    private QuestionBank(Parcel in) {
        mQuestionList = new ArrayList<>();
        in.readList(mQuestionList, Question.class.getClassLoader());
        mCurrentQuestionIndex = in.readInt();
    }

    @Override
    public String toString() {
        return "QuestionBank{" +
                "mQuestionList=" + mQuestionList +
                ", mCurrentQuestionIndex=" + mCurrentQuestionIndex +
                '}';
    }
}
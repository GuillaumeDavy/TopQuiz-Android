package com.guillaumedavy.topquiz.controller;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.guillaumedavy.topquiz.R;
import com.guillaumedavy.topquiz.model.Player;
import com.guillaumedavy.topquiz.model.Question;
import com.guillaumedavy.topquiz.model.QuestionBank;
import com.guillaumedavy.topquiz.model.User;
import com.guillaumedavy.topquiz.model.database_helper.TopQuizDBHelper;

import java.util.Arrays;
import java.util.List;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String USER = "USER";
    public static final String CATEGORY = "CATEGORY";
    public static final int NUMBER_OF_QUESTIONS = 4;
    public static final String BUNDLE_STATE_SCORE = "BUNDLE_STATE_SCORE";
    public static final String BUNDLE_STATE_QUESTION_COUNT = "BUNDLE_STATE_QUESTION_COUNT";
    public static final String BUNDLE_STATE_QUESTION_BANK = "BUNDLE_STATE_QUESTION_BANK";

    private TextView mQuestionTextView;
    private Button mResponseOneButton;
    private Button mResponseTwoButton;
    private Button mResponseThreeButton;
    private Button mResponseFourButton;

    //Game attributs
    private QuestionBank mQuestionBank;
    private int mRemainingQuestionCount;
    private boolean mEnableTouchEvents;
    private Player mUser;
    private String mCategory;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mEnableTouchEvents && super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(BUNDLE_STATE_SCORE, mUser.getScore());
        outState.putInt(BUNDLE_STATE_QUESTION_COUNT, mRemainingQuestionCount);
        outState.putParcelable(BUNDLE_STATE_QUESTION_BANK, mQuestionBank);
        super.onSaveInstanceState(outState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        if(intent.hasExtra(USER) && intent.hasExtra(CATEGORY)){
            mUser = intent.getParcelableExtra(USER);
            mCategory = intent.getStringExtra(CATEGORY);
            System.out.println("Game --> " + mUser + ", Category : " + mCategory);
        }

        if(savedInstanceState != null){
            mUser.setScore(savedInstanceState.getInt(BUNDLE_STATE_SCORE));
            mRemainingQuestionCount = savedInstanceState.getInt(BUNDLE_STATE_QUESTION_COUNT);
            mQuestionBank = savedInstanceState.getParcelable(BUNDLE_STATE_QUESTION_BANK);
            mQuestionBank.setNextQuestionIndex(NUMBER_OF_QUESTIONS - mRemainingQuestionCount);
        } else {
            //Game initialisation
            mQuestionBank = generateQuestionBank();
            mRemainingQuestionCount = NUMBER_OF_QUESTIONS;
        }

        mQuestionTextView = findViewById(R.id.game_activity_textview_question);
        mResponseOneButton = findViewById(R.id.game_activity_button_1);
        mResponseTwoButton = findViewById(R.id.game_activity_button_2);
        mResponseThreeButton = findViewById(R.id.game_activity_button_3);
        mResponseFourButton = findViewById(R.id.game_activity_button_4);

        mResponseOneButton.setOnClickListener(this);
        mResponseTwoButton.setOnClickListener(this);
        mResponseThreeButton.setOnClickListener(this);
        mResponseFourButton.setOnClickListener(this);

        mEnableTouchEvents = true;

        displayQuestion(mQuestionBank.getCurrentQuestion());
    }

    @Override
    public void onClick(View v) {
        int index;
        //Recupere l'id du bouton sur lequel le joueur a clique
        if(v == mResponseOneButton) index = 0;
        else if(v == mResponseTwoButton) index = 1;
        else if(v == mResponseThreeButton) index = 2;
        else if(v == mResponseFourButton) index = 3;
        else throw new IllegalStateException("Unknown clicked view : " + v);

        //Verifie la reponse et affiche le resultat
        if(index == mQuestionBank.getCurrentQuestion().getAnswerIndex()){
            Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show();
            //Ajoute un point au score du joueur
            mUser.incrementScoreByOne();
        } else {
            Toast.makeText(this, "Incorrect", Toast.LENGTH_SHORT).show();
        }

        mEnableTouchEvents = false;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Question suivante ou fin de jeu
                mRemainingQuestionCount--;
                if(mRemainingQuestionCount > 0){
                    displayQuestion(mQuestionBank.getNextQuestion());
                } else {
                    //fin de la session de jeu
                    endGame();
                }
                mEnableTouchEvents = true;
            }
        }, 2000);
    }

    private void displayQuestion(final Question question){
        mQuestionTextView.setText(question.getQuestion());
        mResponseOneButton.setText(question.getAnswer1());
        mResponseTwoButton.setText(question.getAnswer2());
        mResponseThreeButton.setText(question.getAnswer3());
        mResponseFourButton.setText(question.getAnswer4());
    }

    /**
     * Affichage de fin de jeu et retour vers SelectCategoryActivity
     */
    private void endGame(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.score_title_label))
                .setMessage(getString(R.string.score_content_label) + " " + mUser.getScore())
                .setPositiveButton(getString(R.string.score_button_label), (dialog, which) -> {
                    Intent SelectCategoryActivity = new Intent(GameActivity.this, SelectCategoryActivity.class);
                    SelectCategoryActivity.putExtra(USER, mUser);
                    setResult(RESULT_OK, SelectCategoryActivity);
                    finish();
                })
                .create()
                .show();
    }

    /**
     * Permet la génération de questions pour le jeu
     * @return la banque de questions
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private QuestionBank generateQuestionBank(){
        //Database
        TopQuizDBHelper db = new TopQuizDBHelper(this);
        db.getWritableDatabase();
        db.createDefaultQuestionsIfNeeded();
        db.getAllCategories().forEach(System.out::println);
        return new QuestionBank(db.getAllQuestions());
    }
}
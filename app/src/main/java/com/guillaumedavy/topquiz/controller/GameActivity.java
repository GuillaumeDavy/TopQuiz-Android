package com.guillaumedavy.topquiz.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.guillaumedavy.topquiz.R;
import com.guillaumedavy.topquiz.model.Question;
import com.guillaumedavy.topquiz.model.QuestionBank;

import java.util.Arrays;
import java.util.List;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int NUMBER_OF_QUESTIONS = 4;
    public static final String BUNDLE_EXTRA_SCORE = "BUNDLE_EXTRA_SCORE";
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
    private int mCurrentScore = 0;
    private boolean mEnableTouchEvents;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mEnableTouchEvents && super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(BUNDLE_STATE_SCORE, mCurrentScore);
        outState.putInt(BUNDLE_STATE_QUESTION_COUNT, mRemainingQuestionCount);
        outState.putParcelable(BUNDLE_STATE_QUESTION_BANK, mQuestionBank);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        if(savedInstanceState != null){
            mCurrentScore = savedInstanceState.getInt(BUNDLE_STATE_SCORE);
            mRemainingQuestionCount = savedInstanceState.getInt(BUNDLE_STATE_QUESTION_COUNT);
            mQuestionBank = savedInstanceState.getParcelable(BUNDLE_STATE_QUESTION_BANK);
            mQuestionBank.setNextQuestionIndex(NUMBER_OF_QUESTIONS - mRemainingQuestionCount);

        } else {
            //Game initialisation
            mQuestionBank = generateQuestionBank();
            mRemainingQuestionCount = NUMBER_OF_QUESTIONS;
            mCurrentScore = 0;
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
            mCurrentScore++;
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
        mResponseOneButton.setText(question.getChoiceList().get(0));
        mResponseTwoButton.setText(question.getChoiceList().get(1));
        mResponseThreeButton.setText(question.getChoiceList().get(2));
        mResponseFourButton.setText(question.getChoiceList().get(3));
    }

    /**
     * Affichage de fin de jeu et retour vers mainActivity
     */
    private void endGame(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.score_title_label))
                .setMessage(getString(R.string.score_content_label) + " " + mCurrentScore)
                .setPositiveButton(getString(R.string.score_button_label), (dialog, which) -> {
                    Intent intent = new Intent();
                    intent.putExtra(BUNDLE_EXTRA_SCORE, mCurrentScore);
                    setResult(RESULT_OK, intent);
                    finish();
                })
                .create()
                .show();
    }

    /**
     * Permet la génération de questions pour le jeu
     * @return la banque de questions
     */
    private QuestionBank generateQuestionBank(){
        Question question1 = new Question(
                getString(R.string.question1),
                Arrays.asList(
                        getString(R.string.response11),
                        getString(R.string.response12),
                        getString(R.string.response13),
                        getString(R.string.response14)
                ),
                0
        );

        Question question2 = new Question(
                getString(R.string.question2),
                Arrays.asList(
                        getString(R.string.response21),
                        getString(R.string.response22),
                        getString(R.string.response23),
                        getString(R.string.response24)
                ),
                3
        );

        Question question3 = new Question(
                getString(R.string.question3),
                Arrays.asList(
                        getString(R.string.response31),
                        getString(R.string.response32),
                        getString(R.string.response33),
                        getString(R.string.response34)
                ),
                3
        );

        Question question4 = new Question(
                getString(R.string.question4),
                Arrays.asList(
                        getString(R.string.response41),
                        getString(R.string.response42),
                        getString(R.string.response43),
                        getString(R.string.response44)
                ),
                2
        );

        Question question5 = new Question(
                getString(R.string.question5),
                Arrays.asList(
                        getString(R.string.response51),
                        getString(R.string.response52),
                        getString(R.string.response53),
                        getString(R.string.response54)
                ),
                2
        );

        Question question6 = new Question(
                getString(R.string.question6),
                Arrays.asList(
                        getString(R.string.response61),
                        getString(R.string.response62),
                        getString(R.string.response63),
                        getString(R.string.response64)
                ),
                1
        );

        return new QuestionBank(Arrays.asList(
                question1,
                question2,
                question3,
                question4,
                question5,
                question6
        ));
    }
}
package com.guillaumedavy.topquiz.controller;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.guillaumedavy.topquiz.R;
import com.guillaumedavy.topquiz.model.User;

public class MainActivity extends AppCompatActivity {

    private static final int GAME_ACTIVITY_REQUEST_CODE = 42;
    private static final String SHARED_PREF_USER_INFO = "SHARED_PREF_USER_INFO";
    private static final String SHARED_PREF_USER_INFO_NAME = "SHARED_PREF_USER_INFO_NAME";
    private static final String SHARED_PREF_USER_INFO_SCORE = "SHARED_PREF_USER_INFO_SCORE";

    //Les élèments de la vue
    private TextView mGreetingTextView;
    private EditText mNameEditText;
    private Button mPlayButton;
    //Attributs
    private User mUser = new User();

    /**
     * Est appelée lorsque l'activité est créée
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //Permet de déterminer quel fichier layout utiliser

        //Lier les elements avec les id definis dans la vue
        mGreetingTextView = findViewById(R.id.main_textview_greeting);
        mNameEditText = findViewById(R.id.main_edittext_name);
        mPlayButton = findViewById(R.id.main_button_play);
        mPlayButton.setEnabled(false); //Button désactivé

        displayNameAndScore(
                getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getString(SHARED_PREF_USER_INFO_NAME, null),
                getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getInt(SHARED_PREF_USER_INFO_SCORE, 0)
        );

        //On ajoute un listener sur le TextEdit
        mNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            /**
             * Methode appelée a chaque fois que le text change dans le TextEdit
             * @param s
             */
            @Override
            public void afterTextChanged(Editable s) {
                //Active le button si il y a du text dans le TextEdit
                mPlayButton.setEnabled(!s.toString().isEmpty());
            }
        });
        //On ajoute un listener sur le click du button
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Appelée lorsqu'un clique est réalisé sur le button
             * @param v
             */
            @Override
            public void onClick(View v) {
                //Mémorise le nom
                mUser.setFirstname(mNameEditText.getText().toString());
                //Sauvegarde le nom de l'utilisateur
                getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE)
                        .edit()
                        .putString(SHARED_PREF_USER_INFO_NAME, mUser.getFirstname())
                        .apply();
                //Elle communique avec l'OS android en lui demandant de démarrer une activité
                //Ici, elle créée une instance de la gameactivity
                startActivityForResult(new Intent(MainActivity.this, GameActivity.class), GAME_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(GAME_ACTIVITY_REQUEST_CODE == requestCode && RESULT_OK == resultCode){
            //Fetch the score from the Intent
            getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE)
                    .edit()
                    .putInt(SHARED_PREF_USER_INFO_SCORE, data.getIntExtra(GameActivity.BUNDLE_EXTRA_SCORE, 0))
                    .apply();
            displayNameAndScore(
                    mUser.getFirstname(),
                    data.getIntExtra(GameActivity.BUNDLE_EXTRA_SCORE, 0)
            );
        }
    }

    /**
     * Affiche le nom du joueur et son dernier score
     * @param name : le nom du joueur
     * @param score : son dernier score
     */
    private void displayNameAndScore(String name, int score){
        if(name != null){
            String text = getString(R.string.welcome_back_label) + " " + name + "\n" + getString(R.string.welcome_back_label_score) + " " + score;
            mGreetingTextView.setText(text);
            //Fill le textedit
            mNameEditText.setText(name);
            //Place le curseur a la fin
            mNameEditText.setSelection(name.length());
            //Active le bouton Jouer
            mPlayButton.setEnabled(!name.isEmpty());
        }
    }
}
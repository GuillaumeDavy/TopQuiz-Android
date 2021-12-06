package com.guillaumedavy.topquiz.controller;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.SQLException;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.guillaumedavy.topquiz.R;
import com.guillaumedavy.topquiz.model.Category;
import com.guillaumedavy.topquiz.model.Player;
import com.guillaumedavy.topquiz.model.User;
import com.guillaumedavy.topquiz.model.database_helper.TopQuizDBHelper;

public class MainActivity extends AppCompatActivity {
    private static final int GAME_ACTIVITY_REQUEST_CODE = 42;
    private static final int CREATE_ACCOUNT_REQUEST_CODE = 43;
    private static final String SHARED_PREF_USER_INFO = "SHARED_PREF_USER_INFO";
    private static final String SHARED_PREF_USER_INFO_NAME = "SHARED_PREF_USER_INFO_NAME";
    private static final String SHARED_PREF_USER_INFO_SCORE = "SHARED_PREF_USER_INFO_SCORE";
    public static final String EMAIL = "EMAIL";

    //Les élèments de la vue
    private TextView mGreetingTextView;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mPlayButton;
    private TextView mCreateAccount;
    private TextView mErrorTextView;
    //Attributs
    private Player mPlayer = new Player();

    /**
     * Est appelée lorsque l'activité est créée
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //Permet de déterminer quel fichier layout utiliser

        //Lier les elements avec les id definis dans la vue
        mGreetingTextView = findViewById(R.id.main_textview_greeting);
        mEmailEditText = findViewById(R.id.main_edittext_email);
        mPasswordEditText = findViewById(R.id.main_edittext_password);
        mErrorTextView = findViewById(R.id.main_textview_error);
        mPlayButton = findViewById(R.id.main_button_play);
        mPlayButton.setEnabled(false); //Button désactivé
        mCreateAccount = findViewById(R.id.create_account);

        displayNameAndScore(
                getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getString(SHARED_PREF_USER_INFO_NAME, null),
                getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE).getInt(SHARED_PREF_USER_INFO_SCORE, 0)
        );

        //Create default user and admin
        TopQuizDBHelper db = new TopQuizDBHelper(this);
        db.getWritableDatabase();
        db.createDefaultUsersIfNeeded();
        db.createDefaultCategoriesIfNeeded();

        db.getAllUsers().forEach(System.out::println); //TODO Remove

        //On ajoute un listener sur le TextEdit
        mEmailEditText.addTextChangedListener(new TextWatcher() {
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
        //TODO Check credentials before redirect !!!
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Appelée lorsqu'un clique est réalisé sur le button
             * @param v
             */
            @Override
            public void onClick(View v) {
                String email = mEmailEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                try {
                    //Essaye de se connecter
                    db.tryLogIn(email, password);
                    //Mémorise le nom et met le score par defaut
                    mPlayer.setUserEmail(email);
                    //Creer un Intent pour passer le joueur au game activity
                    Intent gameActivity = new Intent(MainActivity.this, GameActivity.class);
                    System.out.println(mPlayer);
                    mPlayer.resetScore();
                    gameActivity.putExtra(GameActivity.USER, mPlayer);
                    Player user = gameActivity.getParcelableExtra(GameActivity.USER);
                    System.out.println("Main " + user);
                    startActivityForResult(gameActivity, GAME_ACTIVITY_REQUEST_CODE);
                } catch (SQLException e){
                    mErrorTextView.setText(e.getMessage());
                }
            }
        });

        // Listener sur le texte de création de compte
        mCreateAccount.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent createAccountActivity = new Intent(MainActivity.this, CreateAccountActivity.class);
                startActivityForResult(createAccountActivity, CREATE_ACCOUNT_REQUEST_CODE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(CREATE_ACCOUNT_REQUEST_CODE == requestCode && RESULT_OK == resultCode){
            // Fetch the Email from CreateAccountActivity
            mEmailEditText.setText(data.getParcelableExtra(CreateAccountActivity.EMAIL));
        }

        if(GAME_ACTIVITY_REQUEST_CODE == requestCode && RESULT_OK == resultCode){
            //Fetch the score from the Intent
            mPlayer = data.getParcelableExtra(GameActivity.USER);
            System.out.println(mPlayer.toString());
            displayNameAndScore(
                    mPlayer.getUserEmail(),
                    mPlayer.getScore()
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
            mEmailEditText.setText(name);
            //Place le curseur a la fin
            mEmailEditText.setSelection(name.length());
            //Active le bouton Jouer
            mPlayButton.setEnabled(!name.isEmpty());
        }
    }
}
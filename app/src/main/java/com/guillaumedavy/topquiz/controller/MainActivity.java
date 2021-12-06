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
import com.guillaumedavy.topquiz.model.Score;
import com.guillaumedavy.topquiz.model.User;
import com.guillaumedavy.topquiz.model.database_helper.TopQuizDBHelper;

public class MainActivity extends AppCompatActivity {
    private static final int GAME_ACTIVITY_REQUEST_CODE = 42;
    private static final int CREATE_ACCOUNT_REQUEST_CODE = 43;
    private static final int SELECT_CATEGORY_REQUEST_CODE = 44;
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
        

        //Create default user and admin
        TopQuizDBHelper db = new TopQuizDBHelper(this);
        try{
            db.getWritableDatabase();
            db.createDefaultUsersIfNeeded();
            db.createDefaultCategoriesIfNeeded();
            db.createDefaultQuestionsIfNeeded();
        } catch (Exception e){
            throw e;
        } finally {
            db.close();
        }

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
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Appelée lorsqu'un clique est réalisé sur le button
             * @param v
             */
            @Override
            public void onClick(View v) {
                loginAction(mEmailEditText.getText().toString(), mPasswordEditText.getText().toString());
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
        /*
        * Rentre dans cette boucle au retour de l'activity CreateAccount
        */
        if(CREATE_ACCOUNT_REQUEST_CODE == requestCode && RESULT_OK == resultCode){
            // Fetch the Email from CreateAccountActivity
            mEmailEditText.setText(data.getParcelableExtra(CreateAccountActivity.EMAIL));
        }
    }


    /**
     * Action done when you click on login button
     * @param email
     * @param password
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loginAction(String email, String password){
        TopQuizDBHelper db = new TopQuizDBHelper(this);
        try {
            db.getWritableDatabase();
            //Essaye de se connecter
            db.tryLogIn(email, password);
            //Mémorise le nom et met le score par defaut
            mPlayer.setUserEmail(email);
            //Creer un Intent pour passer le joueur au select category activity
            Intent selectCategoryActivity = new Intent(MainActivity.this, SelectCategoryActivity.class);
            System.out.println(mPlayer);
            mPlayer.resetScore();
            selectCategoryActivity.putExtra(SelectCategoryActivity.USER, mPlayer);
            startActivityForResult(selectCategoryActivity, SELECT_CATEGORY_REQUEST_CODE);
        } catch (SQLException e){
            mErrorTextView.setText(e.getMessage());
        } finally {
            db.close();
        }
    }
}
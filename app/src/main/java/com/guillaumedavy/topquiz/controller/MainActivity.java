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
import com.guillaumedavy.topquiz.model.Player;
import com.guillaumedavy.topquiz.model.database_helper.TopQuizDBHelper;

public class MainActivity extends AppCompatActivity {
    private static final int CREATE_ACCOUNT_REQUEST_CODE = 43;
    private static final int SELECT_CATEGORY_REQUEST_CODE = 44;
    private static final String PLAYER = "PLAYER";
    private static final String EMAIL = "EMAIL";

    //Les élèments de la vue
    private TextView mGreetingTextView;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;
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
        mLoginButton = findViewById(R.id.main_button_login);
        mLoginButton.setEnabled(false); //Button désactivé
        mCreateAccount = findViewById(R.id.create_account);

        //Met les données de base dans la DB, Users, Catégories, et Questions pour la démo
        putDefaultDataForDemoInDB(); // TODO remove if not démo

        //Listener sur le TextEdit email
        mEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                //Active le button si il y a du text dans le TextEdit
                mLoginButton.setEnabled(!s.toString().isEmpty());
            }
        });

        //Listener sur le click du button
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginActionAndChangeActivity(mEmailEditText.getText().toString(), mPasswordEditText.getText().toString());
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

        // Rentre dans cette condition au retour de l'activity CreateAccount
        if(CREATE_ACCOUNT_REQUEST_CODE == requestCode && RESULT_OK == resultCode){
            mEmailEditText.setText(data.getParcelableExtra(EMAIL));
        }
    }

    /**
     * Permet de vérifier le login, si c'est ok, passe à SelectCategoryActivity
     * sinon affiche un message d'erreur
     * @param email : l'email saisi dans le TextEdit
     * @param password : le password saisi dans le TextEdit
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loginActionAndChangeActivity(String email, String password){
        TopQuizDBHelper db = new TopQuizDBHelper(this);
        try {
            db.getWritableDatabase();
            db.tryLogIn(email, password); //Peut throw si password incorrect ou email incorrect
            goToSelectCategoryActivity(email);
        } catch (SQLException e){
            mErrorTextView.setText(e.getMessage());
        } finally {
            db.close();
        }
    }

    /**
     * Navigue vers l'activité de choix de categorie pour jouer.
     * @param email email du champs de connexion
     */
    private void goToSelectCategoryActivity(String email){
        mPlayer.setUserEmail(email);
        mPlayer.resetScore();
        Intent selectCategoryActivity = new Intent(MainActivity.this, SelectCategoryActivity.class);
        selectCategoryActivity.putExtra(PLAYER, mPlayer);
        startActivityForResult(selectCategoryActivity, SELECT_CATEGORY_REQUEST_CODE);
    }

    /**
     * Met les données de base pour la démo en DB
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void putDefaultDataForDemoInDB(){
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
    }
}
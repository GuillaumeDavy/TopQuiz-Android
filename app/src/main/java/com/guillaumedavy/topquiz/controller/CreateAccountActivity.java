package com.guillaumedavy.topquiz.controller;

import android.content.Intent;
import android.database.SQLException;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.guillaumedavy.topquiz.R;
import com.guillaumedavy.topquiz.model.User;
import com.guillaumedavy.topquiz.model.database_helper.TopQuizDBHelper;

import java.util.Objects;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String EMAIL = "EMAIL";

    // View elements
    private TextView mUsername;
    private TextView mEmail;
    private TextView mPassword;
    private TextView mPasswordConfirm;
    private TextView mErrorTextView;
    private Button mButtonCreate;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account); //Permet de déterminer quel fichier layout utiliser

        //Retour a la home
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mUsername = findViewById(R.id.editTextUsernameCreateAccount);
        mEmail = findViewById(R.id.editTextEmailCreateAccount);
        mPassword = findViewById(R.id.editTextPasswordCreateAccount);
        mPasswordConfirm = findViewById(R.id.editTextConfirmPasswordCreateAccount);
        mErrorTextView = findViewById(R.id.errorTextView);
        mButtonCreate = findViewById(R.id.buttonCreateAccount);
        mButtonCreate.setEnabled(false);

        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}


            @Override
            public void afterTextChanged(Editable s) {
            //Active le button si il y a du text dans le TextEdit
                if(checkAccountPassword()){
                    mButtonCreate.setEnabled(true);
                }else{
                    mButtonCreate.setEnabled(false);
                }
            }
        });

        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(checkAccountPassword()){
                    mButtonCreate.setEnabled(true);
                }else{
                    mButtonCreate.setEnabled(false);
                }
            }
        });
        mPasswordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                //Active le button si il y a du text dans le TextEdit
                if(checkAccountPassword()){
                    mButtonCreate.setEnabled(true);
                }else{
                    mButtonCreate.setEnabled(false);
                }
            }
        });
        mButtonCreate.setOnClickListener(new View.OnClickListener() {
            /**
             * Appelée lorsqu'un clique est réalisé sur le button de creation
             * @param v
             */
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Intent MainActivity = new Intent(CreateAccountActivity.this, MainActivity.class);
                try {
                    createUserAndAddInDB();
                    MainActivity.putExtra(EMAIL, mEmail.getText().toString());
                    setResult(RESULT_OK, MainActivity);
                    startActivity(MainActivity);
                    finish();
                } catch (SQLException e) {
                    mErrorTextView.setText(e.getMessage());
                }
            }
        });
    }

    /**
     * Créer l'utilisateur et l'enregistre en base de donnée
     * @return l'email de l'utilisateur créé
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String createUserAndAddInDB(){
        TopQuizDBHelper db = new TopQuizDBHelper(this);
        db.getWritableDatabase();
        User user = new User(
                mEmail.getText().toString(),
                checkUserName(mUsername.getText().toString()),
                mPassword.getText().toString(),
                false
        );
        db.addUser(user);
        return user.getEmail();
    }

    /**
     * Permet le retour à la page d'accueil
     * @param item
     * @return true
     */
    public boolean onOptionsItemSelected(MenuItem item){
        startActivityForResult(new Intent(getApplicationContext(), MainActivity.class), 0);
        return true;
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * Vérifie que l'email est renseigné avec un @
     * Que les champs de password sont identiques et renseignés
     * @return si c'est ok
     */
    private boolean checkAccountPassword(){
        return mEmail.getText().toString().contains("@")
                && mPassword.getText().toString().equals(mPasswordConfirm.getText().toString())
                && mPassword != null
                && mPasswordConfirm != null;
    }

    /**
     * Vérifie que dans le username il y a quelque chose
     * si rien n'est renseigné le username devient la partie
     * avant le @ de l'adresse email
     * @param maybeUsername le contenu du champ mUsername
     * @return le username
     */
    private String checkUserName(String maybeUsername){
        return maybeUsername.isEmpty() ? mEmail.getText().toString().split("@")[0] : maybeUsername;
    }
}

package com.guillaumedavy.topquiz.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.guillaumedavy.topquiz.R;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String EMAIL = "EMAIL";

    // View elements
    private TextView mEmail;
    private Button mButton;
    private TextView mPassword;
    private TextView mPasswordConfirm;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account); //Permet de déterminer quel fichier layout utiliser

        mEmail = findViewById(R.id.editTextUsernameCreateAccount);
        mButton = findViewById(R.id.buttonCreateAccount);
        mPassword = findViewById(R.id.editTextPasswordCreateAccount);
        mPasswordConfirm = findViewById(R.id.editTextConfirmPasswordCreateAccount);
        mButton.setEnabled(false);

        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}


            @Override
            public void afterTextChanged(Editable s) {
            //Active le button si il y a du text dans le TextEdit
                if(checkAccountPassword()){
                    mButton.setEnabled(true);
                }else{
                    mButton.setEnabled(false);
                }
            }
        });

        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(checkAccountPassword()){
                    mButton.setEnabled(true);
                }else{
                    mButton.setEnabled(false);
                }
            }
        });
        mPasswordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//Active le button si il y a du text dans le TextEdit
                if(checkAccountPassword()){
                    mButton.setEnabled(true);
                }else{
                    mButton.setEnabled(false);
                }
            }
        });
        mButton.setOnClickListener(new View.OnClickListener() {
            /**
             * TODO Enregistrer le USER en base
             * Appelée lorsqu'un clique est réalisé sur le button
             * @param v
             */
            @Override
            public void onClick(View v) {
                Intent MainActivity = new Intent(CreateAccountActivity.this, MainActivity.class);
                MainActivity.putExtra(EMAIL, String.valueOf(mEmail));
                setResult(RESULT_OK, MainActivity);

                startActivity(MainActivity);
                finish();

            }
        });




    }
    public boolean checkAccountPassword(){
        //Active le button si il y a du text dans le TextEdit
        return (mEmail.getText().toString().contains("@") && mPassword.getText().toString().equals(mPasswordConfirm.getText().toString()) && mPassword != null && mPasswordConfirm != null);
    }
    @Override
    public void onClick(View v) {

    }
}

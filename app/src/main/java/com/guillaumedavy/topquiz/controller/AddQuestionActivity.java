package com.guillaumedavy.topquiz.controller;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.guillaumedavy.topquiz.R;
import com.guillaumedavy.topquiz.model.database_helper.TopQuizDBHelper;

public class AddQuestionActivity extends AppCompatActivity {
    public static final String CATEGORY = "CATEGORY";
    private static final int SELECT_CATEGORY_REQUEST_CODE = 44;

    //view elements
    private TextView mChoosedCategory;
    private EditText mNewQuestion;
    private EditText mNewAnswer1;
    private EditText mNewAnswer2;
    private EditText mNewAnswer3;
    private EditText mNewAnswer4;
    private Spinner mAnswerIndex;
    private Button mInsertNewQuestion;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_question);

        Intent intent = getIntent();
        if(intent.hasExtra(CATEGORY)){
            mChoosedCategory.setText(intent.getStringExtra(CATEGORY));
        }
        mChoosedCategory = findViewById(R.id.textViewChoosedCategory);
        mNewQuestion = findViewById(R.id.editTextNewQuestion);
        mNewAnswer1 = findViewById(R.id.editTextNewAnswer1 );
        mNewAnswer2 = findViewById(R.id.editTextNewAnswer2 );
        mNewAnswer3 = findViewById(R.id.editTextNewAnswer3 );
        mNewAnswer4 = findViewById(R.id.editTextNewAnswer4 );
        mAnswerIndex = findViewById(R.id.spinnerGoodAnswer);
        mInsertNewQuestion = findViewById(R.id.buttonValidateNewQuestion);

        //set spinner
        String[] arrayAnswer = {"1","2","3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, arrayAnswer);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAnswerIndex.setAdapter(adapter);

        TopQuizDBHelper db = new TopQuizDBHelper(this);
        try {
            db.getWritableDatabase();
        }catch (Exception e){
            throw e;
        }finally {
            db.close();
        }
        mInsertNewQuestion.setOnClickListener(new View.OnClickListener() {
            /**
             * Appelée lorsqu'un clique est réalisé sur le button d'insertion de la question
             * @param v
             */
            @Override
            public void onClick(View v) {
                //on verifie si tous les champs sont remplis
                if(checkDatas()){
                    Intent SelectCategoryActivity = new Intent(AddQuestionActivity.this, SelectCategoryActivity.class);

                    //TODO faire la requete d'insertion dans la bdd
                    mAnswerIndex.getSelectedItem().toString();
                    startActivityForResult(SelectCategoryActivity, SELECT_CATEGORY_REQUEST_CODE);
                }else{
                    System.out.println("tous les champs pas remplis");
                }
            }
        });
    }

    private boolean checkDatas(){
        if(mNewQuestion.getText().toString().isEmpty() ||
            mNewAnswer1.getText().toString().isEmpty() ||
            mNewAnswer2.getText().toString().isEmpty() ||
            mNewAnswer3.getText().toString().isEmpty() ||
            mNewAnswer4.getText().toString().isEmpty() ||
            mAnswerIndex.getSelectedItem().toString().isEmpty()){
            return false;
        }else{
            return true;
        }
    }
}

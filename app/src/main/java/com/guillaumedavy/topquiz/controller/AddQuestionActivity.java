package com.guillaumedavy.topquiz.controller;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.guillaumedavy.topquiz.App;
import com.guillaumedavy.topquiz.R;
import com.guillaumedavy.topquiz.model.Question;
import com.guillaumedavy.topquiz.model.database_helper.TopQuizDBHelper;
import com.guillaumedavy.topquiz.model.database_helper.utils.QuestionScript;

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

    //Field
    private String mCategoryName;
    private int correctAnswerId;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_question);

        mNewQuestion = findViewById(R.id.editTextNewQuestion);
        mNewAnswer1 = findViewById(R.id.editTextNewAnswer1 );
        mNewAnswer2 = findViewById(R.id.editTextNewAnswer2 );
        mNewAnswer3 = findViewById(R.id.editTextNewAnswer3 );
        mNewAnswer4 = findViewById(R.id.editTextNewAnswer4 );
        mAnswerIndex = findViewById(R.id.spinnerGoodAnswer);
        mInsertNewQuestion = findViewById(R.id.buttonValidateNewQuestion);
        mInsertNewQuestion.setEnabled(false);

        Intent intent = getIntent();
        if(intent.hasExtra(CATEGORY)){
            mCategoryName = intent.getStringExtra(CATEGORY);
            mChoosedCategory.setText(mCategoryName);
        }

        //set spinner
        String[] arrayAnswer = {getString(R.string.new_answer_1),getString(R.string.new_answer_2),getString(R.string.new_answer_3),getString(R.string.new_answer_4)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, arrayAnswer);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAnswerIndex.setAdapter(adapter);

        mNewQuestion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                //Active le button si il y a du text dans le TextEdit
                if(checkDatas()){
                    mInsertNewQuestion.setEnabled(true);
                }else{
                    mInsertNewQuestion.setEnabled(false);
                }
            }
        });

        mNewAnswer1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                //Active le button si il y a du text dans le TextEdit
                if(checkDatas()){
                    mInsertNewQuestion.setEnabled(true);
                }else{
                    mInsertNewQuestion.setEnabled(false);
                }
            }
        });

        mNewAnswer2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                //Active le button si il y a du text dans le TextEdit
                if(checkDatas()){
                    mInsertNewQuestion.setEnabled(true);
                }else{
                    mInsertNewQuestion.setEnabled(false);
                }
            }
        });

        mNewAnswer3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                //Active le button si il y a du text dans le TextEdit
                if(checkDatas()){
                    mInsertNewQuestion.setEnabled(true);
                }else{
                    mInsertNewQuestion.setEnabled(false);
                }
            }
        });

        mNewAnswer4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                //Active le button si il y a du text dans le TextEdit
                if(checkDatas()){
                    mInsertNewQuestion.setEnabled(true);
                }else{
                    mInsertNewQuestion.setEnabled(false);
                }
            }
        });

        mAnswerIndex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                correctAnswerId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mInsertNewQuestion.setOnClickListener(new View.OnClickListener() {
            /**
             * Appelée lorsqu'un clique est réalisé sur le button d'insertion de la question
             * @param v
             */
            @Override
            public void onClick(View v) {
                //on verifie si tous les champs sont remplis
                Intent SelectCategoryActivity = new Intent(AddQuestionActivity.this, SelectCategoryActivity.class);
                addQuestionInDB();
                startActivityForResult(SelectCategoryActivity, SELECT_CATEGORY_REQUEST_CODE);
            }
        });
    }

    private void addQuestionInDB(){
        TopQuizDBHelper db = new TopQuizDBHelper(this);
        try {
            db.getWritableDatabase();
            long id = db.getMaxQuestionId();
            db.addQuestion(new Question(
                    ++id,
                    db.getCategoryByName(mCategoryName),
                    mNewQuestion.getText().toString(),
                    mNewAnswer1.getText().toString(),
                    mNewAnswer2.getText().toString(),
                    mNewAnswer3.getText().toString(),
                    mNewAnswer4.getText().toString(),
                    correctAnswerId
            ));
        }catch (Exception e){
            throw e;
        }finally {
            db.close();
        }
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

package com.guillaumedavy.topquiz.controller;

import android.content.Intent;
import android.database.SQLException;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
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
import com.guillaumedavy.topquiz.model.Category;
import com.guillaumedavy.topquiz.model.Question;
import com.guillaumedavy.topquiz.model.database_helper.TopQuizDBHelper;
import com.guillaumedavy.topquiz.model.database_helper.utils.QuestionScript;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AddQuestionActivity extends AppCompatActivity{
    private static final int SELECT_CATEGORY_REQUEST_CODE = 44;

    //view elements
    private EditText mNewQuestion;
    private Spinner mCategoriesSpinner;
    private EditText mNewAnswer1;
    private EditText mNewAnswer2;
    private EditText mNewAnswer3;
    private EditText mNewAnswer4;
    private Spinner mAnswerIndexSpinner;
    private Button mInsertNewQuestion;

    //Field
    private int correctAnswerId;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_question);

        mNewQuestion = findViewById(R.id.editTextNewQuestion);
        mCategoriesSpinner = findViewById(R.id.spinnerSelectCategoryAddQuestionView);
        mNewAnswer1 = findViewById(R.id.editTextNewAnswer1 );
        mNewAnswer2 = findViewById(R.id.editTextNewAnswer2 );
        mNewAnswer3 = findViewById(R.id.editTextNewAnswer3 );
        mNewAnswer4 = findViewById(R.id.editTextNewAnswer4 );
        mAnswerIndexSpinner = findViewById(R.id.spinnerGoodAnswer);
        mInsertNewQuestion = findViewById(R.id.buttonValidateNewQuestion);
        mInsertNewQuestion.setEnabled(false);

        //Retour au choix de catégorie
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //Set Spinner for category
        setCategoriesSpinner();

        //set spinner for correct answer
        updateCorrectAnswerSpinner();

        mNewQuestion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                //Active le button si il y a du text dans le TextEdit
                mInsertNewQuestion.setEnabled(checkDatas());
            }
        });

        mNewAnswer1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateCorrectAnswerSpinner();
                //Active le button si il y a du text dans le TextEdit
                mInsertNewQuestion.setEnabled(checkDatas());
            }
        });

        mNewAnswer2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateCorrectAnswerSpinner();
                //Active le button si il y a du text dans le TextEdit
                mInsertNewQuestion.setEnabled(checkDatas());
            }
        });

        mNewAnswer3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateCorrectAnswerSpinner();
                //Active le button si il y a du text dans le TextEdit
                mInsertNewQuestion.setEnabled(checkDatas());
            }
        });

        mNewAnswer4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateCorrectAnswerSpinner();
                //Active le button si il y a du text dans le TextEdit
                mInsertNewQuestion.setEnabled(checkDatas());
            }
        });

        mAnswerIndexSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                correctAnswerId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        mInsertNewQuestion.setOnClickListener(new View.OnClickListener() {
            /**
             * Appelée lorsqu'un clique est réalisé sur le button d'insertion de la question
             * @param v : la view
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

    /**
     * Permet le retour à la page de choix de catégorie
     * @param item : le bouton de retour a la home
     * @return true
     */
    public boolean onOptionsItemSelected(MenuItem item){
        startActivityForResult(new Intent(getApplicationContext(), SelectCategoryActivity.class), 0);
        return true;
    }

    /**
     * Ajoute la question en base de donnée à partir des données presentes dans la vue.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addQuestionInDB(){
        try (TopQuizDBHelper db = new TopQuizDBHelper(this)) {
            db.getWritableDatabase();
            long id = db.getMaxQuestionId();
            db.addQuestion(new Question(
                    ++id,
                    db.getCategoryByName(mCategoriesSpinner.getSelectedItem().toString()),
                    mNewQuestion.getText().toString(),
                    mNewAnswer1.getText().toString(),
                    mNewAnswer2.getText().toString(),
                    mNewAnswer3.getText().toString(),
                    mNewAnswer4.getText().toString(),
                    correctAnswerId
            ));
            db.getAllQuestions().forEach(System.out::println);
        }
    }

    /**
     * Met a jour le spinner de choix de bonne réponse avec
     * les valeurs des réponses des TextFields de réponses.
     */
    private void updateCorrectAnswerSpinner(){
        String[] arrayAnswer = {
                mNewAnswer1.getText().toString(),
                mNewAnswer2.getText().toString(),
                mNewAnswer3.getText().toString(),
                mNewAnswer4.getText().toString()
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, arrayAnswer);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAnswerIndexSpinner.setAdapter(adapter);
    }

    /**
     * Récupère les catégories en base de donnée et les mets
     * dans le Spinner de choix de catégories
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setCategoriesSpinner(){
        try (TopQuizDBHelper db = new TopQuizDBHelper(this)) {
            //Conversion List<Category> vers Array<String> en recuperant le nom de chaque category
            String[] allCategories = db.getAllCategories()
                    .stream()
                    .map(Category::getName)
                    .toArray(String[]::new);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, allCategories);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mCategoriesSpinner.setAdapter(adapter);

        }
    }

    /**
     * Vérifie qu'il y a du contenu dans tous les TextFields
     * @return true si pas vide
     */
    private boolean checkDatas(){
        return !mNewQuestion.getText().toString().isEmpty() &&
                !mNewAnswer1.getText().toString().isEmpty() &&
                !mNewAnswer2.getText().toString().isEmpty() &&
                !mNewAnswer3.getText().toString().isEmpty() &&
                !mNewAnswer4.getText().toString().isEmpty() &&
                !mAnswerIndexSpinner.getSelectedItem().toString().isEmpty();
    }
}

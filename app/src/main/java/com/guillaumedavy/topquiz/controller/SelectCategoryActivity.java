package com.guillaumedavy.topquiz.controller;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.guillaumedavy.topquiz.R;
import com.guillaumedavy.topquiz.model.Category;
import com.guillaumedavy.topquiz.model.Player;
import com.guillaumedavy.topquiz.model.User;
import com.guillaumedavy.topquiz.model.database_helper.TopQuizDBHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class SelectCategoryActivity extends AppCompatActivity {
    public static final String USER = "USER";

    // View elements
    private Spinner mSpinnerCategories;
    private Button mButtonPlay;
    private Button mButtonQuestion;

    //Fields
    private Player mPlayer = new Player();
    private List<String> mCategoryList = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_category);

        //Retour a la home
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true); //TODO gerer le retour HOME

        Intent intent = getIntent();
        if(intent.hasExtra(USER)){
            mPlayer = intent.getParcelableExtra(USER);
            System.out.println("Select category for player " + mPlayer);
        }

        mSpinnerCategories = findViewById(R.id.spinnerSelectCategory);
        mButtonPlay = findViewById(R.id.buttonPlaySelectCategory);
        mButtonQuestion = findViewById(R.id.buttonAddQuestionSelectCategory);

        //N'affiche pas le bouton d'ajout de question si pas admin + recupère les catégories
        TopQuizDBHelper db = new TopQuizDBHelper(this);
        try {
            db.getWritableDatabase();
            Optional<User> maybeUser = db.getUserByEmail(mPlayer.getUserEmail());
            if(maybeUser.isPresent() && !maybeUser.get().isAdmin()){
                mButtonQuestion.setVisibility(View.GONE);
            }
            //Convertion List<Category> vers List<String> en recuperant le nom de chaque category
            mCategoryList = db.getAllCategories()
                    .stream()
                    .map(Category::getName)
                    .collect(Collectors.toList());
        } catch (Exception e){
            throw e;
        } finally {
            db.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, mCategoryList.toArray(new String[0]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCategories.setAdapter(adapter);

    }
}

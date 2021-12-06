package com.guillaumedavy.topquiz.controller;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

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
    private static final int GAME_ACTIVITY_REQUEST_CODE = 42;
    public static final String USER = "USER";
    public static final String CATEGORY = "CATEGORY";

    // View elements
    private Spinner mSpinnerCategories;
    private Button mButtonPlay;
    private Button mButtonQuestion;
    private TextView mTextViewScoreMessage;

    //Fields
    private Player mPlayer = new Player();
    private List<String> mCategoryList = new ArrayList<>();


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //quand on revient de GameActivity
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
            String text = getString(R.string.welcome_back_label_score) + " " + score;
            mTextViewScoreMessage.setText(text);
        }
    }

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

        mSpinnerCategories = findViewById(R.id.spinnerSelectCategory);//dropdown
        mButtonPlay = findViewById(R.id.buttonPlaySelectCategory);
        mButtonQuestion = findViewById(R.id.buttonAddQuestionSelectCategory);
        mTextViewScoreMessage = findViewById(R.id.textview_ScoreMessage);

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

        mButtonPlay.setOnClickListener(new View.OnClickListener() {
            /**
             * Appelée lorsqu'un clique est réalisé sur le button play
             * @param v
             */
            @Override
            public void onClick(View v) {
                Intent GameActivity = new Intent(SelectCategoryActivity.this, GameActivity.class);

                GameActivity.putExtra(CATEGORY, mSpinnerCategories.getSelectedItem().toString());//on lui donne la categorie choisie
                GameActivity.putExtra(USER, mPlayer);//on lui donne le user
                //System.out.println("catégorie choisie :  " + mSpinnerCategories.getSelectedItem().toString());
                startActivityForResult(GameActivity, GAME_ACTIVITY_REQUEST_CODE);
            }
        });


    }
}

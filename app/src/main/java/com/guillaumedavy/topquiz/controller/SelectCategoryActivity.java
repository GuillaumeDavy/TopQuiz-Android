package com.guillaumedavy.topquiz.controller;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.guillaumedavy.topquiz.R;
import com.guillaumedavy.topquiz.model.Category;
import com.guillaumedavy.topquiz.model.Player;
import com.guillaumedavy.topquiz.model.Score;
import com.guillaumedavy.topquiz.model.User;
import com.guillaumedavy.topquiz.model.database_helper.TopQuizDBHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SelectCategoryActivity extends AppCompatActivity {
    private static final int GAME_ACTIVITY_REQUEST_CODE = 42;
    private static final int SELECT_CATEGORY_REQUEST_CODE = 44;
    private static final int ADD_QUESTION_REQUEST_CODE = 45;
    public static final String PLAYER = "PLAYER";
    public static final String CATEGORY = "CATEGORY";

    // View elements
    private Spinner mSpinnerCategories;
    private Button mButtonPlay;
    private Button mButtonQuestion;
    private ListView mLeaderBoard;
    private TextView mTextViewLeaderboardEmpty;
    private TextView mTextViewLeaderboardTitle;
    private TextView mYourScoreTextView;

    //Fields
    private Player mPlayer = new Player();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_category);

        mSpinnerCategories = findViewById(R.id.spinnerSelectCategory);//dropdown
        mLeaderBoard = findViewById(R.id.leaderboard_list_view); //Listview
        mButtonPlay = findViewById(R.id.buttonPlaySelectCategory);
        mButtonQuestion = findViewById(R.id.buttonAddQuestionSelectCategory);
        mTextViewLeaderboardTitle = findViewById(R.id.leaderboard_text_view);
        mTextViewLeaderboardEmpty = findViewById(R.id.leaderboard_empty_text_view);
        mYourScoreTextView = findViewById(R.id.your_best_score_text_view);
        mYourScoreTextView.setText(getString(R.string.no_score_yet));

        //Retour a la home
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //Récupère les donnees de l'utilisateur, email + score
        Intent intent = getIntent();
        if(intent.hasExtra(PLAYER)){
            mPlayer = intent.getParcelableExtra(PLAYER);
        }

        //N'affiche pas le bouton d'ajout de question si pas admin + recupère les catégories
        try (TopQuizDBHelper db = new TopQuizDBHelper(this)) {
            db.getWritableDatabase();
            Optional<User> maybeUser = db.getUserByEmail(mPlayer.getUserEmail());

            //Si le joueur n'est pas admin, on n'affiche pas le bouton de creation de question
            if (maybeUser.isPresent() && !maybeUser.get().isAdmin()) {
                mButtonQuestion.setVisibility(View.GONE);
            }

            //Conversion List<Category> vers Array<String> en recuperant le nom de chaque category
            String[] categoriesArray = db.getAllCategories()
                    .stream()
                    .map(Category::getName)
                    .toArray(String[]::new);

            //On met les categories dans le Spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoriesArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinnerCategories.setAdapter(adapter);

            //Listener sur le changement de valeur dans le spinner
            mSpinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //Met à jour le leaderboard et le record personnel pour la categorie choisie
                    String categorySelected = mSpinnerCategories.getSelectedItem().toString();
                    maybeUser.ifPresent(user -> updatePersonalBestScore(user, categorySelected));
                    updateLeaderboard(categorySelected);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            //Listener sur le bouton de jeu qui lance la GameActivity
            mButtonPlay.setOnClickListener(new View.OnClickListener() {
                /**
                 * Appelée lorsqu'un clique est réalisé sur le button play
                 * @param v : la view
                 */
                @Override
                public void onClick(View v) {
                    Intent gameActivity = new Intent(SelectCategoryActivity.this, GameActivity.class);

                    gameActivity.putExtra(CATEGORY, mSpinnerCategories.getSelectedItem().toString());//on lui donne la categorie choisie
                    gameActivity.putExtra(PLAYER, mPlayer);//on lui donne le player
                    startActivityForResult(gameActivity, GAME_ACTIVITY_REQUEST_CODE);
                }
            });

            //Listener sur le bouton de question qui lance la QuestionActivity
            mButtonQuestion.setOnClickListener(new View.OnClickListener() {
                /**
                 * Appelée lorsqu'un clique est réalisé sur le button question
                 * @param v : la view
                 */
                @Override
                public void onClick(View v) {
                    Intent questionActivity = new Intent(SelectCategoryActivity.this, AddQuestionActivity.class);
                    startActivityForResult(questionActivity, ADD_QUESTION_REQUEST_CODE);
                }
            });
        }
    }

    /**
     * Permet le retour à la page d'accueil
     * @param item : le bouton retour
     * @return true
     */
    public boolean onOptionsItemSelected(MenuItem item){
        startActivityForResult(new Intent(getApplicationContext(), MainActivity.class), 0);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Quand on revient de GameActivity
        if(GAME_ACTIVITY_REQUEST_CODE == requestCode && RESULT_OK == resultCode){
            //Récupère le score de l'Intent
            mPlayer = Objects.requireNonNull(data).getParcelableExtra(GameActivity.PLAYER);
            //Met à Leaderboard et record personnel
            try (TopQuizDBHelper db = new TopQuizDBHelper(this)) {
                Optional<User> maybeUser = db.getUserByEmail(mPlayer.getUserEmail());
                String categorySelected = mSpinnerCategories.getSelectedItem().toString();
                maybeUser.ifPresent(user -> updatePersonalBestScore(user, categorySelected));
                updateLeaderboard(categorySelected);
            }
        }
    }


    /**
     * Met à jour le texte pour le meilleur score du joueur sur la catégorie choisie.
     * s'il a un score différent de 0, il lui affiche son meilleur score,
     * sinon il affiche pas de score
     * @param user : l'utilisateur connecté
     * @param categorySelected : la category choisie
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updatePersonalBestScore(User user, String categorySelected){
        try (TopQuizDBHelper db = new TopQuizDBHelper(this)) {
            Score myBestScore = db.getScoreByUserEmailAndCategoryId(user.getEmail(), db.getCategoryByName(categorySelected).getId());
            if (myBestScore.getScore() != 0) {
                String text = getString(R.string.your_best_score) + " " + myBestScore.getScore() + " " + getString(R.string.points);
                mYourScoreTextView.setText(text);
            } else {
                mYourScoreTextView.setText(getString(R.string.no_score_yet));
            }
        }
    }

    /**
     * Met à jour le leaderboard en affichant les 3 meilleurs joueurs de la categorie choisie
     * si le leaderboard est vide, il n'affiche qu'aucuns joueurs n'a joué.
     * @param categorySelected : la catégory selectionnée
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateLeaderboard(String categorySelected){
        try (TopQuizDBHelper db = new TopQuizDBHelper(this)) {
            List<String> leaderboardList = db.getTop3ScoreByCategoryId(db.getCategoryByName(categorySelected).getId())
                    .stream()
                    .filter(score -> score.getScore() != 0)
                    .map(score -> new Player(score.getUser().getUsername(), score.getScore()).toString())
                    .collect(Collectors.toList());

            if (leaderboardList.isEmpty()) {
                mLeaderBoard.setVisibility(View.GONE);
                mTextViewLeaderboardTitle.setVisibility(View.GONE);
                mTextViewLeaderboardEmpty.setVisibility(View.VISIBLE);
            } else {
                mLeaderBoard.setVisibility(View.VISIBLE);
                mTextViewLeaderboardTitle.setVisibility(View.VISIBLE);
                mTextViewLeaderboardEmpty.setVisibility(View.GONE);
                setItemsInLeaderboard(leaderboardList);
            }
        }
    }

    /**
     * Ajoute les joueurs au classement
     * @param leaderboardList : les 3 meilleurs de la catégory
     */
    private void setItemsInLeaderboard(List<String> leaderboardList){
        String[] leaderboardArray = new String[leaderboardList.size()];
        leaderboardList.toArray(leaderboardArray);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, leaderboardArray);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        mLeaderBoard.setAdapter(adapter);
    }
}

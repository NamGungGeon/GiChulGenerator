package com.satisfactoryplace.gichul.gichulgenerator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.satisfactoryplace.gichul.gichulgenerator.fragment.SearchResultTryFragment;

/**
 * Created by WINDOWS7 on 2018-02-09.
 */

public class SearchResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide ActionBar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_searchresult);
        getSupportFragmentManager().beginTransaction().replace(R.id.searchResultContainer, new SearchResultTryFragment()).commit();
    }
}
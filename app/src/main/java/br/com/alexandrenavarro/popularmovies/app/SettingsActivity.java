package br.com.alexandrenavarro.popularmovies.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by alexandrenavarro on 20/12/16.
 */

public class SettingsActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setResult(RESULT_OK);
    }
}
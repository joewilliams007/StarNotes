package com.sport.starnotes.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sport.starnotes.Account;
import com.sport.starnotes.R;

public class ThemeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!Account.theme().equals("normal")) {
            if (Account.theme().equals("purple")) {
                setTheme(R.style.Theme_Purple);
            } else if (Account.theme().equals("blue")) {
                setTheme(R.style.Theme_Blue);
            } else if (Account.theme().equals("amoled")) {
                setTheme(R.style.Theme_Amoled);
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
    }

    public void themeBlue(View view) {
        applyTheme("blue");
    }

    private void applyTheme(String theme) {
        Account.setTheme(theme);
        Intent i = new Intent(this, ThemeActivity.class);
        startActivity(i);

        finish();
    }

    public void themePurple(View view) {
        applyTheme("purple");
    }

    public void themeAmoled(View view) {
        applyTheme("amoled");
    }

    public void themeNormal(View view) {
        Account.setTheme("normal");

        Intent i = new Intent(this, ThemeActivity.class);
        startActivity(i);

        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
        finish();
    }
}
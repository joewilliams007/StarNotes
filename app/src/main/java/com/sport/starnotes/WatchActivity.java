package com.sport.starnotes;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class WatchActivity extends AppCompatActivity {
    CheckBox checkBox;
    @SuppressLint("MissingInflatedId")
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
        setContentView(R.layout.activity_watch);

        checkBox = findViewById(R.id.checkBox);
        checkBox.setChecked(Account.syncWatch());

    }

    public void checkSync(View view) {
        Account.setSyncWatch(!Account.syncWatch());

        checkBox.setChecked(Account.syncWatch());
    }
}